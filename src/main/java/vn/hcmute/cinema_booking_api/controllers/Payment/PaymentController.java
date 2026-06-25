package vn.hcmute.cinema_booking_api.controllers.Payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.cinema_booking_api.dto.payment.CreatePaymentRequest;
import vn.hcmute.cinema_booking_api.dto.payment.CreatePaymentResponse;
import vn.hcmute.cinema_booking_api.services.Impl.PaymentService;
import vn.hcmute.cinema_booking_api.utils.ApiResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/vnpay")
public class PaymentController {

    private final PaymentService vnpayPaymentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPayment(@Valid @RequestBody CreatePaymentRequest request, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.success("Create VNPay payment successful", vnpayPaymentService.createPayment(request, servletRequest)));
    }

    @GetMapping("/return")
    public void paymentReturn(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        String redirectUrl = vnpayPaymentService.handleReturn(request);
        response.sendRedirect(redirectUrl);
    }
}