package com.example.OMPayment.payload.response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }


}
