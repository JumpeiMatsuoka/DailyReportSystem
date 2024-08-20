package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLRestriction;
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
@SQLRestriction("delete_flg = false")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "{jakarta.validation.constraints.NotNull.message}")
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @NotEmpty(message = "{jakarta.validation.constraints.NotEmpty.message}")
    @Length(max = 100, message = "{org.hibernate.validator.constraints.Length.message}")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotEmpty(message = "{jakarta.validation.constraints.NotEmpty.message}")
    @Length(max = 600, message = "{org.hibernate.validator.constraints.Length.message}")
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "delete_flg", nullable = false)
    private boolean deleteFlg = false; // デフォルト値をfalseに設定

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    @Column(name = "report_number")
    private Long reportNumber;

    public void delete() {
        this.deleteFlg = true;
    }

    public void restore() {
        this.deleteFlg = false;
    }

    public void setStatus(String status) {
        // TODO 自動生成されたメソッド・スタブ
    }
}
