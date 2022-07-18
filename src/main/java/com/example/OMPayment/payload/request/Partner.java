package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@ToString
public class Partner {

    private String encryptedPinCode;
    private String idType;
    private String id;
    private String wallet;


    public Partner(String idType, String id, String encryptedPinCode, String wallet) {
        this.idType = idType;
        this.id = id;
        this.encryptedPinCode = encryptedPinCode;
        this.wallet = wallet;
    }

    public Partner() {}

}
