package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.Length;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "日付は必須です。")
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @NotEmpty(message = "タイトルは必須です。")
    @Length(max = 100, message = "タイトルは100文字以内で入力してください。")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotEmpty(message = "内容は必須です。")
    @Length(max = 600, message = "内容は600文字以内で入力してください。")
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg; // 削除フラグ

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 新しいフィールドを追加
    @Column(name = "report_number")
    private Long reportNumber;
}
