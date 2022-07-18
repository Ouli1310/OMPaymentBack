package com.example.OMPayment.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "TP_entite")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Entite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entiteId")
    private Long id;
    @Column(name = "entiteCode")
    private String code;
    @Column(name = "entiteName")
    private String name;
    @Column(name = "entiteMsisdn")
    private String msisdn;
    @Column(name = "entiteCodeMarchand")
    private String codeMarchand;

}
