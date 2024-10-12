package com.metasoft.pointbarmetasoft.salesmanagement.application.services.Implements;

import com.metasoft.pointbarmetasoft.beveragemanagement.domain.entities.Beverage;
import com.metasoft.pointbarmetasoft.beveragemanagement.infraestructure.repositories.BeverageRepository;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.domain.entities.SalesHistory;
import com.metasoft.pointbarmetasoft.saleshistorymanagement.infraestructure.repositories.SalesHistoryRepository;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.CreateOrderRequestDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.requestDto.OrderItemDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto.OrderItemResponseDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.dtos.responseDto.OrderResponseDto;
import com.metasoft.pointbarmetasoft.salesmanagement.application.services.IOrderService;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.Order;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.OrderItem;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.entities.Table;
import com.metasoft.pointbarmetasoft.salesmanagement.domain.enums.OrderStatus;
import com.metasoft.pointbarmetasoft.salesmanagement.infraestructure.repositories.OrderRepository;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import com.metasoft.pointbarmetasoft.shared.model.dto.response.ApiResponse;
import com.metasoft.pointbarmetasoft.tablemanagement.domain.entities.TableSpace;
import com.metasoft.pointbarmetasoft.tablemanagement.infraestructure.repositories.TableSpaceRepository;
//import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final TableSpaceRepository tableSpaceRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final BeverageRepository beverageRepository;
    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, TableSpaceRepository tableSpaceRepository, SalesHistoryRepository salesHistoryRepository,
                            BeverageRepository beverageRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.tableSpaceRepository = tableSpaceRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.beverageRepository = beverageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ApiResponse<?> createOrder(CreateOrderRequestDto requestDto, UserEntity employee) {
        log.info("TableId recibido: " + requestDto.getTableId());
        log.info("TableSpaceId recibido: " + requestDto.getTableSpaceId());

        TableSpace tableSpace = tableSpaceRepository.findById(requestDto.getTableSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Table space not found"));

        Optional<Order> existingOrderOpt = orderRepository.findByTableSpaceIdAndTableId(tableSpace.getId(), requestDto.getTableId());
        Order order = existingOrderOpt.orElse(new Order());

        order.setTableSpace(tableSpace);
        order.setEmployee(employee);
        order.setBusiness(employee.getBusiness());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);

        Map<Long, OrderItem> itemsMap = order.getItems().stream()
                .collect(Collectors.toMap(item -> item.getBeverage().getId(), item -> item));

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

        order.getItems().forEach(item -> {
            item.setQuantity(item.getQuantity());
        });

        order.setTotal(calculateOrderTotal(order));
        Order savedOrder = orderRepository.save(order);
        return new ApiResponse<>(true , "Order created successfully", modelMapper.map(savedOrder, OrderResponseDto.class));
    }


    @Override
    @Transactional
    public ApiResponse<?> addItemToOrder(Long orderId, OrderItemDto orderItemDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        addItemToOrder(order, orderItemDto);
        Order updatedOrder = orderRepository.save(order);

        return new ApiResponse<>(true, "Item added successfully", modelMapper.map(updatedOrder, OrderResponseDto.class));
    }

    private void addItemToOrder(Order order, OrderItemDto itemDto) {
        Beverage beverage = beverageRepository.findById(itemDto.getBeverageId())
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBeverage(beverage);
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setDelivered(false);

        order.getItems().add(orderItem);
    }

    @Override
    @Transactional
    public ApiResponse<?> closeOrder(Long orderId, UserEntity employee) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(OrderStatus.CLOSED);
        Order closedOrder = orderRepository.save(order);

        SalesHistory salesHistory = new SalesHistory();
        salesHistory.setEmployee(employee);
        salesHistory.setBusiness(employee.getBusiness());
        salesHistory.setTableName(order.getTableSpace().getName());
        salesHistory.setAmount(order.getTotal());
        salesHistory.setSaleDate(LocalDateTime.now());
        salesHistoryRepository.save(salesHistory);
        return new ApiResponse<>(true, "Order closed and sale recorded successfully", modelMapper.map(closedOrder, OrderResponseDto.class));
    }

    @Override
    public List<OrderResponseDto> getOrdersByTableSpaceIdAndTableId(Long tableSpaceId, Long tableId, Long businessId) {
        List<Order> orders = orderRepository.findByBusinessIdAndTableSpaceIdAndTableId(businessId, tableSpaceId, tableId);
        return orders.stream()
                .map(this::mapOrderToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getActiveOrders(Long businessId) {
        List<Order> activeOrders = orderRepository.findByBusinessIdAndStatus(businessId, OrderStatus.OPEN);
        return activeOrders.stream()
                .map(this::mapOrderToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse<?> updateOrderItemStatus(Long orderId, Long orderItemId, Boolean delivered) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        orderItem.setDelivered(delivered);
        orderRepository.save(order);
        return new ApiResponse<>(true, "Order item status updated successfully", null);
    }

    private OrderResponseDto mapOrderToResponseDto(Order order) {
        OrderResponseDto dto = modelMapper.map(order, OrderResponseDto.class);
        dto.setTableSpaceId(order.getTableSpace().getId());
        dto.setEmployeeName(order.getEmployee().getFirstname() + " " + order.getEmployee().getLastname());
        dto.setTotal(calculateOrderTotal(order));

        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(this::mapOrderItemToDto)
                .collect(Collectors.toList());

        itemDtos.forEach(itemDto -> {
            itemDto.setQuantity(itemDto.getQuantity());
        });

        dto.setItems(itemDtos);
        return dto;
    }

    private OrderItemResponseDto mapOrderItemToDto(OrderItem orderItem) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setBeverageId(orderItem.getBeverage().getId());
        dto.setBeverageName(orderItem.getBeverage().getName());
        dto.setBeveragePrice(orderItem.getBeverage().getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setDelivered(orderItem.getDelivered());
        dto.setSubtotal(orderItem.getQuantity() * orderItem.getBeverage().getPrice());

        return dto;
    }

    @Override
    public Double calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .mapToDouble(item -> item.getBeverage().getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteOrderItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        order.getItems().remove(orderItem);
        orderRepository.save(order);
        return new ApiResponse<>(true, "Order item deleted successfully", null);
    }
}
