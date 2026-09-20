package vn.tuan.categoryswaggerapi.model;

import java.time.LocalDateTime;

import vn.tuan.categoryswaggerapi.entity.Product;

public class ProductDTO {

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double unitPrice;

    private String images;

    private String description;

    private Double discount;

    private LocalDateTime createDate;

    private Short status;

    private Long categoryId;

    private String categoryName;

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.quantity = product.getQuantity();
        this.unitPrice = product.getUnitPrice();
        this.images = product.getImages();
        this.description = product.getDescription();
        this.discount = product.getDiscount();
        this.createDate = product.getCreateDate();
        this.status = product.getStatus();

        if (product.getCategory() != null) {
            this.categoryId =
                    product.getCategory().getCategoryId();

            this.categoryName =
                    product.getCategory().getCategoryName();
        }
    }

    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(product);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}