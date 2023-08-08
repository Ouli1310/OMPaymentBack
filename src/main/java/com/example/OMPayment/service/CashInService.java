package com.example.OMPayment.service;

import com.example.OMPayment.model.CashIn;
import com.example.OMPayment.model.Transaction;

import java.util.List;


public interface CashInService {

    List<CashIn> listCashIns();
    List<CashIn> listCashInsParStatus(String status);
    List<CashIn> listCashInParMethode(String methode);
    List<CashIn> listCashInParPNR(String pnr);

}
