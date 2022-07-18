package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthRequest {

    private String grant_type;
    private String client_id;
    private String client_secret;

}
