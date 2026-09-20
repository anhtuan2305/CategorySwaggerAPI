package vn.tuan.categoryswaggerapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.tuan.categoryswaggerapi.entity.Product;
import vn.tuan.categoryswaggerapi.repository.ProductRepository;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    @Override
    public Product save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product không được null."
            );
        }

        if (product.getProductName() != null) {
            product.setProductName(
                    product.getProductName().trim()
            );
        }

        if (product.getDescription() != null) {
            product.setDescription(
                    product.getDescription().trim()
            );
        }

        if (product.getImages() != null) {
            String images = product.getImages().trim();

            product.setImages(
                    images.isEmpty() ? null : images
            );
        }

        if (product.getQuantity() == null) {
            product.setQuantity(0);
        }

        if (product.getUnitPrice() == null) {
            product.setUnitPrice(0.0);
        }

        if (product.getDiscount() == null) {
            product.setDiscount(0.0);
        }

        if (product.getStatus() == null) {
            product.setStatus((short) 1);
        }

        /*
         * Khi cập nhật mà không chọn ảnh hoặc Category mới,
         * giữ lại dữ liệu cũ.
         */
        if (product.getProductId() != null) {
            Optional<Product> oldProduct =
                    productRepository.findById(
                            product.getProductId()
                    );

            if (oldProduct.isPresent()) {
                Product old = oldProduct.get();

                if (product.getImages() == null) {
                    product.setImages(
                            old.getImages()
                    );
                }

                if (product.getCategory() == null) {
                    product.setCategory(
                            old.getCategory()
                    );
                }

                if (product.getCreateDate() == null) {
                    product.setCreateDate(
                            old.getCreateDate()
                    );
                }
            }
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByProductName(
            String name) {

        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return productRepository
                .findByProductNameIgnoreCase(
                        name.trim()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByCreateDate(
            LocalDateTime createDate) {

        if (createDate == null) {
            return Optional.empty();
        }

        return productRepository.findByCreateDate(
                createDate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(
            Pageable pageable) {

        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll(
            Sort sort) {

        return productRepository.findAll(sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByProductNameContaining(
            String name) {

        String keyword =
                name == null ? "" : name.trim();

        return productRepository
                .findByProductNameContainingIgnoreCase(
                        keyword
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findByProductNameContaining(
            String name,
            Pageable pageable) {

        String keyword =
                name == null ? "" : name.trim();

        return productRepository
                .findByProductNameContainingIgnoreCase(
                        keyword,
                        pageable
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategoryId(
            Long categoryId) {

        if (categoryId == null) {
            return List.of();
        }

        return productRepository
                .findByCategoryCategoryId(
                        categoryId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return productRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }
}