package com.fastcampus.housebatch.job.apt;

import com.fastcampus.housebatch.adapter.ApartmentApiResource;
import com.fastcampus.housebatch.core.dto.AptDealDto;
import com.fastcampus.housebatch.core.repository.LawdRepository;
import com.fastcampus.housebatch.job.validator.LawdCdParameterValidator;
import com.fastcampus.housebatch.job.validator.YearMonthParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

/**
 * fileName : AptDealInsertJobConfig
 * author :  KimSangHoon
 * date : 2022/12/03
 * <p>
 * --spring.batch.job.names=aptDealInsertJob -filePath=apartment-api-response.xml
 * --spring.batch.job.names=aptDealInsertJob -yearMonth=2021-07 -lawdCd=41135
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ApartmentApiResource apartmentApiResource;

    private final LawdRepository lawdRepository;

    @Bean
    public Job aptDealInsertJob(
            Step guLawdCdStep,
            Step contextPrintStep
//            Step aptDealInsertStep
    ) {
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
//                .validator(new FilePathParameterValidator())
                .validator(aptDealJobParameterValidator())
                .start(guLawdCdStep)
                .on("CONTINUABLE").to(contextPrintStep).next(guLawdCdStep)
                .from(guLawdCdStep)
                .on("*").end()
                .end()
//                .next(contextPrintStep)
//                .next(aptDealInsertStep)
                .build();
    }

    private JobParametersValidator aptDealJobParameterValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(
                new YearMonthParameterValidator()
//                ,new LawdCdParameterValidator()
        ));
        return validator;
    }

    @JobScope
    @Bean
    public Step guLawdCdStep(Tasklet guLawdCdTasklet) {
        return stepBuilderFactory.get("guLawdCdStep")
                .tasklet(guLawdCdTasklet)
                .build();
    }

    /**
     * ExecutionContext에 저장할 데이터
     * 1. guLawdCd - 구 코드 -> 다음 스텝에서 활용할 값
     * 2. guLawdCdList - 구 코드 리스트
     * 3. itemCount - 남아있는 구 코드의 갯수
     */
    @StepScope
    @Bean
    public Tasklet guLawdCdTasklet() {
        return (contribution, chunkContext) -> {

            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
            ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

            //데이터가 있으면 다음 스텝을 실행하도록 하고, 데이터가 없으면 종료되도록 한다.
            //데이터가 있으면 -> CONTINUABLE
            List<String> guLawdCdList;
            if(!executionContext.containsKey("guLawdCdList")){
                guLawdCdList = lawdRepository.findDistinctGuLawdCd();
                executionContext.put("guLawdCdList", guLawdCdList);
                executionContext.putInt("itemCount", guLawdCdList.size());
            } else {
                guLawdCdList = (List<String>)executionContext.get("guLawdCdList");
            }

            Integer itemCount = executionContext.getInt("itemCount");

            if(itemCount == 0) {
                contribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }

            itemCount--;
            String guLawdCd = guLawdCdList.get(itemCount);
            executionContext.putString("guLawdCd", guLawdCd);
            executionContext.putInt("itemCount", itemCount);
            contribution.setExitStatus(new ExitStatus("CONTINUABLE"));
            return RepeatStatus.FINISHED;
        };
    }

    @JobScope
    @Bean
    public Step contextPrintStep(Tasklet contextPrintTasklet) {
        return stepBuilderFactory.get("contextPrintStep")
                .tasklet(contextPrintTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet contextPrintTasklet(
            @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd
    ) {
        return (contribution, chunkContext) -> {

            System.out.println("===========[contextPrintTasklet] guLawdCd = " + guLawdCd);

            return RepeatStatus.FINISHED;
        };
    }


    @JobScope
    @Bean
    public Step aptDealInsertStep(StaxEventItemReader<AptDealDto> aptDealResourceReader
            , ItemWriter<AptDealDto> aptDealWriter
    ) {
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto, AptDealDto>chunk(10)
                .reader(aptDealResourceReader)
                .writer(aptDealWriter)
                .build();
    }

    @StepScope
    @Bean
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
//            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd,
            Jaxb2Marshaller aptDealDtoMarshaller
    ) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
//                .resource(new ClassPathResource(filePath))
                .resource(apartmentApiResource.getResource(guLawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptDealDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;

    }

    @StepScope
    @Bean
    public ItemWriter<AptDealDto> aptDealWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
