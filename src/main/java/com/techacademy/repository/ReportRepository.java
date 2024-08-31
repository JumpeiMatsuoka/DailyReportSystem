package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportDate(LocalDate reportDate);
    List<Report> findAllByDeleteFlgFalse();

    // 特定の従業員と日付に一致する日報が存在するか確認するメソッド
    boolean existsByEmployeeAndReportDateAndDeleteFlgFalse(Employee employee, LocalDate reportDate);

    // 特定の従業員と日付に一致し、指定したIDを除く日報が存在するか確認するメソッド
    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.employee = :employee AND r.reportDate = :reportDate AND (:excludeId IS NULL OR r.id != :excludeId) AND r.deleteFlg = false")
    boolean existsByEmployeeAndReportDateAndIdNotAndDeleteFlgFalse(@Param("employee") Employee employee, @Param("reportDate") LocalDate reportDate, @Param("excludeId") Long excludeId);

    // 特定の従業員の日報を取得するメソッド
    List<Report> findAllByEmployeeAndDeleteFlgFalse(Employee employee);
}
