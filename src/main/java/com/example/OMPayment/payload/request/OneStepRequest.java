package com.example.OMPayment.payload.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class OneStepRequest {

    private Partner partner;
    private Customer customer;
    private Money amount;
    private String reference;
}
