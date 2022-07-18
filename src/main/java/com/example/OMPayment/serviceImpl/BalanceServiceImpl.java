package com.example.OMPayment.serviceImpl;


import com.example.OMPayment.model.Balance;
import com.example.OMPayment.repository.BalanceRepository;
import com.example.OMPayment.service.BalanceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;


}
