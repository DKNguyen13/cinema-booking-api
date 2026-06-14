package vn.hcmute.cinema_booking_api.controllers.Category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.category.CategoryRequest;
import vn.hcmute.cinema_booking_api.dto.category.CategoryResponse;
import vn.hcmute.cinema_booking_api.services.ICategoryService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService cateService;

    // Get all
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Get all categories successfully", cateService.getAllCategories()));
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Get category successfully", cateService.getById(id)));
    }

    // Create
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Create category successfully", cateService.create(request.getCateName())));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Update category successfully", cateService.update(id, request.getCateName()))
        );
    }
}