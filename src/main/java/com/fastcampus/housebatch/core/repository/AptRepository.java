package com.fastcampus.housebatch.core.repository;

import com.fastcampus.housebatch.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * fileName : AptRepository
 * author :  KimSangHoon
 * date : 2022/12/04
 */
public interface AptRepository extends JpaRepository<Apt, Long> {

    Optional<Apt> findAptByAptNameAndJibun(String aptName, String jibun);
}
