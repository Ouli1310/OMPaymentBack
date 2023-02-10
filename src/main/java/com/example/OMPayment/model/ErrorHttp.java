package com.example.OMPayment.model;

import lombok.*;

import javax.persistence.*;


@Data
@Entity
@Table(name = "TD_error")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorHttp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "errorId")
    private Long id;
    @Column(name = "errorType")
    private String type;
    @Column(name = "errorTitle")
    private String title;
    @Column(name = "errorInstance")
    private String instance;
    @Column(name = "errorStatus")
    private String status;
    @Column(name = "errorCode")
    private String code;
    @Column(name = "errorDetail")
    private String detail;
}
