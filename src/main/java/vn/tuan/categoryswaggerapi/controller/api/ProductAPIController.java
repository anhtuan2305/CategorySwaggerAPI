package vn.tuan.categoryswaggerapi.controller.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import vn.tuan.categoryswaggerapi.entity.Category;
import vn.tuan.categoryswaggerapi.entity.Product;
import vn.tuan.categoryswaggerapi.model.ProductDTO;
import vn.tuan.categoryswaggerapi.model.Response;
import vn.tuan.categoryswaggerapi.service.ICategoryService;
import vn.tuan.categoryswaggerapi.service.IProductService;
import vn.tuan.categoryswaggerapi.service.IStorageService;

@RestController
@RequestMapping("/api/product")
@Tag(
        name = "Product API",
        description = "CRUD API quản lý sản phẩm"
)
public class ProductAPIController {

    private final IProductService productService;

    private final ICategoryService categoryService;

    private final IStorageService storageService;

    public ProductAPIController(
            IProductService productService,
            ICategoryService categoryService,
            IStorageService storageService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    /*
     * Lấy toàn bộ sản phẩm.
     */
    @GetMapping
    @Operation(summary = "Lấy tất cả sản phẩm")
    public ResponseEntity<Response> getAllProducts() {

        List<ProductDTO> products =
                productService.findAll()
                        .stream()
                        .map(ProductDTO::fromEntity)
                        .toList();

        Response response = new Response(
                true,
                "Lấy danh sách Product thành công.",
                products
        );

        return ResponseEntity.ok(response);
    }

    /*
     * Lấy một sản phẩm theo ID.
     */
    @PostMapping("/getProduct")
    @Operation(summary = "Lấy sản phẩm theo mã")
    public ResponseEntity<Response> getProduct(
            @RequestParam("id") Long id) {

        Optional<Product> optionalProduct =
                productService.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new Response(
                                    false,
                                    "Không tìm thấy Product.",
                                    null
                            )
                    );
        }

        ProductDTO productDTO =
                ProductDTO.fromEntity(
                        optionalProduct.get()
                );

