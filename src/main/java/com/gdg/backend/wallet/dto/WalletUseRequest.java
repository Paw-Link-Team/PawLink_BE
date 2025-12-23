package com.gdg.backend.wallet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WalletUseRequest {

    private long amount;
    private String reason;
}
