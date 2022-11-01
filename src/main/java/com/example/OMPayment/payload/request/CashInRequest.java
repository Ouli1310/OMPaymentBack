package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CashInRequest implements Serializable {

    private Partner partner;
    private Customer customer;
    private Money amount;
    private String reference;
    private Boolean receiveNotification;
}
