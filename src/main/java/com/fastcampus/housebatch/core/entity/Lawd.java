package com.fastcampus.housebatch.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * fileName : Lawd
 * author :  KimSangHoon
 * date : 2022/11/29
 * --spring.batch.job.names=lawdInsertJob -filePath=LAWD_CODE.txt
 */
@Entity
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Table(name="lawd")
public class Lawd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lawdId;

    @Column(nullable = false)
    private String lawdCd;

    @Column(nullable = false)
    private String lawdDong;

    @Column(nullable = false)
    private Boolean exist;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
