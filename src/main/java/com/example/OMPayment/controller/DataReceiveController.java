package com.example.OMPayment.controller;

import com.example.OMPayment.model.Transaction;
import com.example.OMPayment.payload.request.TransactionRequest;
import com.example.OMPayment.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dataReceive")
@AllArgsConstructor
@Slf4j
public class DataReceiveController {

    private final TransactionRepository transactionRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping()
    public ResponseEntity<String> receiveData(@RequestBody TransactionRequest data) {
        // Handle the received data here
        System.out.println("Received data: " + data);
        // You can process the data and send a response if required.
        Transaction transaction = new Transaction();
        // transaction.setTransactionId(data.getReference());
        transaction.setCustomerId(data.getCustomer().getId());
        transaction.setPartnerId(data.getPartner().getId());
        transaction.setReference(data.getReference());
        transaction.setValue(data.getAmount().getValue());
        transactionRepository.save(transaction);
        messagingTemplate.convertAndSend("/topic/notifications", "Data saved");
        return ResponseEntity.ok("Data received successfully!");
    }
}
