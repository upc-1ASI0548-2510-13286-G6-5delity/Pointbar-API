package com.metasoft.pointbarmetasoft.salesmanagement.application.controllers;
import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.BeverageRepository;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.CreateOrderRequestDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.OrderItemDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto.OrderResponseDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.services.IOrderService;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.Order;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.OrderItem;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.enums.OrderStatus;
import com.metasoft.pointbarmetasoft.salesmanagement.infraestructure.repositories.OrderRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import com.metasoft.pointbarmetasoft.tablemanagement.infraestructure.repositories.TableSpaceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final IOrderService orderService;
    private final UserRepository userRepository;
    private final TableSpaceRepository tableSpaceRepository;
    private final BeverageRepository beverageRepository;
    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(IOrderService orderService, UserRepository userRepository, TableSpaceRepository tableSpaceRepository, BeverageRepository beverageRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.tableSpaceRepository = tableSpaceRepository;
        this.beverageRepository = beverageRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/table/{tableSpaceId}/{tableId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrdersByTableId(
            @PathVariable Long tableSpaceId, @PathVariable Long tableId, Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<OrderResponseDto> orders = orderService.getOrdersByTableSpaceIdAndTableId(tableSpaceId, tableId, user.getBusiness().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createOrder(@Valid @RequestBody CreateOrderRequestDto requestDto, Authentication authentication) {
        log.info("TableSpaceId recibido: " + requestDto.getTableSpaceId());
        log.info("TableId recibido: " + requestDto.getTableId());

        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User  not found"));

        TableSpace tableSpace = tableSpaceRepository.findById(requestDto.getTableSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Table space not found"));

        Optional<Order> existingOrderOpt = orderRepository.findByTableSpaceIdAndTableId(tableSpace.getId(), requestDto.getTableId());
        Order order;
        if (existingOrderOpt.isPresent()) {
            order = existingOrderOpt.get();
        } else {
            order = new Order();
            order.setTableSpace(tableSpace);
            order.setTableId(requestDto.getTableId());
            order.setEmployee(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.OPEN);
            order.setBusiness(user.getBusiness());
        }

        for (OrderItemDto itemDto : requestDto.getItems()) {
            Optional<OrderItem> existingItem = order.getItems().stream()
                    .filter(item -> item.getBeverage().getId().equals(itemDto.getBeverageId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + itemDto.getQuantity());
            } else {
                Beverage beverage = beverageRepository.findById(itemDto.getBeverageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Beverage not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setBeverage(beverage);
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setDelivered(false);

                order.getItems().add(orderItem);
            }
        }

        order.setTotal(orderService.calculateOrderTotal(order));
        Order savedOrder = orderRepository.save(order);

        return new ResponseEntity<>(new ApiResponse<>(true, "Order created successfully", savedOrder), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> updateOrderItemStatus(@PathVariable Long orderId,
                                                                @PathVariable Long itemId,
                                                                @RequestParam Boolean delivered) {
        ApiResponse<?> response = orderService.updateOrderItemStatus(orderId, itemId, delivered);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/close")
    public ResponseEntity<ApiResponse<?>> closeOrder(@PathVariable Long orderId, Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ApiResponse<?> response = orderService.closeOrder(orderId, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> deleteOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        ApiResponse<?> response = orderService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.ok(response);
    }
}
