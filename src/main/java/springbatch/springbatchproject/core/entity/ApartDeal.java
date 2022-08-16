package springbatch.springbatchproject.core.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springbatch.springbatchproject.job.apt.dto.AptDealDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApartDeal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apart_deal_id")
    private Long id;

    @Column(nullable = false)
    private double exclusiveArea;

    @Column(nullable = false)
    private LocalDate dealDate;

    @Column(nullable = false)
    private long dealAmount;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private boolean dealCanceled;

    private LocalDate dealCanceledDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apart_id")
    private Apart apart;

    public void changeDealCanceled(boolean dealCanceled, LocalDate dealCanceledDate) {
        this.dealCanceled = dealCanceled;
        this.dealCanceledDate = dealCanceledDate;
    }

    protected ApartDeal(double exclusiveArea, LocalDate dealDate, long dealAmount, int floor, boolean dealCanceled, LocalDate dealCanceledDate, Apart apart) {
        this.exclusiveArea = exclusiveArea;
        this.dealDate = dealDate;
        this.dealAmount = dealAmount;
        this.floor = floor;
        this.dealCanceled = dealCanceled;
        this.dealCanceledDate = dealCanceledDate;
        this.apart = apart;
    }

    public static ApartDeal of(AptDealDto dto, Apart apart) {
        return new ApartDeal(
                dto.getExclusiveArea(),
                dto.getDealDate(),
                dto.getDealAmount(),
                dto.getFloor(),
                dto.isDealCanceled(),
                dto.getDealCanceledDate(),
                apart
        );
    }
}
