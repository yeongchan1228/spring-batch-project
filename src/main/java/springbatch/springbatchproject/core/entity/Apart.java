package springbatch.springbatchproject.core.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springbatch.springbatchproject.job.apt.dto.AptDealDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apart_id")
    private Long id;

    @Column(nullable = false)
    private String apartName;

    @Column(nullable = false)
    private String jibun;

    @Column(nullable = false)
    private String dong;

    @Column(nullable = false)
    private String lawdCd;

    @Column(nullable = false)
    private int builtYear;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    protected Apart(String apartName, String jibun, String dong, String lawdCd, int builtYear) {
        this.apartName = apartName;
        this.jibun = jibun;
        this.dong = dong;
        this.lawdCd = lawdCd;
        this.builtYear = builtYear;
    }

    public static Apart from(AptDealDto dto) {
        return new Apart(
                dto.getAptName().trim(),
                dto.getJibun().trim(),
                dto.getDong().trim(),
                dto.getRegionalCode(),
                dto.getBuildYear()
        );
    }
}
