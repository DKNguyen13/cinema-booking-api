package vn.hcmute.cinema_booking_api.services;

import vn.hcmute.cinema_booking_api.dto.category.CategoryResponse;
import vn.hcmute.cinema_booking_api.entity.Category;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getById(Long id);
    CategoryResponse create(String name);
    CategoryResponse update(Long id, String newName);
}
