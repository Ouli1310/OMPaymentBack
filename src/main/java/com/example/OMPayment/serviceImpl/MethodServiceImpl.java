package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.Method;
import com.example.OMPayment.repository.MethodRepository;
import com.example.OMPayment.service.MethodService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class MethodServiceImpl implements MethodService {

    private final MethodRepository methodRepository;

    @Override
    public List<Method> getAllMethod() {
        return methodRepository.findAll();
    }
}
