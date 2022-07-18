package com.example.OMPayment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "TD_PaymentActor")
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class PaymentActor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentActorId")
    private Long id;
    @Column(name = "paymentActorMsisdn")
    private String msisdn;
    @Column(name = "paymentActorPinCode")
    private Long pinCode;
    @Column(name = "paymentActorMerchantCode")
    private Long merchantCode;
    @Column(name = "paymentActorOtp")
    private String otp;
    @Column(name = "paymentActorType")
    private String type;
    @Column(name = "paymentActorGrade")
    private String grade;
    @Column(name = "paymentActorExpiresAt")
    private Date expiresAt;

    public PaymentActor(String msisdn, Long pinCode, Long merchantCode, String type, String grade, Date expiresAt) {
        this.msisdn = msisdn;
        this.pinCode = pinCode;
        this.merchantCode = merchantCode;
        this.type = type;
        this.grade = grade;
        this.expiresAt = expiresAt;
    }
}
