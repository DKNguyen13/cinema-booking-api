package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.services.Impl.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try{
            List<Category> cates = categoryService.findAll();
            if(cates.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            else
                return ResponseEntity.ok(ApiResponse.success("Found " + cates.size() + " categories", cates));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/category")
    public ResponseEntity<?> addCategory(@RequestParam String categoryName) {
        try {
            Category category = new Category();
            category.setCategoryName(categoryName);
            categoryService.createCategory(category);
            return ResponseEntity.ok(ApiResponse.success("Category added"));
        }
        catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(500, e.getMessage()));
        }
    }
}
