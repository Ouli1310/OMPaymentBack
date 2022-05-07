package com.example.OMPayment.security.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;


public class SecurityContextSelf {

    private static SecurityContextHolderStrategy strategy;

    public static SecurityContext getContext() {
        return strategy.getContext();
    }
}
