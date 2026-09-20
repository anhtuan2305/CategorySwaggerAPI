package vn.tuan.categoryswaggerapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.tuan.categoryswaggerapi.entity.Category;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category>
            findByCategoryNameContainingIgnoreCase(
                    String name
            );

    Page<Category>
            findByCategoryNameContainingIgnoreCase(
                    String name,
                    Pageable pageable
            );

    Optional<Category>
            findByCategoryNameIgnoreCase(
                    String name
            );

    boolean existsByCategoryNameIgnoreCase(
            String name
    );
}