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
import vn.tuan.categoryswaggerapi.model.Response;
import vn.tuan.categoryswaggerapi.service.ICategoryService;
import vn.tuan.categoryswaggerapi.service.IStorageService;

@RestController
@RequestMapping("/api/category")
@Tag(
        name = "Category API",
        description = "CRUD API quản lý danh mục sản phẩm"
)
public class CategoryAPIController {

    private final ICategoryService categoryService;

    private final IStorageService storageService;

    public CategoryAPIController(
            ICategoryService categoryService,
            IStorageService storageService) {

        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả danh mục"
    )
    public ResponseEntity<Response>
            getAllCategories() {

        List<Category> categories =
                categoryService.findAll();

        Response response = new Response(
                true,
                "Lấy danh sách Category thành công.",
                categories
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/getCategory")
    @Operation(
            summary = "Lấy danh mục theo mã"
    )
    public ResponseEntity<Response> getCategory(
            @RequestParam("id") Long id) {

        Optional<Category> category =
                categoryService.findById(id);

        if (category.isEmpty()) {

            Response response = new Response(
                    false,
                    "Không tìm thấy Category.",
                    null
            );

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        Response response = new Response(
                true,
                "Tìm thấy Category.",
                category.get()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/addCategory",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Thêm danh mục mới"
    )
    public ResponseEntity<Response> addCategory(
            @RequestParam("categoryName")
            String categoryName,

            @RequestParam(
                    value = "icon",
                    required = false
            )
            MultipartFile icon) {

        String normalizedName =
                normalizeName(categoryName);

        if (normalizedName == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new Response(
                                    false,
                                    "Tên Category không được để trống.",
                                    null
                            )
                    );
        }

        if (categoryService.existsByCategoryName(
                normalizedName)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new Response(
                                    false,
                                    "Category đã tồn tại trong hệ thống.",
                                    null
                            )
                    );
        }

        Category category = new Category();

        category.setCategoryName(
                normalizedName
        );

        if (icon != null && !icon.isEmpty()) {

            String storedFilename =
                    storageService
                            .getStorageFilename(
                                    icon,
                                    UUID.randomUUID()
                                            .toString()
                            );

            storageService.store(
                    icon,
                    storedFilename
            );

            category.setIcon(storedFilename);
        }

        Category savedCategory =
                categoryService.save(category);

        Response response = new Response(
                true,
                "Thêm Category thành công.",
                savedCategory
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping(
            value = "/updateCategory",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Cập nhật danh mục"
    )
    public ResponseEntity<Response> updateCategory(
            @RequestParam("categoryId")
            Long categoryId,

            @RequestParam("categoryName")
            String categoryName,

            @RequestParam(
                    value = "icon",
                    required = false
            )
            MultipartFile icon) {

        Optional<Category> optionalCategory =
                categoryService.findById(
                        categoryId
                );

        if (optionalCategory.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new Response(
                                    false,
                                    "Không tìm thấy Category.",
                                    null
                            )
                    );
        }

        String normalizedName =
                normalizeName(categoryName);

        if (normalizedName == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new Response(
                                    false,
                                    "Tên Category không được để trống.",
                                    null
                            )
                    );
        }

        Optional<Category> categoryWithSameName =
                categoryService
                        .findByCategoryName(
                                normalizedName
                        );

        if (categoryWithSameName.isPresent()
                && !categoryWithSameName
                        .get()
                        .getCategoryId()
                        .equals(categoryId)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new Response(
                                    false,
                                    "Tên Category đã tồn tại.",
                                    null
                            )
                    );
        }

        Category category =
                optionalCategory.get();

        String oldIcon = category.getIcon();

        category.setCategoryName(
                normalizedName
        );

        if (icon != null && !icon.isEmpty()) {

            String newIcon =
                    storageService
                            .getStorageFilename(
                                    icon,
                                    UUID.randomUUID()
                                            .toString()
                            );

            storageService.store(
                    icon,
                    newIcon
            );

            category.setIcon(newIcon);
        }

        Category updatedCategory =
                categoryService.save(category);

        if (icon != null
                && !icon.isEmpty()
                && oldIcon != null
                && !oldIcon.isBlank()) {

            storageService.delete(oldIcon);
        }

        Response response = new Response(
                true,
                "Cập nhật Category thành công.",
                updatedCategory
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteCategory")
    @Operation(
            summary = "Xóa danh mục"
    )
    public ResponseEntity<Response> deleteCategory(
            @RequestParam("categoryId")
            Long categoryId) {

        Optional<Category> optionalCategory =
                categoryService.findById(
                        categoryId
                );

        if (optionalCategory.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new Response(
                                    false,
                                    "Không tìm thấy Category.",
                                    null
                            )
                    );
        }

        Category category =
                optionalCategory.get();

        String icon = category.getIcon();

        categoryService.delete(category);

        if (icon != null && !icon.isBlank()) {
            storageService.delete(icon);
        }

        Response response = new Response(
                true,
                "Xóa Category thành công.",
                category
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{filename:.+}")
    @Operation(
            summary = "Xem file icon của danh mục"
    )
    public ResponseEntity<Resource> viewIcon(
            @PathVariable String filename) {

        Resource resource =
                storageService.loadAsResource(
                        filename
                );

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

    private String normalizeName(
            String categoryName) {

        if (categoryName == null) {
            return null;
        }

        String normalizedName =
                categoryName.trim();

        return normalizedName.isEmpty()
                ? null
                : normalizedName;
    }
}