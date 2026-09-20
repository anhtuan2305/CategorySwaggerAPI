package vn.tuan.categoryswaggerapi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuan.categoryswaggerapi.entity.Product;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product>
            findByProductNameContainingIgnoreCase(
                    String name
            );

    Page<Product>
            findByProductNameContainingIgnoreCase(
                    String name,
                    Pageable pageable
            );

    Optional<Product>
            findByProductNameIgnoreCase(
                    String name
            );

    Optional<Product>
            findByCreateDate(
                    LocalDateTime createDate
            );

    List<Product>
            findByCategoryCategoryId(
                    Long categoryId
            );
}