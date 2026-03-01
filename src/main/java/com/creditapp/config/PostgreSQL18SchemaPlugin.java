package com.creditapp.config;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * PostgreSQL 18 uyumluluk eklentisi.
 *
 * PostgreSQL 18, JDBC DatabaseMetaData.getTables() davranışını değiştirdi.
 * Camunda 7.21 bu API'yi tablo varlık kontrolü için kullanır ve PG18'de
 * yanlış sonuçlar alarak "already exists" veya "tables missing" hatalarına
 * neden olur.
 *
 * Bu eklenti sorunu şöyle çözer:
 * 1. Doğrudan SQL ile act_ge_property tablosunu arar (PG18'de güvenilir)
 * 2. Tablo varsa → schema operasyonlarını tamamen atlar
 * (DatabaseMetaData asla çağrılmaz)
 * 3. Tablo yoksa → normal schema-update: true ile ilk kurulum yapılır
 *
 * Camunda 7.23+ PostgreSQL 18'i resmi olarak destekler; bu eklenti
 * Camunda 7.23'e yükseltildiğinde kaldırılabilir.
 */
@Slf4j
@Component
public class PostgreSQL18SchemaPlugin implements ProcessEnginePlugin {

    private final DataSource dataSource;

    public PostgreSQL18SchemaPlugin(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private boolean camundaTablesExist = false;

    @Override
    public void preInit(ProcessEngineConfigurationImpl config) {
        camundaTablesExist = checkCamundaTablesExist();

        if (camundaTablesExist) {
            log.info("PG18 Eklentisi: Camunda tablolari mevcut — schema islemleri atlanacak.");
        } else {
            log.info("PG18 Eklentisi: Camunda tablolari bulunamadi — ilk kurulum yapilacak.");
            config.setDatabaseSchemaUpdate("true");
        }
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl config) {
        if (camundaTablesExist) {
            // postInit'te set ediyoruz ki Camunda'nın kendi init() metodu
            // tarafından üzerine yazılmasın
            config.setCommandExecutorSchemaOperations(new NoOpCommandExecutor());
            log.info("PG18 Eklentisi: Schema operations executor -> NoOp.");
        }
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // No-op
    }

    /**
     * Doğrudan SQL ile ACT_GE_PROPERTY tablosunun varlığını kontrol eder.
     * DatabaseMetaData kullanmaz, bu yüzden PG18'de güvenilirdir.
     */
    private boolean checkCamundaTablesExist() {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("SET search_path TO public");
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables " +
                            "WHERE table_schema = 'public' AND table_name = 'act_ge_property'");
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            log.warn("PG18 Eklentisi: Tablo kontrolu basarisiz: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Hiçbir şey yapmayan CommandExecutor.
     * Camunda'nın schema operasyonlarını tamamen atlar.
     */
    private static class NoOpCommandExecutor implements CommandExecutor {
        @Override
        public <T> T execute(Command<T> command) {
            // Schema operasyonlarını tamamen atla
            return null;
        }
    }
}
