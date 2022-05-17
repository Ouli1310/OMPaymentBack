package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.Transaction;
import com.example.OMPayment.repository.TransactionRepository;
import com.example.OMPayment.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> listTransactions() {
        return transactionRepository.findAll();
    }
}
