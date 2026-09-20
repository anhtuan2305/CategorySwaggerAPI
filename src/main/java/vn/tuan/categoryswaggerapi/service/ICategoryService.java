package vn.tuan.categoryswaggerapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import vn.tuan.categoryswaggerapi.entity.Category;

public interface ICategoryService {

    Category save(Category category);

    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryName(
            String name
    );

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    List<Category> findAll(Sort sort);

    List<Category>
            findByCategoryNameContaining(
                    String name
            );

    Page<Category>
            findByCategoryNameContaining(
                    String name,
                    Pageable pageable
            );

    boolean existsByCategoryName(
            String name
    );

    long count();

    void deleteById(Long id);

    void delete(Category category);
}