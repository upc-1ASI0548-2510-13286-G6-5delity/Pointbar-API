package com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BeverageRepository extends JpaRepository<Beverage, Long> {
    List<Beverage> findByBusinessId(Long businessId);
}