        return ResponseEntity.ok(
                new Response(
                        true,
                        "Tìm thấy Product.",
                        productDTO
                )
        );
    }

    /*
     * Thêm sản phẩm.
     */
    @PostMapping(
            value = "/addProduct",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Thêm sản phẩm mới")
    public ResponseEntity<Response> addProduct(

            @RequestParam("productName")
            String productName,

            @RequestParam("quantity")
            Integer quantity,

            @RequestParam("unitPrice")
            Double unitPrice,

            @RequestParam("description")
            String description,

            @RequestParam(
                    value = "discount",
                    defaultValue = "0"
            )
            Double discount,

            @RequestParam(
                    value = "status",
                    defaultValue = "1"
            )
            Short status,

            @RequestParam("categoryId")
            Long categoryId,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image) {

        String normalizedName =
                normalizeText(productName);

        String normalizedDescription =
                normalizeText(description);

        if (normalizedName == null) {
            return badRequest(
                    "Tên Product không được để trống."
            );
        }

        if (normalizedDescription == null) {
            return badRequest(
                    "Mô tả Product không được để trống."
            );
        }

        if (quantity == null || quantity < 0) {
            return badRequest(
                    "Số lượng phải lớn hơn hoặc bằng 0."
            );
        }

        if (unitPrice == null || unitPrice < 0) {
            return badRequest(
                    "Đơn giá phải lớn hơn hoặc bằng 0."
            );
        }

        if (discount == null ||
                discount < 0 ||
                discount > 100) {

            return badRequest(
                    "Giảm giá phải nằm trong khoảng 0 đến 100."
            );
        }

        Optional<Category> optionalCategory =
                categoryService.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return badRequest(
                    "Category không tồn tại."
            );
        }

        if (productService
                .findByProductName(normalizedName)
                .isPresent()) {

            return badRequest(
                    "Tên Product đã tồn tại."
            );
        }

        Product product = new Product();

        product.setProductName(normalizedName);
        product.setQuantity(quantity);
        product.setUnitPrice(unitPrice);
        product.setDescription(normalizedDescription);
        product.setDiscount(discount);
        product.setStatus(status);
        product.setCategory(optionalCategory.get());

        if (image != null && !image.isEmpty()) {
            String storedFilename =
                    storageService.getStorageFilename(
                            image,
                            UUID.randomUUID().toString()
                    );

            storageService.store(
                    image,
                    storedFilename
            );

            product.setImages(storedFilename);
        }

        Product savedProduct =
                productService.save(product);

        ProductDTO productDTO =
                ProductDTO.fromEntity(savedProduct);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new Response(
                                true,
                                "Thêm Product thành công.",
                                productDTO
                        )
                );
    }

    /*
     * Cập nhật sản phẩm.
     */
    @PutMapping(
            value = "/updateProduct",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Cập nhật sản phẩm")
    public ResponseEntity<Response> updateProduct(

            @RequestParam("productId")
            Long productId,

            @RequestParam("productName")
            String productName,

            @RequestParam("quantity")
            Integer quantity,

            @RequestParam("unitPrice")
            Double unitPrice,

            @RequestParam("description")
            String description,

            @RequestParam(
                    value = "discount",
                    defaultValue = "0"
            )
            Double discount,

            @RequestParam(
                    value = "status",
                    defaultValue = "1"
            )
            Short status,

            @RequestParam("categoryId")
            Long categoryId,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image) {

        Optional<Product> optionalProduct =
                productService.findById(productId);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new Response(
                                    false,
                                    "Không tìm thấy Product.",
                                    null
                            )
                    );
        }

        String normalizedName =
                normalizeText(productName);

        String normalizedDescription =
                normalizeText(description);

        if (normalizedName == null) {
            return badRequest(
                    "Tên Product không được để trống."
            );
        }

        if (normalizedDescription == null) {
            return badRequest(
                    "Mô tả Product không được để trống."
            );
        }

        if (quantity == null || quantity < 0) {
            return badRequest(
                    "Số lượng phải lớn hơn hoặc bằng 0."
            );
        }

        if (unitPrice == null || unitPrice < 0) {
            return badRequest(
                    "Đơn giá phải lớn hơn hoặc bằng 0."
            );
        }

        if (discount == null ||
                discount < 0 ||
                discount > 100) {

            return badRequest(
                    "Giảm giá phải nằm trong khoảng 0 đến 100."
            );
        }

        Optional<Category> optionalCategory =
                categoryService.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return badRequest(
                    "Category không tồn tại."
            );
        }

        Optional<Product> productWithSameName =
                productService.findByProductName(
                        normalizedName
                );

        if (productWithSameName.isPresent()
                && !productWithSameName
                        .get()
                        .getProductId()
                        .equals(productId)) {

            return badRequest(
                    "Tên Product đã tồn tại."
            );
        }

        Product product = optionalProduct.get();

        String oldImage = product.getImages();

        product.setProductName(normalizedName);
        product.setQuantity(quantity);
        product.setUnitPrice(unitPrice);
        product.setDescription(normalizedDescription);
        product.setDiscount(discount);
        product.setStatus(status);
        product.setCategory(optionalCategory.get());

        if (image != null && !image.isEmpty()) {
            String newImage =
                    storageService.getStorageFilename(
                            image,
                            UUID.randomUUID().toString()
                    );

            storageService.store(
                    image,
                    newImage
            );

            product.setImages(newImage);
        }

        Product updatedProduct =
                productService.save(product);

        if (image != null
                && !image.isEmpty()
                && oldImage != null
                && !oldImage.isBlank()) {

            storageService.delete(oldImage);
        }

        ProductDTO productDTO =
                ProductDTO.fromEntity(updatedProduct);

        return ResponseEntity.ok(
                new Response(
                        true,
                        "Cập nhật Product thành công.",
                        productDTO
                )
        );
    }

    /*
     * Xóa sản phẩm.
     */
    @DeleteMapping("/deleteProduct")
    @Operation(summary = "Xóa sản phẩm")
    public ResponseEntity<Response> deleteProduct(

            @RequestParam("productId")
            Long productId) {

        Optional<Product> optionalProduct =
                productService.findById(productId);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new Response(
                                    false,
                                    "Không tìm thấy Product.",
                                    null
                            )
                    );
        }

        Product product = optionalProduct.get();

        ProductDTO deletedProduct =
                ProductDTO.fromEntity(product);

        String image = product.getImages();

        productService.delete(product);

        if (image != null && !image.isBlank()) {
            storageService.delete(image);
        }

        return ResponseEntity.ok(
                new Response(
                        true,
                        "Xóa Product thành công.",
                        deletedProduct
                )
        );
    }

    /*
     * Xem ảnh sản phẩm.
     */
    @GetMapping("/files/{filename:.+}")
    @Operation(summary = "Xem ảnh sản phẩm")
    public ResponseEntity<Resource> viewImage(

            @PathVariable String filename) {

        Resource resource =
                storageService.loadAsResource(filename);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + resource.getFilename()
                                + "\""
                )
                .body(resource);
    }

    private ResponseEntity<Response> badRequest(
            String message) {

        return ResponseEntity
                .badRequest()
                .body(
                        new Response(
                                false,
                                message,
                                null
                        )
                );
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();

        return normalizedValue.isEmpty()
                ? null
                : normalizedValue;
    }
}