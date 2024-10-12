package com.metasoft.pointbarmetasoft.salesmanagement.infraestructure.repositories;

import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.Order;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBusinessIdAndStatus(Long businessId, OrderStatus status);
    List<Order> findByBusinessIdAndTableSpaceIdAndTableId(Long businessId, Long tableSpaceId, Long tableId);
    Optional<Order> findByTableSpaceIdAndTableId(Long tableSpaceId, Long tableId);
}
