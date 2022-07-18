package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Base64;

@Getter @Setter
@ToString
public class Customer {

    private String idType;
    private String id;
    private String encryptedPinCode;
    private String otp;

    public Customer(String idType, String id, String encryptedPinCode) {
        this.idType = idType;
        this.id = id;
        this.encryptedPinCode = encryptedPinCode;
    }

    public Customer() {

    }

}
