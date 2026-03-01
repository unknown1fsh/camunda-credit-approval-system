package com.creditapp.controller;

import com.creditapp.model.CreditApplication;
import com.creditapp.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Merkezi dashboard controller - Tüm uygulama URL'lerini tek ekranda sunar.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CreditApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }
        model.addAttribute("approvedCount", applicationService.countByStatus(CreditApplication.ApplicationStatus.APPROVED));
        model.addAttribute("rejectedCount", applicationService.countByStatus(CreditApplication.ApplicationStatus.REJECTED));
        return "dashboard";
    }
}
