package com.metasoft.pointbarmetasoft.salesmanagement.application.services;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.CreateOrderRequestDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.OrderItemDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto.OrderResponseDto;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.Order;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import java.util.List;

public interface IOrderService {
    Double calculateOrderTotal(Order order);
    ApiResponse<?> createOrder(CreateOrderRequestDto requestDto, UserEntity employee);
    List<OrderResponseDto> getActiveOrders(Long businessId);
    ApiResponse<?> updateOrderItemStatus(Long orderId, Long orderItemId, Boolean delivered);
    ApiResponse<?> closeOrder(Long orderId, UserEntity employee);
    ApiResponse<?> deleteOrderItem(Long orderId, Long itemId);
    ApiResponse<?> addItemToOrder(Long orderId, OrderItemDto orderItemDto);
    List<OrderResponseDto> getOrdersByTableSpaceIdAndTableId(Long tableSpaceId, Long tableId, Long businessId);
}
