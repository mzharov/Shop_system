package ts.tsc.system.dto;

public class OwnerProductReportDTO {
    private Long ownerID;
    private Long productID;
    private Long count;

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public OwnerProductReportDTO(Long ownerID, Long productID, Long count) {
        this.ownerID = ownerID;
        this.productID = productID;
        this.count = count;
    }
}
