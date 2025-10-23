package org.example.service.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegistrationRequestDto;
import org.example.dto.user.UserResponseDto;
import org.example.exception.RegistrationException;
import org.example.mapper.UserMapper;
import org.example.model.Role;
import org.example.model.RoleName;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "User with email "
                            + requestDto.getEmail()
                            + " already exists.");

        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByRole(RoleName.ROLE_USER)
                .orElseThrow(() -> new RegistrationException(
                        "Default role " + RoleName.ROLE_USER + " not found in database."));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
        shoppingCartService.saveShoppingCartForUser(user);
        return userMapper.toDto(user);
    }
}
