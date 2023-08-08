package com.example.OMPayment.payload.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.security.PrivateKey;

@Getter
@Setter
@ToString
public class QrcodeRequest implements Serializable {

    private String callbackCancelUrl;

    private String callbackSuccessUrl;

    private String code;

    private String name;

    private String validity;
}
