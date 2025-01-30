package customer.demonstration.util;

import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cds.gen.adminservice.Users_;
import cds.gen.adminservice.Users;
import cds.gen.adminservice.Products_;
import cds.gen.adminservice.Products;
import cds.gen.ordersservice.Orders;
import cds.gen.ordersservice.Orders_;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderValidationService {
    private final PersistenceService db;

    public Users validateUsers(Integer userId) {
        return db.run(Select.from(Users_.class).byId(userId))
                .first(Users.class)
                .orElseThrow(() -> {
                    log.error("User with ID {} does not exist.", userId);
                    return new IllegalArgumentException("User does not exist.");
                });
    }

    public Products validateProducts(Integer productId, Integer quantity) {
        Products product = db.run(Select.from(Products_.class).byId(productId))
                .first(Products.class)
                .orElseThrow(() -> {
                    log.error("Product with ID {} does not exist.", productId);
                    return new IllegalArgumentException("Product does not exist.");
                });

        if (product == null) {
            log.error("Product with ID {} does not exist.", productId);
            throw new IllegalArgumentException("Product does not exist.");
        }

        if (quantity <= 0) {
            log.error("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        } else if (quantity > product.getStock()) {
            log.error("Requested quantity exceeds stock for product #{}", product.getId());
            throw new IllegalArgumentException("Not enough stock available.");
        }
        return product;
    }

    public void validateOrders(Integer orderId) {
        db.run(Select.from(Orders_.class).byId(orderId))
                .first(Orders.class)
                .orElseThrow(() -> {
                    log.error("Order with ID {} does not exist.", orderId);
                    return new IllegalArgumentException("Order does not exist.");
                });
    }

    public List<Orders> getOrdersByUser(Integer userId) {
        return db.run(Select.from(Orders_.class)
                        .where(orders -> orders.user_ID().eq(userId)))
                .listOf(Orders.class);
    }
}
