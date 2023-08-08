package com.example.OMPayment.model;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@Data
@Entity
@Table(name = "TP_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@XmlRootElement
public class Status{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
    private Long id;
    @Column(name = "statusName")
    private String name;







}
