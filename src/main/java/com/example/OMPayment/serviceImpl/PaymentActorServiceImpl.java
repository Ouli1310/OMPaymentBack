package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.PaymentActor;
import com.example.OMPayment.repository.PaymentActorRepository;
import com.example.OMPayment.service.PaymentActorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PaymentActorServiceImpl implements PaymentActorService {

    private final PaymentActorRepository paymentActorRepository;

    @Override
    public PaymentActor getByMsisdn(String msisdn) {
        return paymentActorRepository.findByMsisdn(msisdn);
    }
}
