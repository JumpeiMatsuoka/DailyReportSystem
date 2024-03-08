package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.techacademy.service.ReportService;

    @Controller
    @RequestMapping("daily-reports")
    public class ReportController {

        private final ReportService reportService;

        @Autowired
        public ReportController(ReportService reportService) {
            this.reportService = reportService;
        }

        // 日報一覧画面を表示
        @GetMapping
        public String listReports(Model model) {
            model.addAttribute("reportsList", reportService.findAll());
            model.addAttribute("listSize", reportService.findAll().size());
            return "reports/list";
        }
        }
    // 他の日報関連メソッド（詳細、更新、削除など）もここに実装
