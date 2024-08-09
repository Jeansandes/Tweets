package com.springsecurity.tweet.services;

import com.springsecurity.tweet.Exceptions.UserAlreadyExistsException;
import com.springsecurity.tweet.Exceptions.UserNotFoundException;
import com.springsecurity.tweet.dtos.UserDto;
import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.RoleRepository;
import com.springsecurity.tweet.repositores.TweetRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Var;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private TweetRepository tweetRepository;

    private KafkaTemplate<String, UserDto> kafkaTemplate;

    public UserService(UserRepository userRepository, RoleRepository roleRepository
                        , BCryptPasswordEncoder passwordEncoder
            , TweetRepository tweetRepository,KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tweetRepository = tweetRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void save(UserDto userDto) {
        int partition = 1;
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromdb = userRepository.findByUsername(userDto.username());
        if(userFromdb.isPresent()){
            throw new UserAlreadyExistsException("usuário já existe!");
        }
        var user = new UserModel();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setRoles(Set.of(basicRole));
        user.setPassword(passwordEncoder.encode(userDto.password()));
        userRepository.save(user);
        sendMessage(userDto,partition);

    }

    public void sendMessage(UserDto userDto,int partition) {
        System.out.println("Sent message to partition: " + partition);
        System.out.println("Sending Order: " + userDto.username());
        this.kafkaTemplate.send("tweet_email_kafka",partition,null, userDto);
    }

    public List<UserModel> findAll() {
        var users = userRepository.findAll();
        return users;
    }

    @Transactional
    public boolean delete(String username, JwtAuthenticationToken token) {
        int partition = 2;
        var user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        var authenticatedUsername = userRepository.findById(UUID.fromString(token.getName()));
        if (hasAdminAuthority() || username.equals(authenticatedUsername.get().getUsername())) {
            // Deletar todos os tweets do usuário
            tweetRepository.deleteAllByUser_UserId(user.get().getUserId());
            userRepository.delete(user.get());
            UserDto dto = new UserDto(user.get().getUsername(),user.get().getEmail(),user.get().getPassword());
            sendMessage(dto,partition);
            return true;
        } else {
            return false;
        }
    }
    public boolean hasAdminAuthority() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("SCOPE_admin"));
    }
}




















