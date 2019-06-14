package ts.tsc.system.dto;

import ts.tsc.system.controller.status.OrderStatus;

import java.math.BigDecimal;

public class PurchaseReportDTO {
    private OrderStatus orderStatus;
    private Long shopID;
    private BigDecimal sumPrice;

    public PurchaseReportDTO(OrderStatus orderStatus, Long shopID, BigDecimal sumPrice) {
        this.orderStatus = orderStatus;
        this.shopID = shopID;
        this.sumPrice = sumPrice;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getShopID() {
        return shopID;
    }

    public void setShopID(Long shopID) {
        this.shopID = shopID;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }
}
