package springbatch.springbatchproject.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springbatch.springbatchproject.core.entity.ApartNotification;

public interface ApartNotificationRepository extends JpaRepository<ApartNotification, Long> {

    @Query
    Page<ApartNotification> findApartNotificationByEnabledIsTrue(Pageable pageable);
}
