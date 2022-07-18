package com.example.OMPayment.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String msisdn;
    private String code;
    private Long profil;
    private Long entite;
    private String password;




}
