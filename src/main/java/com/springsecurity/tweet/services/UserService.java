package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.UserAlreadyExistsException;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.RoleRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private EmailServices emailServices;

    public UserService( UserRepository userRepository,RoleRepository roleRepository
                        ,BCryptPasswordEncoder passwordEncoder,EmailServices emailServices) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailServices = emailServices;
    }

    @Transactional
    public void save(UserDto dto) {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromdb = userRepository.findByUsername(dto.username());
        if(userFromdb.isPresent()){
            throw new UserAlreadyExistsException("usuário já existe!");
        }
        var user = new UserModel();
        user.setUsername(dto.username());
        user.setRoles(Set.of(basicRole));
        user.setPassword(passwordEncoder.encode(dto.password()));
        emailServices.sendTxtMail(dto.email()
                , "Logado com sucesso no twitter!"
                ,"parabéns "+user.getUsername()+" voce foi cadastrado com sucesso!");
        userRepository.save(user);

    }

    public List<UserModel> findAll() {
        var users = userRepository.findAll();
        return users;
    }
}




















