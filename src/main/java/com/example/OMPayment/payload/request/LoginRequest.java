package com.example.OMPayment.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.validation.constraints.*;

@Getter @Setter
public class LoginRequest {

    private String email;
    private String password;
}
