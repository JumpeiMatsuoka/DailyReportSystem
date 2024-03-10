package com.techacademy.controller;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧表示
    @GetMapping
    public String list(Model model) {
        model.addAttribute("rl", reportService.findAll());
        model.addAttribute("totalReports", reportService.findAll().size());
        return "reports/list";
    }

    // 日報新規登録画面表示
    @GetMapping("/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        Report report = new Report();
        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("report") Report report, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "reports/new";
        }
        ErrorKinds result = reportService.save(report);
        if (result != ErrorKinds.SUCCESS) {
            model.addAttribute("errorMessage", result.getMessage());
            return "reports/new";
        }
        return "redirect:/reports";
    }

    // 日報詳細画面表示
    @GetMapping("/{id}/detail")
    public String detail(@PathVariable Long id, Model model) {
        reportService.findById(id).ifPresent(report -> model.addAttribute("report", report));
        return "reports/detail";
    }

    // 日報更新画面表示
    @GetMapping("/{id}/update")
    public String edit(@PathVariable Long id, Model model) {
        reportService.findById(id).ifPresent(report -> model.addAttribute("report", report));
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("report") Report report, BindingResult bindingResult, @PathVariable Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に誤りがあります。");
            model.addAttribute("report", report);
            return "reports/update";
        }
        report.setId(id); // 更新対象のIDを設定
        ErrorKinds result = reportService.save(report);
        if (result != ErrorKinds.SUCCESS) {
            model.addAttribute("errorMessage", result.getMessage());
            return "reports/update";
        }
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}
