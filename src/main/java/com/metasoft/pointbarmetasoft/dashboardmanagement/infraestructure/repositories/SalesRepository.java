package com.metasoft.pointbarmetasoft.dashboardmanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.dashboardmanagement.domain.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface SalesRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT SUM(s.amount) FROM Sale s WHERE s.business.id = :businessId AND s.date = CURRENT_DATE")
    Double getSalesForToday(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(s.table) FROM Sale s WHERE s.business.id = :businessId AND s.date = CURRENT_DATE")
    Integer getTablesServedForToday(@Param("businessId") Long businessId);

    @Query("SELECT SUM(s.amount) FROM Sale s WHERE s.business.id = :businessId")
    Double getTotalRevenue(@Param("businessId") Long businessId);


    //@Query("SELECT COUNT(s.table) FROM Sale s WHERE s.user.id = :userId")
    //Integer getTablesServedByUser(@Param("userId") Long userId);

    //@Query("SELECT SUM(s.amount) FROM Sale s WHERE s.user.id = :userId")
    //Double getRevenueByUser(@Param("userId") Long userId);
}
