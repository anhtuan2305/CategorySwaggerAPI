package vn.tuan.categoryswaggerapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.tuan.categoryswaggerapi.entity.Product;

public interface IProductService {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findByProductName(String name);

    Optional<Product> findByCreateDate(
            LocalDateTime createDate
    );

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findAll(Sort sort);

    List<Product> findByProductNameContaining(
            String name
    );

    Page<Product> findByProductNameContaining(
            String name,
            Pageable pageable
    );

    List<Product> findByCategoryId(
            Long categoryId
    );

    long count();

    void deleteById(Long id);

    void delete(Product product);
}