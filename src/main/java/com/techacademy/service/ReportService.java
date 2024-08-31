package com.techacademy.service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public ErrorKinds save(Report report) {
        LocalDateTime now = LocalDateTime.now();

        // 同じ日付で同じ従業員の日報が既に存在するかチェック
        if (isDuplicateReportDate(report.getEmployee(), report.getReportDate(), report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        if (report.getId() != null && reportRepository.existsById(report.getId())) {
            // 既存のレポートがある場合、作成日時を保持し、更新日時を設定
            Report existingReport = reportRepository.findById(report.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + report.getId()));
            report.setCreatedAt(existingReport.getCreatedAt());
            report.setUpdatedAt(now);
        } else {
            // 新規作成の場合、作成日時と更新日時を現在の日時に設定
            report.setCreatedAt(now);
            report.setUpdatedAt(now);
            report.setDeleteFlg(false);
        }

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    @Transactional
    public void delete(Long id) {
        reportRepository.findById(id).ifPresent(report -> {
            report.setDeleteFlg(true);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
        });
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findAllActive() {
        return reportRepository.findAllByDeleteFlgFalse();
    }

    public List<Report> findAllActiveByEmployee(Employee employee) {
        return reportRepository.findAllByEmployeeAndDeleteFlgFalse(employee);
    }

    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    // 特定の従業員と日付に一致し、指定したIDを除く日報が存在するか確認するメソッド
    public boolean isDuplicateReportDate(Employee employee, LocalDate reportDate, Long excludeId) {
        if (excludeId == null) {
            return reportRepository.existsByEmployeeAndReportDateAndDeleteFlgFalse(employee, reportDate);
        } else {
            return reportRepository.existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(employee, reportDate, excludeId);
        }
    }
}
