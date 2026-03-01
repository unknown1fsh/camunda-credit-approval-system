package com.creditapp.controller;

import com.creditapp.dto.ApplicationFormDto;
import com.creditapp.model.CreditApplication;
import com.creditapp.service.CreditApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Kredi başvurusu controller (Thymeleaf UI)
 */
@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final CreditApplicationService applicationService;

    @GetMapping("/new")
    public String newApplicationForm(Model model) {
        model.addAttribute("application", ApplicationFormDto.builder().build());
        return "application/new-application";
    }

    @PostMapping("/submit")
    public String submitApplication(
            @Valid @ModelAttribute("application") ApplicationFormDto form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        log.info("Submitting new application for customer: {}", form.getCustomerName());

        if (bindingResult.hasErrors()) {
            return "application/new-application";
        }

        try {
            CreditApplication created = applicationService.createAndStartApplication(form.toEntity());
            redirectAttributes.addAttribute("applicationId", created.getApplicationId());
            return "redirect:/applications/{applicationId}?success=true";
        } catch (Exception e) {
            log.error("Error submitting application: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/applications/new";
        }
    }

    @GetMapping
    public String listApplications(
            @RequestParam(required = false) String status,
            Model model) {
        List<CreditApplication> applications;
        if (status != null && !status.isEmpty()) {
            if ("PENDING".equalsIgnoreCase(status)) {
                applications = applicationService.findByStatusIn(
                        CreditApplication.ApplicationStatus.AWAITING_APPROVAL,
                        CreditApplication.ApplicationStatus.UNDER_REVIEW,
                        CreditApplication.ApplicationStatus.SUBMITTED,
                        CreditApplication.ApplicationStatus.DOCUMENT_PENDING);
            } else {
                try {
                    CreditApplication.ApplicationStatus appStatus = CreditApplication.ApplicationStatus.valueOf(status);
                    applications = applicationService.findByStatus(appStatus);
                } catch (IllegalArgumentException e) {
                    applications = applicationService.findAll();
                    status = null;
                }
            }
        } else {
            applications = applicationService.findAll();
        }
        model.addAttribute("applications", applications);
        model.addAttribute("currentFilter", status);
        return "application/application-list";
    }

    @GetMapping("/{applicationId}")
    public String viewApplication(@PathVariable String applicationId, Model model) {
        CreditApplication app = applicationService.findByApplicationId(applicationId);
        model.addAttribute("app", app);
        return "application/application-detail";
    }
}
