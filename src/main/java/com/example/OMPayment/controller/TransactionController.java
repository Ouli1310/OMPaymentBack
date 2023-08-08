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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;

import static java.lang.Long.sum;
import static java.util.Collections.list;

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

    private final EntiteService entiteService;

    private final ProfilService profilService;

    private final CashInService cashInService;


    @GetMapping
    public ResponseEntity<?> getAllTransaction() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        List<Transaction> transactions = transactionRepository.findAllByOrderByDateDesc();
        transactions.forEach(transaction -> {

                changeStatus(transaction.getId());

            System.out.println(transaction);
        });
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/compare")
    public ResponseEntity<?> getTransactionRembouse() {

        List<Transaction> transactions = transactionService.listTransactions();
        List<CashIn> cashIns = cashInService.listCashIns();
        List<Map<String, Object>> resultList = new ArrayList<>();



        for (Transaction transaction : transactions) {
            Map<String, Object> resultMap = new HashMap<>();
            Long sommeTotale = 0L;
            Long cashInInitial = 0L;

            for (CashIn cashIn : cashIns) {
                if (transaction.getReference().equalsIgnoreCase(cashIn.getReference())) {

                    cashInInitial = cashInService.listCashInParPNR(cashIn.getReference()).stream()
                            .mapToLong(CashIn::getValue)
                            .sum();
                    sommeTotale = transactionService.listTransactionParPNR(transaction.getReference()).stream()
                            .mapToLong(transaction::getValue)
                            .sum();

                    resultMap.put("reference", transaction.getReference());
                    resultMap.put("sommeTotale", sommeTotale);
                    resultMap.put("cashInInitial", cashInInitial);

                }
            }



            resultList.add(resultMap);



        }


        return ResponseEntity.ok(resultList.stream()
                .distinct()
                .collect(Collectors.toList()));
    }


    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getAllTransactionByTransactionId(@PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionByTransactionId(transactionId));
    }

    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.changeStatus(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAllTransactionByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionParStatus(status));
    }



    @GetMapping("/methode/{methode}/status/{status}")
    public ResponseEntity<?> getAllTransactionByMethodeAndStatus(@PathVariable("methode") String methode, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionByMethodeAndStatus(methode, status));
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<?> getTransactonByPartnerId(@PathVariable("partnerId") String partnerId) {
        return ResponseEntity.ok(transactionService.listTransactionsByPartnerId(partnerId));
    }

    @GetMapping("sommeTotale/{id}")
    public ResponseEntity<?> getSommeTotale(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        Profil profil = profilService.getProfilById(user.getProfil());
        int sommeTotale = 0;
        if(profil.getCode().equalsIgnoreCase("AD")) {
            List<Transaction> transactions = transactionService.listTransactionParStatus("SUCCESS");
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("CA")) {
            List<Transaction> transactions = transactionService.listTransactionParAgenceEtStatus(user.getEntite(), "SUCCESS");
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("A")) {
            List<Transaction> transactions = transactionService.listTransactionParAgentEtStatus(user.getEmail(), "SUCCESS");
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        }
        return ResponseEntity.ok(sommeTotale);

    }

    @GetMapping("sommeTotale/{id}/{day}")
    public ResponseEntity<?> getSommeTotaleByDay(@PathVariable("id") Long id, @PathVariable("day") String day) {
        User user = userService.getUserById(id);
        Profil profil = profilService.getProfilById(user.getProfil());
        int sommeTotale = 0;
        if(profil.getCode().equalsIgnoreCase("AD")) {
            List<Transaction> transactions = transactionService.listTransactionParStatusAndDay("SUCCESS", day);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("CA")) {
            List<Transaction> transactions = transactionService.listTransactionParStatusAndAgenceDay("SUCCESS", user.getEntite(), day);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("A")) {
            List<Transaction> transactions = transactionService.listTransactionParStatusAndAgentAndDay("SUCCESS", user.getEmail(), day);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        }
        return ResponseEntity.ok(sommeTotale);

    }

    @GetMapping("sommeTotale/{id}/{date1}/{date2}")
    public ResponseEntity<?> getSommeTotaleBetweenDays(@PathVariable("id") Long id, @PathVariable("date1") String date1, @PathVariable("date2") String date2) {
        User user = userService.getUserById(id);
        Profil profil = profilService.getProfilById(user.getProfil());
        int sommeTotale = 0;
        if(profil.getCode().equalsIgnoreCase("AD")) {
            List<Transaction> transactions = transactionService.transactionsByStatusAndBetweenDates("SUCCESS", date1, date2);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("CA")) {
            List<Transaction> transactions = transactionService.transactionsByStatusAndEntiteAndBetweenDates("SUCCESS", user.getEntite(), date1, date2);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        } else if(profil.getCode().equalsIgnoreCase("A")) {
            List<Transaction> transactions = transactionService.transactionsByStatusAndAgentAndBetweenDates("SUCCESS", user.getEmail(), date1, date2);
            for(int i = 0; i<transactions.size(); i++) {
                sommeTotale += transactions.get(i).getValue();
                log.debug("sommeeeee"+sommeTotale);
            }
        }
        return ResponseEntity.ok(sommeTotale);

    }

    @GetMapping("sommeTotale/agence/{id}")
    public ResponseEntity<?> getSommeTotaleParAgence(@PathVariable("id") Long id) {
        int sommeTotale = 0;


        List<Transaction> transactions = transactionService.listTransactionParAgenceEtStatus(id, "SUCCESS");
        for(int i = 0; i<transactions.size(); i++) {
            sommeTotale += transactions.get(i).getValue();
            log.debug("sommeeeee" + sommeTotale);
        }
        return ResponseEntity.ok(sommeTotale);

    }

    @GetMapping("sommeTotale/agence/{id}/day/{day}")
    public ResponseEntity<?> getSommeTotaleParAgenceAndDay(@PathVariable("id") Long id, @PathVariable("day") String day) {
        int sommeTotale = 0;


        List<Transaction> transactions = transactionService.listTransactionParStatusAndAgenceDay("SUCCESS", id, day);
        for(int i = 0; i<transactions.size(); i++) {
            sommeTotale += transactions.get(i).getValue();
            log.debug("sommeeeee" + sommeTotale);
        }
        return ResponseEntity.ok(sommeTotale);

    }


    @GetMapping("/listeTransactionParAgence")
    public ResponseEntity<?> listofList() {
        List<List<Map<String, Object>>> listOfList = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.listTransactionsParAgence(entite.getId());
            List<Map<String, Object>> transformedList = list.stream().map(transaction -> {
                Map<String, Object> map = new HashMap<>();
                map.put("label", transaction.getDay());
                map.put("y", transaction.getValue());
                return map;
            }).collect(Collectors.toList());
            listOfList.add(transformedList);
        }
        return ResponseEntity.ok(listOfList);
    }

    @GetMapping("/listeTransactionParAgenceParDay/{day}")
    public ResponseEntity<?> listofListParDay(@PathVariable("day") String day) {
        List<List<Map<String, Object>>> listOfList = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.transactinByEntiteAndDay(entite.getId(), day);
            List<Map<String, Object>> transformedList = list.stream().map(transaction -> {
                Map<String, Object> map = new HashMap<>();
                map.put("label", transaction.getDay());
                map.put("y", transaction.getValue());
                return map;
            }).collect(Collectors.toList());
            listOfList.add(transformedList);
        }
        return ResponseEntity.ok(listOfList);
    }

    @GetMapping("/listeTransactionParAgenceBetweenDates/{date1}/{date2}")
    public ResponseEntity<?> listofListBetweenDays(@PathVariable("date1") String date1, @PathVariable("date2") String date2) {
        List<List<Map<String, Object>>> listOfList = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.transactionsByEntiteAndBetweenDates(entite.getId(), date1, date2);
            List<Map<String, Object>> transformedList = list.stream().map(transaction -> {
                Map<String, Object> map = new HashMap<>();
                map.put("label", transaction.getDay());
                map.put("y", transaction.getValue());
                return map;
            }).collect(Collectors.toList());
            listOfList.add(transformedList);
        }
        return ResponseEntity.ok(listOfList);
    }

    @GetMapping("/listeSommeParAgence")
    public ResponseEntity<?> listofSomme() {
        List<Long> listOfSomme = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.listTransactionParAgenceEtStatus(entite.getId(), "SUCCESS");
            Long sum = 0L; // Initialize sum to 0
            for (Transaction transaction : list) {
                sum += transaction.getValue(); // Add the value of each transaction to the sum
            }
            System.out.println("Somme: " + sum);
            listOfSomme.add(sum);
        }
        return ResponseEntity.ok(listOfSomme);
    }

    @GetMapping("/listeSommeParAgenceAndDay/{day}")
    public ResponseEntity<?> listofSommeParDay(@PathVariable("day") String day) {
        List<Long> listOfSomme = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.listTransactionParStatusAndAgenceDay("SUCCESS", entite.getId(), day);
            Long sum = 0L; // Initialize sum to 0
            for (Transaction transaction : list) {
                sum += transaction.getValue(); // Add the value of each transaction to the sum
            }
            System.out.println("Somme: " + sum);
            listOfSomme.add(sum);
        }
        return ResponseEntity.ok(listOfSomme);
    }

    @GetMapping("/listeSommeParAgenceAndBetweenDates/{date1}/{date2}")
    public ResponseEntity<?> listofSommeBetweenDates(@PathVariable("date1") String date1, @PathVariable("date2") String date2) {
        List<Long> listOfSomme = new ArrayList<>();
        List<Entite> entites = entiteService.getAllEntite();
        for (Entite entite : entites) {
            List<Transaction> list = transactionService.transactionsByStatusAndEntiteAndBetweenDates("SUCCESS", entite.getId(), date1, date2);
            Long sum = 0L; // Initialize sum to 0
            for (Transaction transaction : list) {
                sum += transaction.getValue(); // Add the value of each transaction to the sum
            }
            System.out.println("Somme: " + sum);
            listOfSomme.add(sum);
        }
        return ResponseEntity.ok(listOfSomme);
    }

    @GetMapping("/listeTransactionParStatus")
    public ResponseEntity<Map<String, Integer>> listofStatus() {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParStatus("SUCCESS").size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParStatus("INITIATED").size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParStatus("ECHEC").size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/listeTransactionParStatusAndDay/{day}")
    public ResponseEntity<Map<String, Integer>> listofStatusDay(@PathVariable("day") String day) {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParStatusAndDay("SUCCESS", day).size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParStatusAndDay("INITIATED", day).size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParStatusAndDay("ECHEC", day).size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/listeTransactionParStatusAndAgenceAndDay/{entite}/{day}")
    public ResponseEntity<Map<String, Integer>> listofStatusAndAgenceAndDay(@PathVariable("entite") Long entite, @PathVariable("day") String day) {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParStatusAndAgenceDay("SUCCESS", entite, day).size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParStatusAndAgenceDay("INITIATED", entite, day).size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParStatusAndAgenceDay("ECHEC", entite, day).size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/listeTransactionParStatusParAgence/{entite}")
    public ResponseEntity<Map<String, Integer>> listofStatusParAgence(@PathVariable("entite") Long entite) {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParAgenceEtStatus(entite, "SUCCESS").size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParAgenceEtStatus(entite, "INITIATED").size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParAgenceEtStatus(entite,"ECHEC").size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/listeTransactionParStatusParAgent/agent/{email}")
    public ResponseEntity<Map<String, Integer>> listofStatusParAgent(@PathVariable("email") String email) {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParAgentEtStatus(email, "SUCCESS").size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParAgentEtStatus(email, "INITIATED").size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParAgentEtStatus(email,"ECHEC").size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/listeTransactionParStatusParAgentAndDay/agent/{email}/day/{day}")
    public ResponseEntity<Map<String, Integer>> listofStatusParAgentAndDay(@PathVariable("email") String email, @PathVariable("day") String day) {
        Map<String, Integer> transactionCounts = new HashMap<>();
        transactionCounts.put("SUCCESS", transactionService.listTransactionParStatusAndAgentAndDay("SUCCESS", email, day).size());
        transactionCounts.put("INITIATED", transactionService.listTransactionParStatusAndAgentAndDay("INITIATED", email, day).size());
        transactionCounts.put("ECHEC", transactionService.listTransactionParStatusAndAgentAndDay("ECHEC", email, day).size());

        return ResponseEntity.ok(transactionCounts);
    }

    @GetMapping("/agent/{email}")
    public ResponseEntity<?> getTransactonByAgent(@PathVariable("email") String email) {
        List<Transaction> transactions = transactionService.listTransactionsParAgent(email);
        transactions.forEach(transaction -> {

            changeStatus(transaction.getId());

            System.out.println(transaction);
        });
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/agent/{email}/{status}")
    public ResponseEntity<?> getTransactonByAgentAndStatus(@PathVariable("email") String email, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.listTransactionParAgentEtStatus(email, status));
    }

    @GetMapping("/agent/{email}/day/{day}")
    public ResponseEntity<?> getTransactionByAgentAndDay(@PathVariable("email") String email, @PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactionsByAgentAndDay(email, day));
    }

    @GetMapping("/partner/{partnerId}/{status}")
    public ResponseEntity<?> getTransactonByPartnerIdAndStatus(@PathVariable("partnerId") String partnerId, @PathVariable("status") String status) {
        return ResponseEntity.ok(transactionService.lstTransactionByPartnerIdAndStatus(partnerId, status));
    }

    @GetMapping("/entite/{entite}")
    public ResponseEntity<?> getAllTransactionByEntite(@PathVariable("entite") Long entite) {
        List<Transaction> transactions = transactionService.listTransactionsParAgence(entite);
        transactions.forEach(transaction -> {

            changeStatus(transaction.getId());

            System.out.println(transaction);
        });
        return ResponseEntity.ok(transactions);
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

    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<?> getTransactionByPNR(@PathVariable("pnr") String pnr) {

        return ResponseEntity.ok(transactionService.listTransactionParPNR(pnr));
    }

    @GetMapping("/pnr/{pnr}/status/{status}")
    public ResponseEntity<?> getTransactionByPNRAndStatus(@PathVariable("pnr") String pnr, @PathVariable("status") String status) {

        return ResponseEntity.ok(transactionService.listTransactionParPNRAndStatus(pnr, status));
    }

    @GetMapping("/methode/{methode}/pnr/{pnr}/status/{status}")
    public ResponseEntity<?> getTransactionByMethodeAndPNRAndStatus(@PathVariable("methode") String methode, @PathVariable("pnr") String pnr, @PathVariable("status") String status) {

        return ResponseEntity.ok(transactionService.listTransactionParMethodeAndPNRAndStatus(methode, pnr, status));
    }

    @GetMapping("/day/{day}")
    public ResponseEntity<?> getTransactionDay(@PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactionByDay(day));
    }

    @GetMapping("/methode/{methode}/day/{day}")
    public ResponseEntity<?> getTransactionByMethodeAndDay(@PathVariable("methode") String methode, @PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactionByMethodeAndDay(methode, day));
    }

    @GetMapping("/entite/{entite}/day/{day}")
    public ResponseEntity<?> getTransactionByEntiteAndDay(@PathVariable("entite") Long entite, @PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactinByEntiteAndDay(entite, day));
    }

    @GetMapping("/methode/{methode}/entite/{entite}/day/{day}")
    public ResponseEntity<?> getTransactionByMethodeAndEntiteAndDay(@PathVariable("methode") String methode, @PathVariable("entite") Long entite, @PathVariable("day") String day) {
        return ResponseEntity.ok(transactionService.transactinByMethodeAndEntiteAndDay(methode, entite, day));
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

    @GetMapping("/methode/{methode}/dates/{date1}/{date2}")
    public ResponseEntity<?> getTransactionByMethodeAndBetween2Dates(@PathVariable("methode") String methode, @PathVariable("date1") String date1, @PathVariable("date2") String date2) {

        return ResponseEntity.ok(transactionService.transactionByMethodeAndBetweenDate1AnDate2(methode, date1, date2));
    }

    @GetMapping("entite/{entite}/dates/{date1}/{date2}")
    public ResponseEntity<?> getTransactionByEntiteAndBetween2Dates(@PathVariable("entite") Long entite, @PathVariable("date1") String date1, @PathVariable("date2") String date2) {

        return ResponseEntity.ok(transactionService.transactionsByEntiteAndBetweenDates(entite, date1, date2));
    }

    @GetMapping("methode/{methode}/entite/{entite}/dates/{date1}/{date2}")
    public ResponseEntity<?> getTransactionByMethodeAndEntiteAndBetween2Dates(@PathVariable("methode") String methode,@PathVariable("entite") Long entite, @PathVariable("date1") String date1, @PathVariable("date2") String date2) {

        return ResponseEntity.ok(transactionService.transactionsByMethodeAndEntiteAndBetweenDates(methode, entite, date1, date2));
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


    return ResponseEntity.ok(json.getString("detail"));
}






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

    for(int i = 0; i<result.length; i++) {
        System.out.println(result[i]);

        PaymentActor paymentActor = result[i];
        paymentActorRepository.save(paymentActor);
    }




        return ResponseEntity.ok(result);
    }

    @GetMapping("/callBack/{id}")
    public ResponseEntity<?> getNotif(@PathVariable("id") Long id, @Param("code") String code, @Param("page") int page, @Param("size") int size) {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        System.out.println("tokennnnn"+token);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/notification/v1/merchantcallback";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<String> entity1 = new HttpEntity<>(headers1);

        String result = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class).getBody();




        return ResponseEntity.ok(result);
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

            return ResponseEntity.ok(json.getString("detail"));

        }catch (ResourceAccessException e) {

            return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion."); }
        catch (NullPointerException e) {
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


            return ResponseEntity.ok(json.getString("detail"));
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
        Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
        if(transaction.getStatus().equalsIgnoreCase("INITIATED")) {
            try {
                HttpEntity<Customer> entity1 = new HttpEntity<Customer>(customer, headers1);
                //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
                String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
                JsonNode node = mapper.readTree(result);
                String status = node.path("status").asText();
                log.debug("Status------------------"+status);
                transaction.setStatus(status);
                transactionRepository.save(transaction);
                log.debug("newTrans---------------"+transaction);
                return ResponseEntity.ok(result);
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
                transaction.setDescription(json.getString("detail"));
                transaction.setStatus("ECHEC");
                transactionRepository.save(transaction);
                log.debug("newTrans---------------"+transaction);

                return ResponseEntity.ok(json.getString("detail"));
            }catch (ResourceAccessException e) {
                transaction.setDescription("Ressource non accessible.");
                transaction.setStatus("ECHEC");
                transactionRepository.save(transaction);
                return ResponseEntity.ok("Ressource non accessible. Vérifiez votre connexion.");

            }   catch(HttpServerErrorException e) {
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
                transaction.setDescription(json.getString("detail"));
                transaction.setStatus("ECHEC");
                transactionRepository.save(transaction);
                return ResponseEntity.ok(json.getString("detail"));
            }

        }


             else if(transaction.getStatus().equalsIgnoreCase("ECHEC")){

            transactionRepository.save(transaction);
               return  ResponseEntity.ok("Cette transaction a dépassé le délai de confirmation.");
            }

             else {
            return  ResponseEntity.ok("Cette transaction a déjà été confirmée.");
        }







    }

    @PostMapping("/initQrcode/{id}")
    public ResponseEntity<?> initiateQrcode(@PathVariable("id") Long id, @RequestBody QrcodeRequest qrcodeRequest) {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/eWallet/v4/qrcode";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<QrcodeRequest> entity1 = new HttpEntity<QrcodeRequest>(qrcodeRequest, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);

        return ResponseEntity.ok(result);

    }

    @PostMapping("/sendNotifQrcode/{id}")
    public ResponseEntity<?> sendNotif(@PathVariable("id") Long id, @RequestBody CallBackRequest callBackRequest) {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/notification/v1/merchantcallback";
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity<CallBackRequest> entity1 = new HttpEntity<CallBackRequest>(callBackRequest, headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.POST, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/getNotifQrcode/{id}")
    public ResponseEntity<?> getNotif(@PathVariable("id") Long id, @Param("code") String code) throws JsonProcessingException, ParseException {
        User user = userService.getUserById(id);
        String token = user.getTokenOM();
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.sandbox.orange-sonatel.com/api/notification/v1/merchantcallback?code="+code;
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        headers1.add("Authorization", "Bearer " + token);
        HttpEntity entity1 = new HttpEntity(headers1);
        //ResponseEntity<String> res = restTemplate.postForEntity(url, entity1, String.class);
        String result = restTemplate.exchange(url, HttpMethod.GET, entity1, String.class).getBody();
        log.debug("RESULT2------------------------------------"+result);

        JsonNode node = mapper.readTree(result);
        JsonNode result2 = node.get(0);
        log.debug("result22------------------------------------"+result2);
        String transactionId = result2.path("transactionId").asText();
        String status = result2.path("status").asText();
        String createdAt = result2.path("createdAt").asText();
        log.debug("dateeeee-----------------------"+createdAt);
        String date = result2.path("requestDate").asText();
        Transaction transaction = new Transaction();
        transaction.setTransactionId(result2.path("transactionId").asText());
        transaction.setStatus(result2.path("status").asText());
        transaction.setEntite(user.getEntite());
        transaction.setAgent(user.getEmail());
        transaction.setDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(createdAt));
        transaction.setMethode(result2.path("type").asText());
        JsonNode amountNode = result2.get("amount");
        JsonNode customerNode = result2.get("customer");
        JsonNode partnerNode = result2.get("partner");
        //String requestDate = result2.path("requestDate").asText();
        log.debug("amount------------ç------------------------"+amountNode);
        if(amountNode != null && customerNode != null && partnerNode != null) {
            transaction.setValue( amountNode.get("value").asLong());

            transaction.setCustomerId(customerNode.get("id").asText());

            transaction.setPartnerId(partnerNode.get("id").asText());


          //  transaction.setDate(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(createdAt));
           // transaction.setDay(LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(createdAt)).toString());

        } else {
            transaction.setValue( null);

            transaction.setCustomerId(null);

            transaction.setPartnerId(null);

           // transaction.setDate(null);

            //transaction.setDay(null);
        }

      //  transactionRepository.save(transaction);
        log.debug("createdAt------------------------------------"+createdAt);
        return ResponseEntity.ok(result);
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
