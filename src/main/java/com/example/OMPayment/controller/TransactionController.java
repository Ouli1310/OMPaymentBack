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
import com.sun.jdi.event.StepEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

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

    private final ErrorHttpRepository errorHttpRepository;


    @GetMapping
    public ResponseEntity<?> getAllTransaction() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        return ResponseEntity.ok(transactionService.listTransactions());
    }

    @GetMapping("/{status}")
    public ResponseEntity<?> getAllTransactionByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionParStatus(status));
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<?> getTransactonByPartnerId(@PathVariable("partnerId") String partnerId) {
        return ResponseEntity.ok(transactionService.listTransactionsByPartnerId(partnerId));
    }

    @GetMapping("/agent/{email}")
    public ResponseEntity<?> getTransactonByAgent(@PathVariable("email") String email) {
        return ResponseEntity.ok(transactionService.listTransactionsParAgent(email));
    }

    @GetMapping("/agent/{email}/{status}")
    public ResponseEntity<?> getTransactonByAgentAndStatus(@PathVariable("email") String email, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionParAgentEtStatus(email, status));
    }

    @GetMapping("/partner/{partnerId}/{status}")
    public ResponseEntity<?> getTransactonByPartnerIdAndStatus(@PathVariable("partnerId") String partnerId, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.lstTransactionByPartnerIdAndStatus(partnerId, status));
    }

    @GetMapping("/entite/{entite}")
    public ResponseEntity<?> getAllTransactionByEntite(@PathVariable("entite") Long entite) {
        return ResponseEntity.ok(transactionService.listTransactionsParAgence(entite));
    }

    @GetMapping("/entite/{entite}/{status}")
    public ResponseEntity<?> getAllTransactionByEntiteAndStatus(@PathVariable("entite") Long entite, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionParAgenceEtStatus(entite, status));
    }

    @GetMapping("/method")
    public ResponseEntity<?> getAllMethod() {

        return ResponseEntity.ok(methodService.getAllMethod());
    }

    @GetMapping("/idType")
    public ResponseEntity<?> getAllIdTypes() {

        return ResponseEntity.ok(idTypeService.getListIdType());
    }

    @GetMapping("/method/{methode}")
    public ResponseEntity<?> getTransactionByMethod(@PathVariable("methode") String methode) {

        return ResponseEntity.ok(transactionService.listTransactionsParMethode(methode));
    }

    @GetMapping("/day/{day}")
    public ResponseEntity<?> getTransactionDay(@PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactionByDay(day));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<?> getTransactionDate(@PathVariable("date") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date date) {
        return ResponseEntity.ok(transactionService.transactionByDate(date));
    }

   // public List<Transaction> getTransactionBetween2Dates(@PathVariable("date1") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date1, @PathVariable("date2") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate date2) {

        @GetMapping("/dates/{date1}/{date2}")
    public ResponseEntity<?> getTransactionBetween2Dates(@PathVariable("date1") String date1, @PathVariable("date2") String date2) {

        return ResponseEntity.ok(transactionService.transactionBetweenDate1AnDate2(date1, date2));
    }

    @GetMapping("entite/{entite}/dates/{date1}/{date2}")
    public ResponseEntity<?> getTransactionByEntiteAndBetween2Dates(@PathVariable("entite") Long entite, @PathVariable("date1") String date1, @PathVariable("date2") String date2) {

        return ResponseEntity.ok(transactionService.transactionsByEntiteAndBetweenDates(entite, date1, date2));
    }

    @RequestMapping(value = "/newToken/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> createToken(@PathVariable("id") Long id) throws IOException, NoSuchAlgorithmException {

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
try {
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
    return ResponseEntity.ok(token);
}catch (ResourceAccessException e) {

    return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");
}catch (HttpClientErrorException e) {

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

    ErrorHttp errorHttp = new ErrorHttp();
    errorHttp.setType(json.getString("type"));
    errorHttp.setTitle(json.getString("title"));
    errorHttp.setInstance(json.getString("instance"));
    errorHttp.setStatus(json.getString("status"));
    errorHttp.setCode(json.getString("code"));
    errorHttp.setDetail(json.getString("detail"));
    errorHttpRepository.save(errorHttp);
    return ResponseEntity.ok(errorHttp.getDetail()); }



    }

   @GetMapping("/testNumbers/{id}")
    public ResponseEntity<?> getTestNumbers(@PathVariable("id") Long id) throws JsonProcessingException {
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

        return ResponseEntity.ok(result.toString());
    }

    @GetMapping("/getPubliKey/{id}")
    public ResponseEntity<?> getPublicKey(@PathVariable("id") Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);

        String result = transactionService.getKey(id);

        log.debug("__------------------------------resultatt"+result);
        user.setPublicKey(result);

        userRepository.save(user);


        return ResponseEntity.ok(result);
    }



    @PostMapping(value = "/initTransaction/{id}")
    public ResponseEntity<?> initiateTransaction(@PathVariable("id") Long id, @RequestBody TransactionRequest transactionRequest) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
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

        try {
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
            newTransaction.setMethode(transactionRequest.getMethod());
            newTransaction.setRequestId(requestId);
            newTransaction.setDescription(description);
            newTransaction.setStatus(status);
            newTransaction.setCustomerId(transactionRequest.getCustomer().getId());
            newTransaction.setPartnerId(transactionRequest.getPartner().getId());
            newTransaction.setAgent(user.getEmail());
            newTransaction.setValue(transactionRequest.getAmount().getValue());
            newTransaction.setReference(transactionRequest.getReference());
            newTransaction.setEntite(userService.getUserByEmailAndMsisdn(user.getEmail(), transactionRequest.getPartner().getId()).getEntite());
            newTransaction.setDate(new Date());
            newTransaction.setDay(LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(newTransaction.getDate()) ).toString());
            log.debug("Date"+newTransaction.getDate());
            transactionRepository.save(newTransaction);
            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException e) {

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

            ErrorHttp errorHttp = new ErrorHttp();
            errorHttp.setType(json.getString("type"));
            errorHttp.setTitle(json.getString("title"));
            errorHttp.setInstance(json.getString("instance"));
            errorHttp.setStatus(json.getString("status"));
            errorHttp.setCode(json.getString("code"));
            errorHttp.setDetail(json.getString("detail"));
            errorHttpRepository.save(errorHttp);
            return ResponseEntity.ok(errorHttp.getDetail());

        }catch (ResourceAccessException e) {

            return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion."); }
        catch (NullPointerException e) {
            return ResponseEntity.ok(e.getMessage());
        }



    }

    @PostMapping("/confirmTransaction/{id}/{transactionId}")
    public ResponseEntity<?> confirmPayment(@PathVariable("id") Long id, @PathVariable("transactionId") String transactionId, @RequestBody Customer customer) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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
        try {
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

            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException e) {

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

            ErrorHttp errorHttp = new ErrorHttp();
            errorHttp.setType(json.getString("type"));
            errorHttp.setTitle(json.getString("title"));
            errorHttp.setInstance(json.getString("instance"));
            errorHttp.setStatus(json.getString("status"));
            errorHttp.setCode(json.getString("code"));
            errorHttp.setDetail(json.getString("detail"));
            errorHttpRepository.save(errorHttp);
            Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
            transaction.setStatus("ECHEC");
            transactionRepository.save(transaction);
            log.debug("newTrans---------------"+transaction);

            return ResponseEntity.ok(errorHttp.getDetail());
        }catch (ResourceAccessException e) {

            return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");

        }



    }

    @GetMapping("/isTransactionConfirm/{id}/{transactionId}")
    public ResponseEntity<?> isconfirmPayment(@PathVariable("id") Long id, @PathVariable("transactionId") String transactionId, @RequestBody Customer customer) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, InterruptedException {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);
        int i = 0;
        while (i< 100 ) {
int t = +i;
while (t < 100) {
    if (confirmPayment(id, transactionId, customer).hasBody()) {
        log.debug("hhhhhhhhhhhhhhhh" + confirmPayment(id, transactionId, customer).hasBody());
        return ResponseEntity.ok("yes");
    } else {
        log.debug("hhhhhhhhhhhhhhhh" + confirmPayment(id, transactionId, customer).hasBody());
        return ResponseEntity.ok("NON");
    }
}

        }
        return ResponseEntity.ok("FALSE");
    }

        @PostMapping(value = "/generateOTP/{id}")
    public ResponseEntity<?> initiateTransaction(@PathVariable("id") Long id, @RequestBody Customer customer) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
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
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/oneStepPayment/{id}")
    public ResponseEntity<?> oneStepPayment(@PathVariable("id") Long id, @RequestBody OneStepRequest oneStepRequest) throws JsonProcessingException {
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
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getBalance/{id}")
    public ResponseEntity<?> getBalance(@PathVariable("id") Long id, @RequestBody Partner partner) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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

      return ResponseEntity.ok(result);
    }

    @GetMapping("/getProfile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id, @Param("msisdn") String msisdn, @Param("type") String type) throws JsonProcessingException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
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
        return ResponseEntity.ok(result);
    }










}
