package com.example.OMPayment.payload.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
@ToString
public class TransactionRequest implements Serializable {


    private String method;
    private Partner partner;
    private Customer customer;
    private Money amount;
    private String reference;
    private Boolean receiveNotification;


}
