package org.example.ecommerce.controller;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.dto.CreateOrderRequest;
import org.example.ecommerce.dto.OrderDTO;
import org.example.ecommerce.dto.PagedResponse;
import org.example.ecommerce.entity.OrderStatus;
import org.example.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/v1/orders - user: {}", request.getUserId());
        OrderDTO created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        log.info("GET /api/v1/orders/{}", id);
        OrderDTO order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<OrderDTO>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/orders/user/{}", userId);
        PagedResponse<OrderDTO> orders = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        log.info("PATCH /api/v1/orders/{}/status - status: {}", id, status);
        OrderDTO updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
