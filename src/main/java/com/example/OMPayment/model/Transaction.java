package com.example.OMPayment.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "TD_transaction")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;
    @Column(name = "request_id")
    private Long requestId;
    @Column(name = "transaction_status")
    private Status status;
    @Column(name = "transaction_description")
    private String description;
    @Column(name = "transaction_customerId")
    private Long customerId;
    @Column(name = "transaction_partnerId")
    private Long partnerId;
}
