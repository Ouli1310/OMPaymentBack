package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString

public class CallBackRequest implements Serializable {

    private String apiKey;

    private String code;

    private String name;

    private String callbackUrl;

}
