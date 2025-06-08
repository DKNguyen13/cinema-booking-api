package vn.hcmute.cinema_booking_api.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hcmute.cinema_booking_api.dto.DiscountDTO;
import vn.hcmute.cinema_booking_api.entity.Discount;
import vn.hcmute.cinema_booking_api.repository.DiscountRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Discount discount : discounts) {
            if ((discount.getEndDate().isBefore(today) && Boolean.TRUE.equals(discount.getActive())) ||
                    (discount.getQuantity() != null && discount.getQuantity() == 0 && Boolean.TRUE.equals(discount.getActive()))) {
                discount.setActive(false);
                discountRepository.save(discount);
            }
        }
        return discountRepository.findAll();
    }

    public List<Discount> findAllDiscountInactive() {
        return discountRepository.findDiscountsByActive(false);
    }

    public List<DiscountDTO> findAllDiscountActive() {
        List<Discount> discounts = discountRepository.findDiscountsByActive(true);
        LocalDate today = LocalDate.now();
        for (Discount discount : discounts) {
            if ((discount.getEndDate().isBefore(today) && Boolean.TRUE.equals(discount.getActive())) ||
                    (discount.getQuantity() != null && discount.getQuantity() == 0 && Boolean.TRUE.equals(discount.getActive()))) {
                discount.setActive(false);
                discountRepository.save(discount);
            }
        }

        List<Discount> stillActive = discountRepository.findDiscountsByActive(true);

        return stillActive.stream()
                .map(d -> {
                    DiscountDTO dto = new DiscountDTO();
                    dto.setActive(true);
                    dto.setDiscountCode(d.getDiscountCode());
                    dto.setDescription(d.getDescription());
                    dto.setFixedAmount(d.getFixedAmount());
                    dto.setQuantity(d.getQuantity());
                    dto.setPercentage(d.getPercentage());
                    dto.setStartDate(d.getStartDate());
                    dto.setEndDate(d.getEndDate());
                    return dto;
                }).collect(Collectors.toList());
    }

    public void creatDiscount(DiscountDTO dto) {
        Discount discount = new Discount();
        discount.setDiscountCode(dto.getDiscountCode());
        discount.setDescription(dto.getDescription());
        discount.setFixedAmount(dto.getFixedAmount());
        discount.setActive(dto.getActive());
        discount.setQuantity(dto.getQuantity());
        discount.setPercentage(dto.getPercentage());
        discount.setStartDate(dto.getStartDate());
        discount.setEndDate(dto.getEndDate());
        discountRepository.save(discount);
    }

    public boolean deleteDiscount(Long id){
        Optional<Discount> discount = discountRepository.findById(id);
        if(discount.isEmpty()){
            return false;
        }
        discountRepository.deleteById(id);
        return true;
    }
}
