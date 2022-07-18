package com.example.OMPayment.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private Long profil;

    public JwtResponse(String accessToken, Long id, String email, Long profil) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.profil = profil;

    }

}
