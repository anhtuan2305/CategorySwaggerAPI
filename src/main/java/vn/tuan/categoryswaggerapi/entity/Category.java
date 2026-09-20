package vn.tuan.categoryswaggerapi.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Categories")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(
            message = "Tên danh mục không được để trống."
    )
    @Size(
            max = 255,
            message = "Tên danh mục tối đa 255 ký tự."
    )
    @Column(
            name = "category_name",
            nullable = false,
            length = 255,
            columnDefinition = "NVARCHAR(255)"
    )
    private String categoryName;

    @Column(
            name = "icon",
            length = 500
    )
    private String icon;

    @JsonIgnore
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Product> products =
            new HashSet<>();

    public Category() {
    }

    public Category(
            Long categoryId,
            String categoryName,
            String icon) {

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.icon = icon;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(
            Long categoryId) {

        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(
            String categoryName) {

        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(
            String icon) {

        this.icon = icon;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(
            Set<Product> products) {

        this.products = products;
    }
}