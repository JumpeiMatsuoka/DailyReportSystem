package com.techacademy.service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    // コンストラクタ注入でReportRepositoryをインジェクション
    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報の保存または更新を行うメソッド
    @Transactional
    public ErrorKinds save(Report report) {
        LocalDateTime now = LocalDateTime.now();
        // 既存の日報を更新する場合、更新日時を設定
        if (report.getId() != null && reportRepository.existsById(report.getId())) {
            report.setUpdatedAt(now);
        } else {
            // 新規日報を登録する場合、作成日時と更新日時を設定
            report.setCreatedAt(now);
            report.setUpdatedAt(now);
            report.setDeleteFlg(false); // 新規作成の場合は削除フラグをfalseに設定
        }
        reportRepository.save(report); // 日報を保存
        return ErrorKinds.SUCCESS; //
    }

    // 日報の論理削除を行うメソッド
    @Transactional
    public void delete(Long id) {
        reportRepository.findById(id).ifPresent(report -> {
            report.setDeleteFlg(true); // 削除フラグをtrueに設定
            report.setUpdatedAt(LocalDateTime.now()); // 更新日時を現在時刻に設定
            reportRepository.save(report); // 更新を保存
        });
    }

    // すべての日報を取得するメソッド
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // IDによる日報の検索を行うメソッド
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }
}
