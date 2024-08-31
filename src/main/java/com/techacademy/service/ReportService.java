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

        // 新規登録か更新かを判定し、適切な日時を設定
        if (report.getId() != null && reportRepository.existsById(report.getId())) {
            // 更新時に created_at が null の場合、エラーを防ぐための対応
            if (report.getCreatedAt() == null) {
                report.setCreatedAt(now); // ここで作成日時をセット
            }
            report.setUpdatedAt(now);
        } else {
            report.setCreatedAt(now); // 新規作成時に作成日時をセット
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
