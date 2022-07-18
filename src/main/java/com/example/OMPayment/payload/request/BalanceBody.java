package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BalanceBody {

    private String idType;
    private String id;
    private String encryptedPinCode;
    private String wallet;
}
