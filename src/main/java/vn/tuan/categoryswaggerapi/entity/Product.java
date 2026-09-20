package vn.tuan.categoryswaggerapi.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(
            name = "product_name",
            nullable = false,
            length = 500,
            columnDefinition = "NVARCHAR(500)"
    )
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(
            name = "unit_price",
            nullable = false
    )
    private Double unitPrice;

    @Column(length = 500)
    private String images;

    @Column(
            nullable = false,
            length = 1000,
            columnDefinition = "NVARCHAR(1000)"
    )
    private String description;

    @Column(nullable = false)
    private Double discount = 0.0;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(nullable = false)
    private Short status = 1;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    public void prePersist() {

        if (createDate == null) {
            createDate = LocalDateTime.now();
        }

        if (discount == null) {
            discount = 0.0;
        }

        if (status == null) {
            status = 1;
        }
    }
}