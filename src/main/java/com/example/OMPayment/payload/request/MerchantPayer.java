package com.example.OMPayment.payload.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MerchantPayer {

    private String idType;
    private String id;
    private String otp;
}
