package com.creditapp.listener;

import com.creditapp.model.CreditApplication;
import com.creditapp.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Uygulama başlangıcında 10 gerçekçi kredi başvurusu oluşturur.
 * Manager bu başvuruları görev listesinde görebilir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(100)
public class DataSeeder implements ApplicationRunner {

    private final CreditApplicationService applicationService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        long count = applicationService.findAll().stream()
                .filter(a -> a.getProcessInstanceId() != null)
                .count();
        if (count >= 10) {
            log.info("Zaten {} onay bekleyen başvuru mevcut, seed atlanıyor.", count);
            return;
        }

        int toCreate = 10 - (int) count;
        log.info("{} adet örnek başvuru oluşturuluyor...", toCreate);

        List<CreditApplication> applications = createStaticApplications();
        int created = 0;
        for (CreditApplication app : applications) {
            if (created >= toCreate) break;
            try {
                applicationService.createAndStartApplication(app);
                created++;
            } catch (Exception e) {
                log.warn("Başvuru oluşturulamadı: {}", e.getMessage());
            }
        }
        log.info("{} adet başvuru başarıyla oluşturuldu. Manager görev listesinde görebilir.", created);
    }

    public static List<CreditApplication> createStaticApplications() {
        return List.of(
                CreditApplication.builder()
                        .customerId("CUST-1001")
                        .customerName("Ahmet Yılmaz")
                        .nationalId("12345678901")
                        .age(35)
                        .email("ahmet.yilmaz@email.com")
                        .phone("5321112233")
                        .loanAmount(new BigDecimal("85000"))
                        .termMonths(24)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("25000"))
                        .employer("ABC Teknoloji A.Ş.")
                        .customerSegment(CreditApplication.CustomerSegment.GOLD)
                        .creditHistory(4)
                        .existingLoans(1)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1002")
                        .customerName("Ayşe Demir")
                        .nationalId("23456789012")
                        .age(28)
                        .email("ayse.demir@email.com")
                        .phone("5332223344")
                        .loanAmount(new BigDecimal("120000"))
                        .termMonths(36)
                        .employmentStatus(CreditApplication.EmploymentStatus.SELF_EMPLOYED)
                        .monthlyIncome(new BigDecimal("35000"))
                        .employer("Demir Mimarlık")
                        .customerSegment(CreditApplication.CustomerSegment.SILVER)
                        .creditHistory(5)
                        .existingLoans(0)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1003")
                        .customerName("Mehmet Kaya")
                        .nationalId("34567890123")
                        .age(42)
                        .email("mehmet.kaya@email.com")
                        .phone("5343334455")
                        .loanAmount(new BigDecimal("250000"))
                        .termMonths(48)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("45000"))
                        .employer("Kaya Holding")
                        .customerSegment(CreditApplication.CustomerSegment.PREMIUM)
                        .creditHistory(5)
                        .existingLoans(2)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1004")
                        .customerName("Fatma Öztürk")
                        .nationalId("45678901234")
                        .age(31)
                        .email("fatma.ozturk@email.com")
                        .phone("5354445566")
                        .loanAmount(new BigDecimal("45000"))
                        .termMonths(12)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("18000"))
                        .employer("XYZ Perakende Ltd.")
                        .customerSegment(CreditApplication.CustomerSegment.BRONZE)
                        .creditHistory(3)
                        .existingLoans(0)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1005")
                        .customerName("Ali Çelik")
                        .nationalId("56789012345")
                        .age(38)
                        .email("ali.celik@email.com")
                        .phone("5365556677")
                        .loanAmount(new BigDecimal("180000"))
                        .termMonths(60)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("32000"))
                        .employer("Çelik İnşaat A.Ş.")
                        .customerSegment(CreditApplication.CustomerSegment.GOLD)
                        .creditHistory(4)
                        .existingLoans(1)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1006")
                        .customerName("Zeynep Arslan")
                        .nationalId("67890123456")
                        .age(26)
                        .email("zeynep.arslan@email.com")
                        .phone("5376667788")
                        .loanAmount(new BigDecimal("65000"))
                        .termMonths(18)
                        .employmentStatus(CreditApplication.EmploymentStatus.TEMPORARY)
                        .monthlyIncome(new BigDecimal("15000"))
                        .employer("Arslan Danışmanlık")
                        .customerSegment(CreditApplication.CustomerSegment.SILVER)
                        .creditHistory(4)
                        .existingLoans(0)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1007")
                        .customerName("Mustafa Şahin")
                        .nationalId("78901234567")
                        .age(45)
                        .email("mustafa.sahin@email.com")
                        .phone("5387778899")
                        .loanAmount(new BigDecimal("320000"))
                        .termMonths(72)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("55000"))
                        .employer("Şahin Otomotiv")
                        .customerSegment(CreditApplication.CustomerSegment.PREMIUM)
                        .creditHistory(5)
                        .existingLoans(1)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1008")
                        .customerName("Elif Koç")
                        .nationalId("89012345678")
                        .age(33)
                        .email("elif.koc@email.com")
                        .phone("5398889900")
                        .loanAmount(new BigDecimal("95000"))
                        .termMonths(24)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("22000"))
                        .employer("Koç Finans Hizmetleri")
                        .customerSegment(CreditApplication.CustomerSegment.GOLD)
                        .creditHistory(4)
                        .existingLoans(0)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1009")
                        .customerName("Emre Aydın")
                        .nationalId("90123456789")
                        .age(29)
                        .email("emre.aydin@email.com")
                        .phone("5309990011")
                        .loanAmount(new BigDecimal("75000"))
                        .termMonths(36)
                        .employmentStatus(CreditApplication.EmploymentStatus.SELF_EMPLOYED)
                        .monthlyIncome(new BigDecimal("28000"))
                        .employer("Aydın Yazılım")
                        .customerSegment(CreditApplication.CustomerSegment.SILVER)
                        .creditHistory(3)
                        .existingLoans(1)
                        .build(),
                CreditApplication.builder()
                        .customerId("CUST-1010")
                        .customerName("Selin Yıldız")
                        .nationalId("01234567890")
                        .age(27)
                        .email("selin.yildiz@email.com")
                        .phone("5310001122")
                        .loanAmount(new BigDecimal("55000"))
                        .termMonths(12)
                        .employmentStatus(CreditApplication.EmploymentStatus.PERMANENT)
                        .monthlyIncome(new BigDecimal("19500"))
                        .employer("Yıldız Eğitim Merkezi")
                        .customerSegment(CreditApplication.CustomerSegment.BRONZE)
                        .creditHistory(4)
                        .existingLoans(0)
                        .build()
        );
    }
}
