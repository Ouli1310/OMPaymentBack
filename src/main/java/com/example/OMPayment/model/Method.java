package com.example.OMPayment.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "TP_method")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Method implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "methodId")
    private Long id;
    @Column(name = "methodName")
    private String name;





}
