package springbatch.springbatchproject.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import springbatch.springbatchproject.core.entity.Apart;
import springbatch.springbatchproject.core.entity.ApartDeal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartDealRepository extends JpaRepository<ApartDeal, Long> {
    
    Optional<ApartDeal> findApartDealByApartAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apart apart, double exclusiveArea, LocalDate dealDate, long dealAmount, int floor
    );

    @Query("select ad from ApartDeal ad join fetch ad.apart where ad.dealCanceled = false and ad.dealDate = ?1")
    List<ApartDeal> findByDealCanceledIsFalseAndDealDateEquals(LocalDate dealDate);
}
