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

        if (isDuplicateReportDate(report.getEmployee(), report.getReportDate(), report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        if (report.getId() != null && reportRepository.existsById(report.getId())) {
            report.setUpdatedAt(now);
        } else {
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

    public boolean isDuplicateReportDate(Employee employee, LocalDate reportDate, Long excludeId) {
        return reportRepository.existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(employee, reportDate, excludeId);
    }

    public boolean existsByEmployeeAndDate(Employee employee, LocalDate date, Long excludeId) {
        return reportRepository.existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(employee, date, excludeId);
    }
}
