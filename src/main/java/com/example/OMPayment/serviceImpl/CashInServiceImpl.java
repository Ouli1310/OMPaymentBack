package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.CashIn;
import com.example.OMPayment.repository.CashInRepository;
import com.example.OMPayment.service.CashInService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CashInServiceImpl implements CashInService {

    private  final CashInRepository cashInRepository;


    @Override
    public List<CashIn> listCashIns() {
        return cashInRepository.findAll();
    }

    @Override
    public List<CashIn> listCashInsParStatus(String status) {
        return cashInRepository.findByStatus(status);
    }

    @Override
    public List<CashIn> listCashInParMethode(String methode) {
        return cashInRepository.findByMethodeOrderByDateDesc(methode);
    }

    @Override
    public List<CashIn> listCashInParPNR(String pnr) {
        return cashInRepository.findByReference(pnr);
    }
}
