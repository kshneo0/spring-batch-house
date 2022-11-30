package com.fastcampus.housebatch.core.repository;

import com.fastcampus.housebatch.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * fileName : LawdRepository
 * author :  KimSangHoon
 * date : 2022/11/29
 */
public interface LawdRepository extends JpaRepository<Lawd, Long> {
    Optional<Lawd> findByLawdCd(String lawdCd);
}
