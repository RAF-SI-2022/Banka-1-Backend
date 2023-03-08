package org.banka1.userservice.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.banka1.userservice.domains.dtos.user.*;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.exceptions.BadRequestException;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.domains.mappers.UserMapper;
import org.banka1.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${password.reset.endpoint}")
    private String passwordResetEndpoint;

    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserDto> getUsers(UserFilterRequest filterRequest, Integer page, Integer size) {
        List<User> userList = new ArrayList<>();
        Position position; // Ovaj objekat i donji flag postoje samo jer je problematicno raditi sa enum-ima u bazi
        boolean positionFlag = false;

        for(Position pos : Position.values()){ // Provera u for-u da li je u nasim filterima navedena neka od postojecih pozicija
            if(filterRequest.getPosition().toUpperCase().equals(pos.toString())){
                position = Position.valueOf(filterRequest.getPosition().toUpperCase());
                userList = userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndPositionIs(filterRequest.getFirstName(), filterRequest.getLastName(), filterRequest.getEmail(), position, PageRequest.of(page, size, Sort.by("firstName").descending()));
                positionFlag = true;
            }
        }
        if(!positionFlag && filterRequest.getPosition() == ""){ // Ako u filterima nije navedena pozicija, napravi upit bez pozicije
            userList = userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(filterRequest.getFirstName(), filterRequest.getLastName(), filterRequest.getEmail(), PageRequest.of(page, size, Sort.by("firstName").descending()));
        }

        return new PageImpl<>(userList.stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList()));
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        String secretKey = RandomStringUtils.randomAlphabetic(6);

        user.setActive(true);
        user.setSecretKey(secretKey);

        userRepository.save(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordResetEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Activate account", text);

        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundExceptions("user not found"));
        UserMapper.INSTANCE.updateUserFromUserUpdateDto(user, userUpdateDto);

        userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public void resetUserPassword(PasswordDto passwordDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundExceptions("user not found"));

        if(user.getSecretKey() == null || !user.getSecretKey().equals(passwordDto.getPassword()))
            throw new BadRequestException("invalid secret key");

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        user.setSecretKey(null);

        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundExceptions("user not found"));

        if(user.getSecretKey() != null)
            throw new BadRequestException("check your email, we have already sent secret key to you");

        String secretKey = RandomStringUtils.randomAlphabetic(6);
        user.setSecretKey(secretKey);
        userRepository.save(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordResetEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Reset password", text);
    }

    public UserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundExceptions("user not found"));
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundExceptions("user not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundExceptions("user not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

}
