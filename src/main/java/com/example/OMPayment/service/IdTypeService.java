package com.example.OMPayment.service;

import com.example.OMPayment.model.IdType;

import java.util.List;

public interface IdTypeService {

    List<IdType> getListIdType();
    String getIdByType(Long id, Long idType);
}
