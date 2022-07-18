package com.example.OMPayment.controller;

import com.example.OMPayment.model.*;
import com.example.OMPayment.payload.request.*;
import com.example.OMPayment.repository.*;
import com.example.OMPayment.security.services.ObjectToUrlEncodedConverter;
import com.example.OMPayment.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@RestController
@RequestMapping("api/transaction")
@AllArgsConstructor
@Slf4j
public class TransactionController<LOGGER> {

    private final TransactionService transactionService;

    private final TokenOMRepository tokenOMRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final PaymentActorRepository paymentActorRepository;

    private final TransactionRepository transactionRepository;

    private final MethodService methodService;

    private final IdTypeService idTypeService;

    private final PaymentActorService paymentActorService;

    private final BalanceRepository balanceRepository;


    @GetMapping
    public List<Transaction> getAllTransaction() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        return transactionService.listTransactions();
    }

    @GetMapping("/{status}")
    public List<Transaction> getAllTransactionByStatus(@PathVariable("status") String status) {
        return transactionService.listTransactionParStatus(status);
    }

    @GetMapping("/partner/{partnerId}")
    public List<Transaction> getTransactonByPartnerId(@PathVariable("partnerId") String partnerId) {
        return transactionService.listTransactionsByPartnerId(partnerId);
    }

    @GetMapping("/partner/{partnerId}/{status}")
    public List<Transaction> getTransactonByPartnerIdAndStatus(@PathVariable("partnerId") String partnerId, @PathVariable("status") String status) {
        return transactionService.lstTransactionByPartnerIdAndStatus(partnerId, status);
    }

    @GetMapping("/method")
    public List<Method> getAllMethod() {

        return methodService.getAllMethod();
    }

    @GetMapping("/idType")
    public List<IdType> getAllIdTypes() {

        return idTypeService.getListIdType();
    }

    @RequestMapping(value = "/newToken/{id}", method = RequestMethod.POST)
    public String createToken(@PathVariable("id") Long id) throws IOException, NoSuchAlgorithmException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //System.out.println(auth);
        AuthRequest authRequest = new AuthRequest();
        authRequest.setGrant_type("client_credentials");
        authRequest.setClient_id("8ccd9e42-e94b-4686-97c2-beae8643e4dc");
        authRequest.setClient_secret("e1e9e5e5-dbe9-4483-920c-867c152b5cf6");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<AuthRequest> entity = new HttpEntity<AuthRequest>(authRequest,headers);

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));

        String result = restTemplate.exchange(
                "https://api.sandbox.orange-sonatel.com/oauth/token", HttpMethod.POST, entity, String.class).getBody();
        TokenOM tokenOM = new TokenOM();

        JsonNode node = mapper.readTree(result);
        String token = node.path("access_token").asText();
        tokenOM.setToken(token);
        System.out.println(token);
        System.out.println(token.length());
        User user = userService.getUserById(id);
        user.setTokenOM(token);
        System.out.println(user);
        userRepository.save(user);
        tokenOMRepository.save(tokenOM);
        return token;

    }

   @GetMapping("/testNumbers/{id}")
    public String getTestNumbers(@PathVariable("id") Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        System.out.println("tokennnnn"+token);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/assignments/v1/partner/sim-cards?nbMerchants=1&nbCustomers=1";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity1 = new HttpEntity<>(headers1);


    PaymentActor[] result = restTemplate.exchange(url, HttpMethod.GET, entity1, PaymentActor[].class).getBody();

       System.out.println(result[0]);

       PaymentActor paymentActor1 = result[0];
       PaymentActor paymentActor2 = result[1];

       System.out.println("PAYMMMMMMMMMMMMMMMMM"+paymentActor1);
       System.out.println("PAYMMMMMMMMMMMMMMMMM"+paymentActor2);

       paymentActorRepository.save(paymentActor1);
       paymentActorRepository.save(paymentActor2);

        return result.toString();
    }

    @GetMapping("/getPubliKey/{id}")
    public String getPublicKey(@PathVariable("id") Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);

        String result = transactionService.getKey(id);

        log.debug("__------------------------------resultatt"+result);
        user.setPublicKey(result);

        userRepository.save(user);


        return result;
    }



    @PostMapping(value = "/initTransaction/{id}")
    public String initiateTransaction(@PathVariable("id") Long id, @RequestBody TransactionRequest transactionRequest) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        log.debug("PUBLICKEY------------------------"+publickKey);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/payments";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        String pinCode = transactionRequest.getPartner().getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        log.debug("EcodedBytes--------------"+encodedString);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        transactionRequest.getPartner().setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newTrans = objectMapper.writeValueAsString(transactionRequest);
        log.debug("TRANSACTIONREQUEST-----------------"+newTrans);

        HttpEntity<TransactionRequest> entity1 = new HttpEntity<TransactionRequest>(transactionRequest, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);
        JsonNode node = mapper.readTree(result);
        String description = node.path("description").asText();
        String transactionId = node.path("transactionId").asText();
        String requestId = node.path("requestId").asText();
        String status = node.path("status").asText();
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionId(transactionId);
        newTransaction.setRequestId(requestId);
        newTransaction.setDescription(description);
        newTransaction.setStatus(status);
        newTransaction.setCustomerId(transactionRequest.getCustomer().getId());
        newTransaction.setPartnerId(transactionRequest.getPartner().getId());
        newTransaction.setValue(transactionRequest.getAmount().getValue());
        newTransaction.setReference(transactionRequest.getReference());
        newTransaction.setDate(new Date());
        log.debug("Date"+newTransaction.getDate());
        transactionRepository.save(newTransaction);
        return result;
    }

    @PostMapping("/confirmTransaction/{id}/{transactionId}")
    public String confirmPayment(@PathVariable("id") Long id, @PathVariable("transactionId") String transactionId, @RequestBody Customer customer) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.debug("CUSTOMER---------------"+customer);
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/transactions/"+transactionId+"/confirm";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        String pinCode = customer.getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        customer.setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newCustom = objectMapper.writeValueAsString(customer);
        HttpEntity<Customer> entity1 = new HttpEntity<Customer>(customer, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        JsonNode node = mapper.readTree(result);
        String status = node.path("status").asText();
        log.debug("Status------------------"+status);
        Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
        transaction.setStatus(status);
        transactionRepository.save(transaction);
        log.debug("newTrans---------------"+transaction);
return result;
    }

    @PostMapping(value = "/generateOTP/{id}")
    public String initiateTransaction(@PathVariable("id") Long id, @RequestBody Customer customer) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();
        String publickKey = transactionService.getKey(id);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/payments/otp";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        String pinCode = customer.getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        customer.setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newCustom = objectMapper.writeValueAsString(customer);
        HttpEntity<Customer> entity1 = new HttpEntity<Customer>(customer, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        JsonNode node = mapper.readTree(result);
        String otp = node.path("otp").asText();
        PaymentActor paymentActor = paymentActorService.getByMsisdn(customer.getId());
        paymentActor.setOtp(otp);
        paymentActorRepository.save(paymentActor);
        return result;
    }

    @PostMapping(value = "/oneStepPayment/{id}")
    public String oneStepPayment(@PathVariable("id") Long id, @RequestBody OneStepRequest oneStepRequest) throws JsonProcessingException {
        log.debug("Customer----------------"+oneStepRequest.getCustomer());
        log.debug("Partner---------------"+oneStepRequest.getPartner());
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        log.debug("PUBLICKEY------------------------"+publickKey);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/payments/onestep";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);

        HttpEntity<OneStepRequest> entity1 = new HttpEntity<OneStepRequest>(oneStepRequest, headers1);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);
        JsonNode node = mapper.readTree(result);
        String description = node.path("description").asText();
        String transactionId = node.path("transactionId").asText();
        String requestId = node.path("requestId").asText();
        String status = node.path("status").asText();
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionId(transactionId);
        newTransaction.setRequestId(requestId);
        newTransaction.setDescription(description);
        newTransaction.setStatus(status);
        newTransaction.setCustomerId(oneStepRequest.getCustomer().getId());
        newTransaction.setPartnerId(oneStepRequest.getPartner().getId());
        transactionRepository.save(newTransaction);
        return result;
    }

    @PostMapping("/getBalance/{id}")
    public String getBalance(@PathVariable("id") Long id, @RequestBody Partner partner) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String publickKey = transactionService.getKey(id);
        String pinCode = partner.getEncryptedPinCode();
        byte[] encodedString = transactionService.encrypt(pinCode, publickKey);
        String encodedMessage = Base64.getEncoder().encodeToString(encodedString);

        partner.setEncryptedPinCode(encodedMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        String newPartner = objectMapper.writeValueAsString(partner);

        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/account/retailer/balance";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<Partner> entity1 = new HttpEntity<Partner>(partner, headers1);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();

      return result;
    }

    @GetMapping("/getProfile/{id}")
    public String getProfile(@PathVariable("id") Long id, @Param("msisdn") String msisdn, @Param("type") String type) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();

        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v1/account?msisdn="+msisdn+"&type="+type;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity entity1 = new HttpEntity(headers1);
        String result = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class).getBody();
        JsonNode node = mapper.readTree(result);
        String userId = node.path("userId").asText();
        String unit = node.path("balance").path("unit").asText();
        String value = node.path("balance").path("value").asText();
        log.debug("userd"+userId);
        log.debug("ud"+unit);
        log.debug("value"+value);
        Balance balance = new Balance();
        balance.setUnit(unit);
        balance.setValue(Double.valueOf(value));
        balanceRepository.save(balance);
        log.debug("balance"+balance);
        user.setBalance(balance);
        userRepository.save(user);
        log.debug("user"+user);
        return result;
    }









}
