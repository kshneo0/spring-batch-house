package com.fastcampus.housebatch.core.repository;

import com.fastcampus.housebatch.core.entity.Apt;
import com.fastcampus.housebatch.core.entity.AptDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * fileName : AptDealRepository
 * author :  KimSangHoon
 * date : 2022/12/04
 */
public interface AptDealRepository extends JpaRepository<AptDeal, Long> {

    Optional<AptDeal> findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate dealDate, Long DealAmount, Integer floor
    );

}
