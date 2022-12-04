package com.fastcampus.housebatch.job.notify;

import com.fastcampus.housebatch.core.dto.NotificationDto;
import com.fastcampus.housebatch.core.entity.AptNotification;
import com.fastcampus.housebatch.core.repository.AptNotificationRepository;
import com.fastcampus.housebatch.job.validator.DealDateParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

/**
 * fileName : AptNotificationJobConfig
 * author :  KimSangHoon
 * date : 2022/12/04
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class AptNotificationJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptNotificationJob(Step aptNotificationStep) {
        return jobBuilderFactory.get("aptNotificationJob")
                .incrementer(new RunIdIncrementer())
                .validator(new DealDateParameterValidator())
                .start(aptNotificationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step aptNotificationStep(RepositoryItemReader<AptNotification> aptNotificationRepositoryItemReader,
                                    ItemProcessor<AptNotification, NotificationDto> aptNotificationProcessor,
                                    ItemWriter<NotificationDto> aptNotificationWriter
    ) {
        return stepBuilderFactory.get("aptNotificationStep")
                .<AptNotification, NotificationDto>chunk(10)
                .reader(aptNotificationRepositoryItemReader)
                .processor(aptNotificationProcessor)
                .writer(aptNotificationWriter)
                .build();

    }

    @StepScope
    @Bean
    public RepositoryItemReader<AptNotification> aptNotificationRepositoryItemReader(AptNotificationRepository aptNotificationRepository) {
        return new RepositoryItemReaderBuilder<AptNotification>()
                .name("aptNotificationRepositoryItemReader")
                .repository(aptNotificationRepository)
                .methodName("findByEnabledIsTrue")
                .pageSize(10)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("aptNotificationId", Sort.Direction.DESC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<AptNotification, NotificationDto> aptNotificationProcessor() {
        return new ItemProcessor<AptNotification, NotificationDto>() {
            @Override
            public NotificationDto process(AptNotification item) throws Exception {
                return null;
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<NotificationDto> aptNotificationWriter() {
        return items -> {

        };
    }


}
