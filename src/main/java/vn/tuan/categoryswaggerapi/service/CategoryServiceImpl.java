package vn.tuan.categoryswaggerapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.tuan.categoryswaggerapi.entity.Category;
import vn.tuan.categoryswaggerapi.repository.CategoryRepository;

@Service
@Transactional
public class CategoryServiceImpl
        implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository) {

        this.categoryRepository =
                categoryRepository;
    }

    @Override
    public Category save(Category category) {

        if (category == null) {

            throw new IllegalArgumentException(
                    "Category không được null."
            );
        }

        if (category.getCategoryName() != null) {

            category.setCategoryName(
                    category.getCategoryName().trim()
            );
        }

        if (category.getIcon() != null) {

            String icon =
                    category.getIcon().trim();

            category.setIcon(
                    icon.isEmpty() ? null : icon
            );
        }

        if (category.getCategoryId() != null) {

            Optional<Category> oldCategory =
                    categoryRepository.findById(
                            category.getCategoryId()
                    );

            if (oldCategory.isPresent()
                    && category.getIcon() == null) {

                category.setIcon(
                        oldCategory.get().getIcon()
                );
            }
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {

        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findByCategoryName(
            String name) {

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return categoryRepository
                .findByCategoryNameIgnoreCase(
                        name.trim()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {

        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> findAll(
            Pageable pageable) {

        return categoryRepository.findAll(
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll(
            Sort sort) {

        return categoryRepository.findAll(sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category>
            findByCategoryNameContaining(
                    String name) {

        String keyword =
                name == null ? "" : name.trim();

        return categoryRepository
                .findByCategoryNameContainingIgnoreCase(
                        keyword
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category>
            findByCategoryNameContaining(
                    String name,
                    Pageable pageable) {

        String keyword =
                name == null ? "" : name.trim();

        return categoryRepository
                .findByCategoryNameContainingIgnoreCase(
                        keyword,
                        pageable
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(
            String name) {

        if (name == null || name.isBlank()) {
            return false;
        }

        return categoryRepository
                .existsByCategoryNameIgnoreCase(
                        name.trim()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {

        return categoryRepository.count();
    }

    @Override
    public void deleteById(Long id) {

        categoryRepository.deleteById(id);
    }

    @Override
    public void delete(Category category) {

        categoryRepository.delete(category);
    }
}