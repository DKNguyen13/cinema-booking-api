package vn.hcmute.cinema_booking_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.cinema_booking_api.dto.DiscountDTO;
import vn.hcmute.cinema_booking_api.dto.response.ApiResponse;
import vn.hcmute.cinema_booking_api.entity.Discount;
import vn.hcmute.cinema_booking_api.services.Impl.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/discounts")
    public ResponseEntity<?> getAllDiscounts() {
        try {
            List<Discount> discounts = discountService.getAllDiscounts();
            if(discounts.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("All discounts found", discounts));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/discount-inactive")
    public ResponseEntity<?> getDiscountInactive() {
        try {
            List<Discount> discounts = discountService.findAllDiscountInactive();
            if(discounts.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("All inactive discounts found", discounts));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/discount-active")
    public ResponseEntity<?> getDiscountsActive() {
        try {
            List<DiscountDTO> list = discountService.findAllDiscountActive();
            if(list.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("All discounts active found", list));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
}

