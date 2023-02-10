package com.example.OMPayment.controller;


import com.example.OMPayment.model.CashInActor;
import com.example.OMPayment.model.PaymentActor;
import com.example.OMPayment.model.Transaction;
import com.example.OMPayment.model.User;
import com.example.OMPayment.payload.request.CashInRequest;
import com.example.OMPayment.repository.CashInActorRepository;
import com.example.OMPayment.repository.PaymentActorRepository;
import com.example.OMPayment.repository.TransactionRepository;
import com.example.OMPayment.service.TransactionService;
import com.example.OMPayment.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/cashIn")
@AllArgsConstructor
@Slf4j
public class CashInController<LOGGER> {

    private final UserService userService;

    private final TransactionService transactionService;

    private final TransactionRepository transactionRepository;

    private final CashInActorRepository cashInActorRepository;

    private final PaymentActorRepository paymentActorRepository;


    @GetMapping()
    public ResponseEntity<?> getAllTransactionCashIn() {

        return ResponseEntity.ok(transactionService.listTransactionsCashIn("CASHIN"));
    }

    @GetMapping("/testNumbers/{id}")
    public ResponseEntity<?> getTestNumbers(@PathVariable("id") Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        System.out.println("tokennnnn"+token);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/assignments/v1/partner/sim-cards?nbRetailers=1&nbCustomers=1";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity1 = new HttpEntity<>(headers1);


        PaymentActor[] result = restTemplate.exchange(url, HttpMethod.GET, entity1, PaymentActor[].class).getBody();

        System.out.println(result[0]);

        PaymentActor cashInActor1 = result[0];
        PaymentActor cashInActor2 = result[1];

        System.out.println("PAYMMMMMMMMMMMMMMMMM"+cashInActor1);
        System.out.println("PAYMMMMMMMMMMMMMMMMM"+cashInActor2);

        paymentActorRepository.save(cashInActor1);
        paymentActorRepository.save(cashInActor2);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/initCashIn/{id}")
    public ResponseEntity<?> cashIn(@PathVariable("id") Long id, @RequestBody CashInRequest cashInRequest) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        log.debug("PUBLICKEY------------------------"+publickKey);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/cashins";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        String pinCode = cashInRequest.getPartner().getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        log.debug("EcodedBytes--------------"+encodedString);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        cashInRequest.getPartner().setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newTrans = objectMapper.writeValueAsString(cashInRequest);
        log.debug("TRANSACTIONREQUEST-----------------"+newTrans);

        HttpEntity<CashInRequest> entity1 = new HttpEntity<CashInRequest>(cashInRequest, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);
        JsonNode node = mapper.readTree(result);
        String description = node.path("description").asText();
        String transactionId = node.path("transactionId").asText();
        String requestId = node.path("requestId").asText();
        String status = node.path("status").asText();
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionId(transactionId);
        newTransaction.setMethode("CASHIN");
        newTransaction.setRequestId(requestId);
        newTransaction.setDescription(description);
        newTransaction.setStatus(status);
        newTransaction.setCustomerId(cashInRequest.getCustomer().getId());
        newTransaction.setPartnerId(cashInRequest.getPartner().getId());
        newTransaction.setAgent(user.getEmail());
        newTransaction.setValue(cashInRequest.getAmount().getValue());
        newTransaction.setReference(cashInRequest.getReference());
        newTransaction.setEntite(userService.getUserByEmailAndMsisdn(user.getEmail(), cashInRequest.getPartner().getId()).getEntite());
        newTransaction.setDate(new Date());
        newTransaction.setDay(LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(newTransaction.getDate()) ).toString());
        log.debug("Date"+newTransaction.getDate());
        transactionRepository.save(newTransaction);
        return ResponseEntity.ok(result);
    }

}
