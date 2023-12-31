package com.andreasx42.taskmanagerapi.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.andreasx42.taskmanagerapi.dto.UserDto;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.exception.DuplicateEntityException;
import com.andreasx42.taskmanagerapi.exception.EntityNotFoundException;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import com.andreasx42.taskmanagerapi.service.api.IUserService;
import com.andreasx42.taskmanagerapi.service.mapper.impl.UserMapper;

import lombok.AllArgsConstructor;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    @Override
    public User getByName(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        return userOptional.orElseThrow(() -> new EntityNotFoundException(username, User.class));
    }

    @Override
    public User getById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.orElseThrow(() -> new EntityNotFoundException(id, User.class));
    }

    @Override
    public Page<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::mapFromEntity);
    }

    @Override
    public UserDto create(UserDto userDto) {

        if (userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEntityException("email", userDto.email(), User.class);
        }

        if (userRepository.findByUsername(userDto.username()).isPresent()) {
            throw new DuplicateEntityException("username", userDto.username(), User.class);
        }

        User user = userMapper.mapToEntity(userDto);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userMapper.mapFromEntity(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {

        User user = getById(id);

        if (!userDto.email().equals(user.getEmail()) && userRepository.findByEmail(userDto.email()).isPresent()) {
            throw new DuplicateEntityException("email", userDto.email(), User.class);
        }

        if (userDto.password() != null) {
            user.setPassword(bCryptPasswordEncoder.encode(userDto.password()));
        }

        user.setEmail(userDto.email());
        return userMapper.mapFromEntity(userRepository.save(user));

    }

}
