package springbatch.springbatchproject.core.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ApartNotification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apart_notification_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String lawdCd;

    @Column(nullable = false)
    private boolean enabled;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
