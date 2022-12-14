package springbatch.springbatchproject.job.apt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import springbatch.springbatchproject.adapter.ApartmentApiResource;
import springbatch.springbatchproject.core.service.ApartDealService;
import springbatch.springbatchproject.job.apt.dto.AptDealDto;
import springbatch.springbatchproject.job.validator.YearMonthParameterValidator;

import java.time.YearMonth;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AptDealInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ApartmentApiResource apartmentApiResource;

    @Bean
    public Job aptDealInsertJob(
            Step lawdCdReadStep,
            Step aptDealInsertStep
//            Step contextPrintStep
    ) {
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
//                .validator(new FilePathParameterResourceValidator())
//                .validator(aptDealJobParameterValidator())
                .validator(new YearMonthParameterValidator())

                .start(lawdCdReadStep)
                .on("CONTINUABLE").to(aptDealInsertStep).next(lawdCdReadStep) // ?????? ??????
                .from(lawdCdReadStep).on("*").end() // ?????? ??????
                .end()

                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
//            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
//            @Value("#{jobParameters['lawdCd']}") String lawdCd,
            @Value("#{jobExecutionContext['lawdCd']}") String lawdCd,
            Jaxb2Marshaller aptDealDtoMarshaller
    ) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
//                .resource(new ClassPathResource(filePath))
                .resource(apartmentApiResource.getResource(lawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }

    @Bean
    @JobScope
    public Step lawdCdReadStep(Tasklet lawdTasklet) {
        return stepBuilderFactory.get("lawdCdReadStep")
                .tasklet(lawdTasklet)
                .build();
    }

    @Bean
    @JobScope
    public Step aptDealInsertStep(
            StaxEventItemReader<AptDealDto> aptDealResourceReader,
            ItemWriter<AptDealDto> aptDealDtoItemWriter
            ) {
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto, AptDealDto>chunk(10)
                .reader(aptDealResourceReader)
                .writer(aptDealDtoItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public Jaxb2Marshaller aptDealDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }

    @Bean
    @StepScope
    public ItemWriter<AptDealDto> aptDealDtoItemWriter(ApartDealService apartDealService) {
        return items -> items.forEach(apartDealService::upsert);
    }

//    @Bean
//    @JobScope
//    public Step contextPrintStep(
////            @Value("#{jobExecutionContext['lawdCd']}") String valueLawdCd
//    ) {
//        return stepBuilderFactory.get("contextPrintStep")
//                .tasklet((contribution, chunkContext) -> {
//                    ExecutionContext jobExecutionContext
//                            = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
//
//                    String lawdCd = jobExecutionContext.getString("lawdCd");
//                    System.out.println("lawdCd = " + lawdCd);
////                    System.out.println("valueLawdCd = " + valueLawdCd); // @JobScope??? ?????? Job Bean??? ????????? ??? ?????? ???????????? ????????? ????????? ????????? ?????? ????????????.
//                    return RepeatStatus.FINISHED;
//                })
//                .build();
//

//    }
    //    private JobParametersValidator aptDealJobParameterValidator() {
//        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
//        validator.setValidators(List.of(
//                new YearMonthParameterValidator(),
//                new LawdCdParameterValidator()
//        ));
//        return validator;
//    }
}
