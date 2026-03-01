package com.creditapp.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Camunda BPM Engine konfigürasyonu
 *
 * Bu configuration class Camunda engine'in davranışını özelleştirir:
 * - Custom process engine plugins
 * - History cleanup configuration
 * - Authorization settings
 * - Job executor customization
 */
@Configuration
@Order(Ordering.DEFAULT_ORDER + 1)
public class CamundaConfiguration extends AbstractCamundaConfiguration {

    @Override
    public void preInit(SpringProcessEngineConfiguration configuration) {
        // History cleanup konfigürasyonu
        configuration.setHistoryCleanupBatchWindowStartTime("01:00");
        configuration.setHistoryCleanupBatchWindowEndTime("05:00");
        configuration.setHistoryCleanupBatchSize(500);

        // Job executor konfigürasyonu
        configuration.setJobExecutorActivate(true);
        configuration.setJobExecutorDeploymentAware(true);

        // Failed jobs için retry time cycle
        configuration.setFailedJobRetryTimeCycle("R3/PT5M");

        // DMN konfigürasyonu
        configuration.setDmnEnabled(true);

        // History Time To Live (TTL) konfigürasyonu
        // Camunda 7.21+ için zorunludur, aksi halde deployment hata verir.
        configuration.setHistoryTimeToLive("P180D");
        configuration.setEnforceHistoryTimeToLive(false);

        // BPMN parse listeners
        // Burada custom parse listener'lar eklenebilir

        // schema-update application.yml'dan okunur (varsayılan false; init-camunda
        // profilde true).
        configuration.setSkipHistoryOptimisticLockingExceptions(true);

        super.preInit(configuration);
    }

    @Override
    public void postInit(SpringProcessEngineConfiguration configuration) {
        // preInit'te DB_SCHEMA_UPDATE_TRUE bırakılıyor; eksik Camunda tabloları
        // otomatik oluşturulur.
        // postInit'te FALSE yapmak kapatıyordu ve "Tables are missing" hatasına yol
        // açıyordu.
        super.postInit(configuration);
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // Process engine build edildikten sonra yapılacak işlemler
        // Örneğin: default user'lar oluşturma, example data loading, vb.

        super.postProcessEngineBuild(processEngine);
    }
}
