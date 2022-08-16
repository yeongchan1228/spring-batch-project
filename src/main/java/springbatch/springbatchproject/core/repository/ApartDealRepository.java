package springbatch.springbatchproject.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbatch.springbatchproject.core.entity.Apart;
import springbatch.springbatchproject.core.entity.ApartDeal;

import java.time.LocalDate;
import java.util.Optional;

public interface ApartDealRepository extends JpaRepository<ApartDeal, Long> {
    
    Optional<ApartDeal> findApartDealByApartAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apart apart, double exclusiveArea, LocalDate dealDate, long dealAmount, int floor
    );
}
