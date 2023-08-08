package com.example.OMPayment.controller;


import com.example.OMPayment.model.*;
import com.example.OMPayment.payload.request.CashInRequest;
import com.example.OMPayment.repository.CashInRepository;
import com.example.OMPayment.repository.PaymentActorRepository;
import com.example.OMPayment.repository.TransactionRepository;
import com.example.OMPayment.service.CashInService;
import com.example.OMPayment.service.TransactionService;
import com.example.OMPayment.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("api/cashIn")
@AllArgsConstructor
@Slf4j
public class CashInController<LOGGER> {

    private final UserService userService;

    private final TransactionService transactionService;

    private final TransactionRepository transactionRepository;

    private final CashInRepository cashInRepository;

    private final PaymentActorRepository paymentActorRepository;

    private final CashInService cashInService;


    @GetMapping()
    public ResponseEntity<?> getAllCashIn() {

        return ResponseEntity.ok(cashInRepository.findAllByOrderByDateDesc());
    }

    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<?> getCashInByPNR(@PathVariable("pnr") String pnr) {

        return ResponseEntity.ok(cashInRepository.findByReference(pnr));
    }

    @GetMapping("/methode/{methode}")
    public ResponseEntity<?> getCashInByMethod(@PathVariable("methode") String methode) {

        return ResponseEntity.ok(cashInService.listCashInParMethode(methode));
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

        for(int i = 0; i<result.length; i++) {
            System.out.println(result[i]);

            PaymentActor paymentActor = result[i];
            paymentActorRepository.save(paymentActor);
        }


        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/initCashIn/{id}")
    public ResponseEntity<?> cashIn(@PathVariable("id") Long id, @RequestBody CashInRequest cashInRequest) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        log.debug("PUBLICKEY------------------------" + publickKey);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/cashins";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        String pinCode = cashInRequest.getPartner().getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        log.debug("EcodedBytes--------------" + encodedString);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        cashInRequest.getPartner().setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newTrans = objectMapper.writeValueAsString(cashInRequest);
        log.debug("TRANSACTIONREQUEST-----------------" + newTrans);


        if(transactionRepository.existsByReference(cashInRequest.getReference())) {
            try {


                HttpEntity<CashInRequest> entity1 = new HttpEntity<CashInRequest>(cashInRequest, headers1);
                //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
                log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
                log.debug("RESULT2------------------------------------" + result);
                JsonNode node = mapper.readTree(result);
                String description = node.path("description").asText();
                String transactionId1 = node.path("transactionId").asText();
                String requestId = node.path("requestId").asText();
                String status = node.path("status").asText();
                CashIn newTransaction = new CashIn();
                newTransaction.setCashInId(transactionId1);

                newTransaction.setMethode("REMBOURSEMENT");
                newTransaction.setRequestId(requestId);
                newTransaction.setDescription(description);
                newTransaction.setStatus(status);
                newTransaction.setCustomerId(cashInRequest.getCustomer().getId());
                newTransaction.setPartnerId(cashInRequest.getPartner().getId());
                newTransaction.setAgent(user.getFirstName());
                newTransaction.setValue(cashInRequest.getAmount().getValue());
                newTransaction.setReference(cashInRequest.getReference());
                newTransaction.setEntite(userService.getUserByEmailAndMsisdn(user.getEmail(), cashInRequest.getPartner().getId()).getEntite());
                newTransaction.setDate(new Date());
                newTransaction.setDay(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(newTransaction.getDate())).toString());
                log.debug("Date" + newTransaction.getDate());
                cashInRepository.save(newTransaction);
                return ResponseEntity.ok(result);


            }

            catch (HttpClientErrorException e) {

                String message = e.getMessage();
                log.debug("messageee---------------" + message);

                int startIndex = message.indexOf("{");
                int endIndex = message.lastIndexOf("}") + 1;
                String jsonString = message.substring(startIndex, endIndex);

                log.debug("jsonstttrrrsr---------------" + jsonString);
                String js = jsonString.replaceAll("<EOL>", "");
                log.debug("jsfddddddddddddddddddddr---------------" + js);
                JSONObject json = new JSONObject(js);
                System.out.println("Type: " + json.getString("type"));
                System.out.println("Title: " + json.getString("title"));


                return ResponseEntity.ok(json.getString("detail"));

            } catch (ResourceAccessException e) {

                return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");
            } catch (NullPointerException e) {
                return ResponseEntity.ok(e.getMessage());
            }
            catch(HttpServerErrorException e) {
                String message = e.getMessage();
                log.debug("messageee---------------"+message);

                int startIndex = message.indexOf("{");
                int endIndex = message.lastIndexOf("}") + 1;
                String jsonString = message.substring(startIndex, endIndex);

                log.debug("jsonstttrrrsr---------------"+jsonString);
                String js = jsonString.replaceAll("<EOL>", "");
                log.debug("jsfddddddddddddddddddddr---------------"+js);
                JSONObject json = new JSONObject(js);
                System.out.println("Type: " + json.getString("type"));
                System.out.println("Title: " + json.getString("title"));


                return ResponseEntity.ok(json.getString("detail")); }
        } else {
            try {


                HttpEntity<CashInRequest> entity1 = new HttpEntity<CashInRequest>(cashInRequest, headers1);
                //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
                log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
                log.debug("RESULT2------------------------------------" + result);
                JsonNode node = mapper.readTree(result);
                String description = node.path("description").asText();
                String transactionId1 = node.path("transactionId").asText();
                String requestId = node.path("requestId").asText();
                String status = node.path("status").asText();
                CashIn newTransaction = new CashIn();
                newTransaction.setCashInId(transactionId1);

                newTransaction.setMethode("DEPOT");
                newTransaction.setRequestId(requestId);
                newTransaction.setDescription(description);
                newTransaction.setStatus(status);
                newTransaction.setCustomerId(cashInRequest.getCustomer().getId());
                newTransaction.setPartnerId(cashInRequest.getPartner().getId());
                newTransaction.setAgent(user.getFirstName());
                newTransaction.setValue(cashInRequest.getAmount().getValue());
                newTransaction.setReference(cashInRequest.getReference());
                newTransaction.setEntite(userService.getUserByEmailAndMsisdn(user.getEmail(), cashInRequest.getPartner().getId()).getEntite());
                newTransaction.setDate(new Date());
                newTransaction.setDay(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(newTransaction.getDate())).toString());
                log.debug("Date" + newTransaction.getDate());
                cashInRepository.save(newTransaction);
                return ResponseEntity.ok(result);


            }

