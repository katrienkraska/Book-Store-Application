package org.example.mapper;

import org.example.dto.user.UserRegistrationRequestDto;
import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperManual {
    public User toModel(UserRegistrationRequestDto dto) {
        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setShippingAddress(dto.getShippingAddress());

        return user;
    }
}

