package com.gdg.backend.wallet.controller;

import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.wallet.dto.WalletBalanceResponse;
import com.gdg.backend.wallet.dto.WalletChargeRequest;
import com.gdg.backend.wallet.dto.WalletTransactionResponse;
import com.gdg.backend.wallet.dto.WalletUseRequest;
import com.gdg.backend.wallet.service.WalletService;
import com.gdg.backend.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getMyBalance(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.userId();

        long balance = walletService.getBalance(userId);
        return ApiResponse.success(new WalletBalanceResponse(balance));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getMyTransactions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.userId();
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                walletService.getMyTransactions(userId)
        );
    }

    @PostMapping("/use")
    public ResponseEntity<ApiResponse<Object>> usePoint(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody WalletUseRequest request
    ) {
        Long userId = principal.userId();
        walletService.use(
                userId,
                request.getAmount(),
                request.getReason()
        );
        return ApiResponse.success(SuccessCode.OK);
    }

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<Object>> charge(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody WalletChargeRequest request
    ) {
        Long userId = principal.userId();

        walletService.charge(
                userId,
                request.getAmount()
        );

        return ApiResponse.success(SuccessCode.OK);
    }

}
