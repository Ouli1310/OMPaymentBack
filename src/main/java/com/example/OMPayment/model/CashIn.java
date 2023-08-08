package com.example.OMPayment.model;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "TD_CashIn")

public class CashIn implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "transaction_id", columnDefinition = "TEXT")
    private String transactionId;
    @Column(name = "cashIn_id", columnDefinition = "TEXT")
    private String cashInId;
    @Column(name = "cashIn_methode", columnDefinition = "TEXT")
    private String methode;
    @Column(name = "request_id", columnDefinition = "TEXT")
    private String requestId;
    @Column(name = "reference", columnDefinition = "TEXT")
    private String reference;
    @Column(name = "cashIn_status")
    private String status;
    @Column(name = "cashIn_description")
    private String description;
    @Column(name = "cashIn_customerId")
    private String customerId;
    @Column(name = "cashIn_partnerId")
    private String partnerId;
    @Column(name = "cashIn_agent")
    private String agent;
    @Column(name = "cashIn_value")
    private Long value;
    @Column(name = "cashIn_date")
    private Date date;
    @Column(name = "cashIn_day")
    private String day;
    @Column(name = "cashIn_entite")
    private Long entite;
}
