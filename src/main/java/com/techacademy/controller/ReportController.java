package com.techacademy.controller;

import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        List<Report> reports;
        if (userDetail == null) {
            return "redirect:/login";
        }

        List<String> roles = userDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) {
            reports = reportService.findAllActive();
        } else {
            reports = reportService.findAllActiveByEmployee(userDetail.getEmployee());
        }

        model.addAttribute("listSize", reports.size());
        model.addAttribute("rL", reports);
        return "reports/list";
    }

    // 日報新規登録画面表示
    @GetMapping("/add")
    public String add(@AuthenticationPrincipal UserDetail userDetail, Model model) {
        Report report = new Report();
        report.setEmployee(userDetail.getEmployee());
        report.setReportDate(LocalDate.now());
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping("/add")
    public String create(@AuthenticationPrincipal UserDetail userDetail, @Valid @ModelAttribute("report") Report report, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // エラーメッセージを個別のフィールドごとに設定する
            model.addAttribute("report", report);
            return "reports/new";
        }

        // 同じ日付で同じ従業員が既に日報を登録しているかチェック
        if (reportService.isDuplicateReportDate(userDetail.getEmployee(), report.getReportDate(), null)) {
            model.addAttribute("reportDateError", "既に登録されている日付です");
            model.addAttribute("report", report);
            return "reports/new";
        }

        report.setEmployee(userDetail.getEmployee());
        reportService.save(report);
        return "redirect:/reports";
    }

    // 日報詳細画面表示
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        reportService.findById(id).ifPresent(report -> model.addAttribute("report", report));
        return "reports/detail";
    }

    // 日報更新画面表示
    @GetMapping("/{id}/update")
    public String edit(@PathVariable Long id, Model model) {
        reportService.findById(id).ifPresent(report -> {
            // ここで日付がnullであれば、現在の日付を設定
            if (report.getReportDate() == null) {
                report.setReportDate(LocalDate.now());
            }
            model.addAttribute("report", report);
        });
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@Valid @ModelAttribute("report") Report report, BindingResult bindingResult, @PathVariable Long id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (bindingResult.hasErrors()) {
            report.setEmployee(userDetail.getEmployee());
            bindingResult.getFieldErrors().forEach(error -> {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            });
            model.addAttribute("report", report);
            return "reports/update";
        }

        // 更新時も同様のチェックを行うが、同一レコードのチェックは無視する
        if (reportService.isDuplicateReportDate(userDetail.getEmployee(), report.getReportDate(), id)) {
            model.addAttribute("reportDateError", "既に登録されている日付です");
            report.setEmployee(userDetail.getEmployee());
            model.addAttribute("report", report);
            return "reports/update";
        }

        // report IDを利用して、created_atの値を取得し、nullでないか確認
        Report existingReport = reportService.findById(id).orElse(null);
        if (existingReport != null) {
            report.setCreatedAt(existingReport.getCreatedAt());
            report.setEmployee(existingReport.getEmployee()); // ここでemployeeをセット
        } else {
            report.setEmployee(userDetail.getEmployee()); // 新しいデータの場合、ログインユーザーをセット
        }

        report.setId(id);
        reportService.save(report);
        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reportService.delete(id);
        return "redirect:/reports";
    }
}
