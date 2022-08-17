package springbatch.springbatchproject.job.notify;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import springbatch.springbatchproject.core.entity.ApartNotification;
import springbatch.springbatchproject.core.repository.ApartNotificationRepository;
import springbatch.springbatchproject.core.repository.LawdRepository;
import springbatch.springbatchproject.core.service.ApartDealService;
import springbatch.springbatchproject.core.service.SendServiceImpl;
import springbatch.springbatchproject.core.service.dto.ApartDto;
import springbatch.springbatchproject.job.notify.dto.NotificationDto;
import springbatch.springbatchproject.job.validator.DealDateParameterValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApartNotificationJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job apartNotificationJob(Step apartNotificationStep) {
        return jobBuilderFactory.get("apartNotificationJob")
                .incrementer(new RunIdIncrementer())
                .validator(new DealDateParameterValidator())
                .start(apartNotificationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step apartNotificationStep(
            RepositoryItemReader<ApartNotification> apartNotificationRepositoryItemReader,
            ItemProcessor<ApartNotification, NotificationDto> apartNotificationItemProcessor,
            ItemWriter<NotificationDto> apartNotificationWriter
    ) {
        return stepBuilderFactory.get("apartNotificationStep")
                .<ApartNotification, NotificationDto>chunk(10)
                .reader(apartNotificationRepositoryItemReader)
                .processor(apartNotificationItemProcessor)
                .writer(apartNotificationWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ApartNotification> apartNotificationRepositoryItemReader(
            ApartNotificationRepository apartNotificationRepository
    ) {
        return new RepositoryItemReaderBuilder<ApartNotification>()
                .name("apartNotificationRepositoryItemReader")
                .repository(apartNotificationRepository)
                .methodName("findApartNotificationByEnabledIsTrue")
                .pageSize(10)
                .arguments(List.of()) // Pageable은 pageSize(), sorts()로 넘어간다.
                .sorts(Collections.singletonMap("id", Sort.Direction.DESC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<ApartNotification, NotificationDto> apartNotificationItemProcessor(
            @Value("#{jobParameters['dealDate']}") String dealDate,
            ApartDealService apartDealService,
            LawdRepository lawdRepository
    ) {
        return apartNotification -> {
            List<ApartDto> apartDtoList
                    = apartDealService.findByLawdCdAndDealDate(apartNotification.getLawdCd(), LocalDate.parse(dealDate));

            if (apartDtoList.isEmpty()) {
                return null;
            }

            String guName = lawdRepository
                    .findByLawdCd(apartNotification.getLawdCd() + "00000")
                    .orElseThrow()
                    .getLawdDong();

            System.out.println("guName = " + guName);

            return NotificationDto.builder()
                    .email(apartNotification.getEmail())
                    .guName(guName)
                    .count(apartDtoList.size())
                    .apartDeals(apartDtoList)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<NotificationDto> apartNotificationWriter(SendServiceImpl sendService) {
        return items -> items.forEach(item -> sendService.send(item.getEmail(), item.toMessage()));
    }
}
