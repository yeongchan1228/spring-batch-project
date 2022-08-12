package springbatch.springbatchproject.job.lawd;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import springbatch.springbatchproject.core.entity.Lawd;

public class LawdFieldSetMapper implements FieldSetMapper<Lawd> {

    public static final String LAWD_CD = "lawdCd";
    public static final String LAWD_DONG = "lawdDong";
    public static final String EXIST = "exist";

    public static final String EXIST_TRUE = "존재";

    @Override
    public Lawd mapFieldSet(FieldSet fieldSet) throws BindException {
        return Lawd.of(
                fieldSet.readString(LAWD_CD),
                fieldSet.readString(LAWD_DONG),
                fieldSet.readBoolean(EXIST, EXIST_TRUE) // 해당 값이 존재와 동일하면 true로 반환
        );
    }

}
