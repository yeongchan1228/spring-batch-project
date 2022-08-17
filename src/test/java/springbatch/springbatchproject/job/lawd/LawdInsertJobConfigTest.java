package springbatch.springbatchproject.job.lawd;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import springbatch.springbatchproject.BatchTestConfig;
import springbatch.springbatchproject.core.service.LawdService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {LawdInsertJobConfig.class, BatchTestConfig.class})
public class LawdInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private LawdService lawdService;

    @Test
    public void success() throws Exception {
        // when
        JobParameters jobParameters = new JobParameters(Map.of("filePath", new JobParameter("LAWD_CODE_SAMPLE.txt")));
        JobExecution execution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        verify(lawdService, times(8)).upsert(any());
    }

    @Test
    public void whenFileNotFound() throws Exception {
        // when
        JobParameters jobParameters = new JobParameters(Map.of("filePath", new JobParameter("NOT_EXIST_FILE.txt")));

        // then
        assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(jobParameters))
                .isInstanceOf(JobParametersInvalidException.class)
                .hasMessage("filePath가 class path에 존재하지 않습니다. 경로를 확인해 주세요.");
        verify(lawdService, never()).upsert(any());
    }
}
