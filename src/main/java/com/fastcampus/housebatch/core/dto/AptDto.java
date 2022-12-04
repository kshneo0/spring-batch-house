package com.fastcampus.housebatch.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.GeneratedValue;

/**
 * fileName : AptDto
 * author :  KimSangHoon
 * date : 2022/12/04
 */
@AllArgsConstructor
@Getter
public class AptDto {
    private String name;
    private Long price;
}
