package springbatch.springbatchproject.job.apt;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import springbatch.springbatchproject.core.repository.LawdRepository;

import java.util.List;

/**
 * 데이터가 있으면 다음 스텝을 실행하도록 하고, 데이터가 없으면 종료되도록 한다.
 * 데이터가 있으면 -> CONTINUABLE
 */
@Component
@RequiredArgsConstructor
public class LawdTasklet implements Tasklet {

    private int itemCount;
    private List<String> lawdCds;
    private final LawdRepository lawdRepository;

    private static final String KEY_LAWD_CD = "lawdCd";
    private static final String KEY_ITEM_COUNT = "itemCount";
    private static final String KEY_LAWD_CD_LIST = "lawdCdList";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobExecutionContext = getExecutionContext(chunkContext);

        initList(jobExecutionContext);
        initItemCount(jobExecutionContext);

        if (itemCount == 0) {
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }

        String lawdCd = lawdCds.get(--itemCount);
        jobExecutionContext.putString(KEY_LAWD_CD, lawdCd);
        jobExecutionContext.putInt(KEY_ITEM_COUNT, itemCount);
        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));
        return RepeatStatus.FINISHED;
    }

    private void initList(ExecutionContext jobExecutionContext) {
        if (jobExecutionContext.containsKey(KEY_LAWD_CD_LIST)) {
            lawdCds = (List<String>) jobExecutionContext.get(KEY_LAWD_CD_LIST);
        } else {
            lawdCds = lawdRepository.findDistinctSubstringLawdCd(PageRequest.of(0, 40));
            jobExecutionContext.put(KEY_LAWD_CD_LIST, lawdCds);
        }
    }

    private void initItemCount(ExecutionContext jobExecutionContext) {
        if (jobExecutionContext.containsKey(KEY_ITEM_COUNT)) {
            itemCount = jobExecutionContext.getInt(KEY_ITEM_COUNT);
        } else {
            itemCount = lawdCds.size();
        }
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
    }

}
