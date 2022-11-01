package com.example.OMPayment.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "TD_transaction")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "transaction_id", columnDefinition = "TEXT")
    private String transactionId;
    @Column(name = "transaction_methode", columnDefinition = "TEXT")
    private String methode;
    @Column(name = "request_id", columnDefinition = "TEXT")
    private String requestId;
    @Column(name = "reference", columnDefinition = "TEXT")
    private String reference;
    @Column(name = "transaction_status")
    private String status;
    @Column(name = "transaction_description")
    private String description;
    @Column(name = "transaction_customerId")
    private String customerId;
    @Column(name = "transaction_partnerId")
    private String partnerId;
    @Column(name = "transaction_agent")
    private String agent;
    @Column(name = "transaction_value")
    private Float value;
    @Column(name = "transaction_date")
    private Date date;
    @Column(name = "transaction_day")
    private String day;
    @Column(name = "transaction_entite")
    private Long entite;
}
