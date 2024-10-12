package com.metasoft.pointbarmetasoft.saleshistorymanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.domain.entities.SalesHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SalesHistoryRepository extends JpaRepository<SalesHistory, Long> {
    @Query("SELECT s FROM SalesHistory s WHERE s.business.id = :businessId ORDER BY s.saleDate DESC")
    List<SalesHistory> findAllByBusiness(@Param("businessId") Long businessId);

    @Query("SELECT s FROM SalesHistory s WHERE s.employee.id = :employeeId ORDER BY s.saleDate DESC")
    List<SalesHistory> findAllByEmployee(@Param("employeeId") Long employeeId);
}
