package com.example.OMPayment.serviceImpl;

import com.example.OMPayment.model.IdType;
import com.example.OMPayment.model.User;
import com.example.OMPayment.repository.IdTypeRepository;
import com.example.OMPayment.service.IdTypeService;
import com.example.OMPayment.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class IdTypeServiceImpl implements IdTypeService {

    private final IdTypeRepository idTypeRepository;
    private UserService userService;

    @Override
    public List<IdType> getListIdType() {
        return idTypeRepository.findAll();
    }

    @Override
    public String getIdByType(Long id, Long idType) {
        User user = userService.getUserById(id);
        if (idType == 1) {
            return user.getMsisdn();
        }
        return user.getCode();
    }
}
