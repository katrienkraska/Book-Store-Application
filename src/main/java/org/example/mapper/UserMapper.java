package org.example.mapper;

import org.example.dto.user.UserRegistrationRequestDto;
import org.example.dto.user.UserResponseDto;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "firstName", target = "firstName")
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);
}
