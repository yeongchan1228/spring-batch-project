package springbatch.springbatchproject.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

public class FilePathParameterResourceValidator implements JobParametersValidator {

    private static final String FILE_PATH = "filePath";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String filePath = parameters.getString(FILE_PATH);

        if (!StringUtils.hasText(filePath)) {
            throw new JobParametersInvalidException(FILE_PATH + "가 빈 문자열이거나 존재하지 않습니다.");
        }

        ClassPathResource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            throw new JobParametersInvalidException(FILE_PATH + "가 class path에 존재하지 않습니다. 경로를 확인해 주세요.");
        }
    }
}