            catch (HttpClientErrorException e) {

                String message = e.getMessage();
                log.debug("messageee---------------" + message);

                int startIndex = message.indexOf("{");
                int endIndex = message.lastIndexOf("}") + 1;
                String jsonString = message.substring(startIndex, endIndex);

                log.debug("jsonstttrrrsr---------------" + jsonString);
                String js = jsonString.replaceAll("<EOL>", "");
                log.debug("jsfddddddddddddddddddddr---------------" + js);
                JSONObject json = new JSONObject(js);
                System.out.println("Type: " + json.getString("type"));
                System.out.println("Title: " + json.getString("title"));


                return ResponseEntity.ok(json.getString("detail"));

            } catch (ResourceAccessException e) {

                return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");
            } catch (NullPointerException e) {
                return ResponseEntity.ok(e.getMessage());
            }
            catch(HttpServerErrorException e) {
                String message = e.getMessage();
                log.debug("messageee---------------"+message);

                int startIndex = message.indexOf("{");
                int endIndex = message.lastIndexOf("}") + 1;
                String jsonString = message.substring(startIndex, endIndex);

                log.debug("jsonstttrrrsr---------------"+jsonString);
                String js = jsonString.replaceAll("<EOL>", "");
                log.debug("jsfddddddddddddddddddddr---------------"+js);
                JSONObject json = new JSONObject(js);
                System.out.println("Type: " + json.getString("type"));
                System.out.println("Title: " + json.getString("title"));


                return ResponseEntity.ok(json.getString("detail")); }
        }



    }

    @PostMapping(value = "/initBulkCashIn/{id}")
    public ResponseEntity<?> bulkCashIn(@PathVariable("id") Long id, @RequestBody List<CashInRequest> cashInRequest) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();
        String result;

        String publickKey = transactionService.getKey(id);
        log.debug("PUBLICKEY------------------------" + publickKey);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/bulkcashins";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("X-Callback-Url", "");
        headers1.add("Authorization", "Bearer " + token);
        System.out.println("SIZE-------" + cashInRequest.size());
        List<CashInRequest> newListCashIn = new ArrayList<>();
        for (int i = 0; i < cashInRequest.size(); i++) {
            String pinCode = cashInRequest.get(i).getPartner().getEncryptedPinCode();
            byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
            log.debug("EcodedBytes--------------" + encodedString);
            String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

            cashInRequest.get(i).getPartner().setEncryptedPinCode(encodedMessage);
            newListCashIn.add(cashInRequest.get(i));
            ObjectMapper objectMapper = new ObjectMapper();
            String newTrans = objectMapper.writeValueAsString(cashInRequest.get(i));
            log.debug("TRANSACTIONREQUEST-----------------" + newTrans);
        }
        System.out.println("NewList" + newListCashIn);

        try {
            HttpEntity<List<CashInRequest>> entity1 = new HttpEntity<>(newListCashIn, headers1);
            //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
            log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
            log.debug("RESULT2------------------------------------" + result);
            JsonNode node = mapper.readTree(result);
            String description = node.path("description").asText();
            String transactionId1 = node.path("bulkId").asText();
            String status = node.path("status").asText();
            for (int i = 0; i < newListCashIn.size(); i++) {
                log.debug("SIZEEEE--" + newListCashIn.size());
                log.debug("SIZEEEEEE--" + newListCashIn.get(i));


                CashIn newTransaction = new CashIn();
                newTransaction.setCashInId(transactionId1);

                newTransaction.setMethode("BULK CASHIN");
                newTransaction.setDescription(description);
                newTransaction.setStatus(status);
                newTransaction.setCustomerId(newListCashIn.get(i).getCustomer().getId());
                newTransaction.setPartnerId(newListCashIn.get(i).getPartner().getId());
                newTransaction.setAgent(user.getEmail());
                newTransaction.setValue(newListCashIn.get(i).getAmount().getValue());
                newTransaction.setReference(newListCashIn.get(i).getReference());
                newTransaction.setEntite(userService.getUserByEmailAndMsisdn(user.getEmail(), newListCashIn.get(i).getPartner().getId()).getEntite());
                newTransaction.setDate(new Date());
                newTransaction.setDay(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(newTransaction.getDate())).toString());
                log.debug("transaction"+(i) + newTransaction);
                log.debug("Date" + newTransaction.getDate());
                cashInRepository.save(newTransaction);


            }
            return ResponseEntity.ok(result);

        } catch (HttpClientErrorException e) {

            String message = e.getMessage();
            log.debug("messageee---------------" + message);

            int startIndex = message.indexOf("{");
            int endIndex = message.lastIndexOf("}") + 1;
            String jsonString = message.substring(startIndex, endIndex);

            log.debug("jsonstttrrrsr---------------" + jsonString);
            String js = jsonString.replaceAll("<EOL>", "");
            log.debug("jsfddddddddddddddddddddr---------------" + js);
            JSONObject json = new JSONObject(js);
            System.out.println("Type: " + json.getString("type"));
            System.out.println("Title: " + json.getString("title"));


            return ResponseEntity.ok(json.getString("detail"));

        } catch (ResourceAccessException e) {

            return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");
        } catch (NullPointerException e) {
            return ResponseEntity.ok(e.getMessage());
        }


    }





}
