package springbatch.springbatchproject.job.apt;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import springbatch.springbatchproject.BatchTestConfig;
import springbatch.springbatchproject.adapter.ApartmentApiResource;
import springbatch.springbatchproject.core.repository.LawdRepository;
import springbatch.springbatchproject.core.service.ApartDealService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptDealInsertJobConfig.class, BatchTestConfig.class})
class AptDealInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private ApartDealService apartDealService;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private ApartmentApiResource apartmentApiResource;

    @Test
    public void success() throws Exception {
        // given
        when(lawdRepository.findDistinctSubstringLawdCd(any())).thenReturn(List.of("41135"));
        when(apartmentApiResource.getResource(anyString(), any())).thenReturn(new ClassPathResource("apartment-api-response.xml"));

        // when
        JobExecution execution =
                jobLauncherTestUtils.launchJob(new JobParameters(Map.of("yearMonth", new JobParameter("2021-07"))));

        // then
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(apartDealService, times(1)).upsert(any()); // upsert가 1번 호출 되었는가
    }

    @Test
    public void fail_when_yearMonthNotExist() throws Exception {
        // given
        when(lawdRepository.findDistinctSubstringLawdCd(any())).thenReturn(List.of("41135"));
        when(apartmentApiResource.getResource(anyString(), any())).thenReturn(new ClassPathResource("apartment-api-response.xml"));

        // when & then
        assertThatThrownBy(() -> jobLauncherTestUtils.launchJob()).isInstanceOf(JobParametersInvalidException.class);
        verify(apartDealService, never()).upsert(any());
    }
}