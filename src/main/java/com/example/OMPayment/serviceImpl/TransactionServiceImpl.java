package com.example.OMPayment.serviceImpl;
import com.example.OMPayment.model.Transaction;
import com.example.OMPayment.model.User;
import com.example.OMPayment.repository.TransactionRepository;
import com.example.OMPayment.service.TransactionService;
import com.example.OMPayment.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.OMPayment.encryption.RSAUtils.getPublicKey;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static org.springframework.security.crypto.codec.Base64.*;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserService userService;

    @Override
    public List<Transaction> listTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> listTransactionParStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    @Override
    public List<Transaction> listTransactionParStatusAndDay(String status, String day) {
        return transactionRepository.findByStatusAndDay(status, day);
    }

    @Override
    public List<Transaction> listTransactionParStatusAndAgenceDay(String status, Long entire, String day) {
        return transactionRepository.findByStatusAndEntiteAndDay(status, entire, day);
    }

    @Override
    public List<Transaction> listTransactionParStatusAndAgentAndDay(String status, String email, String day) {
        return transactionRepository.findByStatusAndAgentAndDay(status, email, day);
    }

    @Override
    public List<Transaction> transactionsByStatusAndBetweenDates(String status, String date1, String date2) {
        return transactionRepository.findByStatusAndDayGreaterThanEqualAndDayLessThanEqual(status, date1, date2);
    }

    @Override
    public List<Transaction> transactionsByStatusAndEntiteAndBetweenDates(String status, Long entite, String date1, String date2) {
        return transactionRepository.findByStatusAndEntiteAndDayGreaterThanEqualAndDayLessThanEqual(status, entite, date1, date2);
    }

    @Override
    public List<Transaction> transactionsByStatusAndAgentAndBetweenDates(String status, String email, String date1, String date2) {
        return transactionRepository.findByStatusAndAgentAndDayGreaterThanEqualAndDayLessThanEqual(status, email, date1, date2);
    }


    @Override
    public String encryptedCode(Long id, String pinCode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JsonProcessingException {
        String key = getKey(id);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(key);
        String keyId = node.path("keyId").asText();
        String keyType = node.path("keyType").asText();
        int keySize = node.path("keySize").asInt();
        byte[] skey = node.path("key").asText().getBytes(StandardCharsets.UTF_8);
        Key pupkey = new SecretKeySpec(skey,0,keySize, "RSA");
       /* byte[] byteKey = getDecoder().decode(key);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        Key pubKey = kf.generatePublic(X509publicKey);*/

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, pupkey);
        byte[] secretMessageBytes = pinCode.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        String encodedMessage = getEncoder().encodeToString(encryptedMessageBytes);
        return encodedMessage;
    }


    public String getKey(Long id) throws JsonProcessingException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        System.out.println("pukey"+token);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/account/v1/publicKeys";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity1 = new HttpEntity<>(headers1);


        String result = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class).getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(result);
        String key = node.path("key").asText();
        return key;
    }

    public byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    @Override
    public Transaction getTransactionByTransactionId(String id) {
        return transactionRepository.findByTransactionId(id);
    }

    @Override
    public Transaction getTransactionByMethodeAndTransactionId(String methode, String id) {
        return transactionRepository.findByMethodeAndTransactionId(methode, id);
    }

    @Override
    public List<Transaction> listTransactionsByPartnerId(String partnerId) {
        return transactionRepository.findByPartnerId(partnerId);
    }

    @Override
    public List<Transaction> lstTransactionByPartnerIdAndStatus(String partnerId, String status) {
        return transactionRepository.findByPartnerIdAndStatus(partnerId, status);
    }

    @Override
    public List<Transaction> listTransactionsParAgence(Long entite) {
        return transactionRepository.findByEntiteOrderByDateDesc(entite);
    }

    @Override
    public List<Transaction> listTransactionsParMethode(String methode) {
        return transactionRepository.findByMethode(methode);
    }

    @Override
    public List<Transaction> listTransactionByMethodeAndStatus(String methode, String status) {
        return transactionRepository.findByMethodeAndStatus(methode, status);
    }

    @Override
    public List<Transaction> listTransactionsCashIn(String methode) {
        return transactionRepository.findByMethode(methode);
    }

    @Override
    public List<Transaction> listTransactionsParAgent(String emailAgent) {

        return transactionRepository.findByAgentOrderByDateDesc(emailAgent);
    }

    @Override
    public List<Transaction> listTransactionParPNR(String pnr) {
        return transactionRepository.findByReference(pnr);
    }

    @Override
    public List<Transaction> listTransactionParPNRAndStatus(String pnr, String status) {
        return transactionRepository.findByReferenceAndStatus(pnr, status);
    }

    @Override
    public List<Transaction> listTransactionParMethodeAndPNRAndStatus(String methode, String pnr, String status) {
        return transactionRepository.findByMethodeAndReferenceAndStatus(methode, pnr, status);
    }

    @Override
    public List<Transaction> listTransactionParAgenceEtStatus(Long entite, String status) {
        return transactionRepository.findByEntiteAndStatus(entite, status);
    }

    @Override
    public List<Transaction> listTransactionParAgentEtStatus(String email, String status) {

        return transactionRepository.findByAgentAndStatus(email, status);
    }

    @Override
    public Transaction transactionByDate(Date date) {
        return transactionRepository.findByDate(date);
    }

    @Override
    public List<Transaction> transactionByDay(String day) {
        return transactionRepository.findByDay(day);
    }

    @Override
    public List<Transaction> transactionByMethodeAndDay(String methode, String day) {
        return transactionRepository.findByMethodeAndDay(methode, day);
    }

    @Override
    public List<Transaction> transactinByEntiteAndDay(Long entite, String day) {
        return transactionRepository.findByEntiteAndDay(entite, day);
    }

    @Override
    public List<Transaction> transactinByMethodeAndEntiteAndDay(String methode, Long entite, String day) {
        return transactionRepository.findByMethodeAndEntiteAndDay(methode, entite,day);
    }

    @Override
    public List<Transaction> transactionBetweenDate1AnDate2(String date1, String date2) {
        return transactionRepository.findByDayGreaterThanEqualAndDayLessThanEqual(date1, date2);
    }

    @Override
    public List<Transaction> transactionByMethodeAndBetweenDate1AnDate2(String methode, String date1, String date2) {
        return transactionRepository.findByMethodeAndDayGreaterThanEqualAndDayLessThanEqual(methode, date1, date2);
    }

    @Override
    public List<Transaction> transactionsByEntiteAndBetweenDates(Long entite, String date1, String date2) {
        return transactionRepository.findByEntiteAndDayGreaterThanEqualAndDayLessThanEqual(entite, date1, date2);
    }

    @Override
    public List<Transaction> transactionsByMethodeAndEntiteAndBetweenDates(String methode, Long entite, String date1, String date2) {
        return transactionRepository.findByMethodeAndEntiteAndDayGreaterThanEqualAndDayLessThanEqual(methode, entite, date1, date2);
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).get();
    }

    @Override
    public Transaction changeStatus(Long id) {
        Transaction transaction = getTransactionById(id);

        String dateTimeStr = transaction.getDate().toString();
        System.out.println(dateTimeStr);
        /**  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
         Date dat = format.parse(transaction.getDate().toString());
         System.out.println("datttttt"+dat);
         System.out.println(dat.getSeconds());
         Date currentTime = new Date();
         System.out.println("currentTime"+currentTime);*/

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        System.out.println(formatter);
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
        System.out.println("datetime"+dateTime);
        LocalDateTime newDateTime = dateTime.plusSeconds(15);
        System.out.println(newDateTime);

        //On instancie la date présente, puis on la formatte

        Date currentTime = new Date();
        System.out.println("currentTime"+currentTime);
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

        LocalDateTime date = LocalDateTime.parse(currentTime.toString(), formatter1);
        System.out.println("dateeeee"+date.toString());

        /** DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         LocalDateTime formattedCurrentTime = LocalDateTime.parse(date.toString(), outputFormatter);
         System.out.println(formattedCurrentTime);*/


        if((newDateTime.plusSeconds(15).compareTo(date)<0) && transaction.getStatus().equalsIgnoreCase("INITIATED")) {
            transaction.setDescription("Cette transaction a dépassé le délai de confirmation.");
            transaction.setStatus("ECHEC");
            transactionRepository.save(transaction);
            return transaction;
        }

        return transaction;
    }

    @Override
    public List<Transaction> transactionsByAgentAndDay(String email, String day) {
        return transactionRepository.findByAgentAndDay(email, day);
    }
}
