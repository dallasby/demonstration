package customer.demonstration.handlers;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Update;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import customer.demonstration.util.OrderValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import cds.gen.ordersservice.OrdersService_;
import cds.gen.ordersservice.CreateOrderContext;
import cds.gen.ordersservice.DeleteOrderContext;
import cds.gen.ordersservice.GetOrdersByUserContext;
import cds.gen.adminservice.Users;
import cds.gen.adminservice.Products_;
import cds.gen.adminservice.Products;
import cds.gen.ordersservice.Orders;
import cds.gen.ordersservice.Orders_;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@ServiceName(OrdersService_.CDS_NAME)
@Slf4j
@RequiredArgsConstructor
public class OrderServiceHandler implements EventHandler {
    private final PersistenceService db;
    private final OrderValidationService validationService;

    @On(event = "createOrder")
    @Transactional
    public void onCreateOrder(CreateOrderContext context) {
        log.info("Creating order...");

        Integer quantity = context.getQuantity();
        Users user = validationService.validateUsers(context.getUser());
        Products product = validationService.validateProducts(context.getProduct(), quantity);

        product.setStock(product.getStock() - quantity);
        db.run(Update.entity(Products_.CDS_NAME).data(product));

        Orders order = Orders.create();
        order.setId(context.getId());
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice());
        order.setUser(user);
        order.setProductId(product.getId());

        Orders savedOrder = db.run(Insert.into(Orders_.class).entry(order)).single().as(Orders.class);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        context.setCompleted();
    }

    @After(event = CqnService.EVENT_READ, entity = Orders_.CDS_NAME)
    public void addDiscountIfApplicable(List<Orders> orders) {
        log.info("Adding discount for orders #{}", orders.size());
        for (Orders order : orders) {
            if (order.getTotalPrice().compareTo(BigDecimal.valueOf(10000.00)) > 0) {
                log.info("Applying a 10% discount for order ID: {}", order.getId());
                BigDecimal discountRate = BigDecimal.valueOf(0.10);
                BigDecimal discountAmount = order.getTotalPrice().multiply(discountRate);
                order.setTotalPrice(order.getTotalPrice().subtract(discountAmount));
            }
        }
    }

    @On(event = "deleteOrder")
    @Transactional
    public void onDeleteOrder(DeleteOrderContext context) {
        log.info("Deleting order...");

        Integer orderId = context.getId();
        validationService.validateOrders(orderId);

        db.run(Delete.from(Orders_.class).where(o -> o.get("ID").eq(orderId)));

        context.setCompleted();
    }

    @On(event = "getOrdersByUser")
    public void onGetOrdersByUser(GetOrdersByUserContext context) {
        Integer userId = context.getUser();
        log.info("Retrieving orders by ID: {}", userId);

        List<Orders> orders = validationService.getOrdersByUser(userId);

        context.setResult(orders);
    }
}
