package ts.tsc.system.dto;

import ts.tsc.system.controller.status.OrderStatus;

import java.math.BigDecimal;

public class DeliveryReportDTO {
    private OrderStatus orderStatus;
    private Long supplierStorageID;
    private BigDecimal sumPrice;

    public DeliveryReportDTO(OrderStatus orderStatus, Long supplierStorageID, BigDecimal sumPrice) {
        this.orderStatus = orderStatus;
        this.supplierStorageID = supplierStorageID;
        this.sumPrice = sumPrice;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getSupplierStorageID() {
        return supplierStorageID;
    }

    public void setSupplierStorageID(Long supplierStorageID) {
        this.supplierStorageID = supplierStorageID;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }
}
