package com.example.OMPayment.service;

import com.example.OMPayment.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TransactionService {

    List<Transaction> listTransactions();
}
