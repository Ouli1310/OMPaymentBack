package com.example.OMPayment.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Money {

    private String unit;
    private Long value;

}
