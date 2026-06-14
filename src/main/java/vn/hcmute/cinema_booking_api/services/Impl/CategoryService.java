package vn.hcmute.cinema_booking_api.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.category.CategoryResponse;
import vn.hcmute.cinema_booking_api.entity.Category;
import vn.hcmute.cinema_booking_api.exception.BadRequestException;
import vn.hcmute.cinema_booking_api.repositories.CategoryRepository;
import vn.hcmute.cinema_booking_api.services.ICategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    // Get list
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mappingToCategoryResponse)
                .toList();
    }

    // Get category by id
    @Override
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new BadRequestException("Category not found!"));
        return mappingToCategoryResponse(category);
    }

    // Create category
    @Override
    public CategoryResponse create(String name) {
        categoryRepository.findByCategoryNameIgnoreCase(name)
                .ifPresent(c -> {
                    throw new BadRequestException("Category already exists");
                });
        return mappingToCategoryResponse(categoryRepository.save(Category.builder().categoryName(name).build()));
    }

    // Update
    @Override
    public CategoryResponse update(Long id, String newName) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new BadRequestException("Category not found!"));

        categoryRepository.findByCategoryNameIgnoreCase(newName)
                .ifPresent(c -> {
                    if (!c.getCategoryId().equals(id)) {
                        throw new BadRequestException("Category already exists");
                    }
                });

        category.setCategoryName(newName);
        return mappingToCategoryResponse(categoryRepository.save(category));
    }

    // Helper
    private CategoryResponse mappingToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getCategoryId())
                .name(category.getCategoryName())
                .build();
    }
}