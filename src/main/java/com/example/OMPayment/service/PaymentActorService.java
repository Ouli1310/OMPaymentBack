package com.example.OMPayment.service;

import com.example.OMPayment.model.PaymentActor;

public interface PaymentActorService {

    PaymentActor getByMsisdn(String msisdn);
}
