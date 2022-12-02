package com.fastcampus.housebatch.job.apt;

import com.fastcampus.housebatch.core.dto.AptDealDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fileName : AptDealInsertJobConfig
 * author :  KimSangHoon
 * date : 2022/12/03
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptDealInsertJob(Step aptDealInsertStep){
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
                .start(aptDealInsertStep)
                .build();
    }

    @JobScope
    @Bean
    public Step aptDealInsertStep(){
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto,AptDealDto>chunk(10)
                .reader(/*XML 리더*/)
                .writer()
                .build();
    }
}
