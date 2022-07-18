package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PayingRequest {

    private String msisdn;
    private String merchantCode;
    private String pinCode;
    private String type;
    private String grade;
}
