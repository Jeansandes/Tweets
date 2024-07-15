package com.springsecurity.tweet.config;

import com.springsecurity.tweet.models.Role;
import com.springsecurity.tweet.models.UserModel;
import com.springsecurity.tweet.repositores.RoleRepository;
import com.springsecurity.tweet.repositores.UserRepository;
import com.springsecurity.tweet.services.EmailServices;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    private EmailServices emailServices;

    public AdminUserConfig(UserRepository userRepository,RoleRepository roleRepository,BCryptPasswordEncoder passwordEncoder,EmailServices emailServices) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailServices = emailServices;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var role = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");
        userAdmin.ifPresentOrElse(
                user -> {
                    //userRepository.delete(user);
                    System.out.println("admin já existe!");
                },
                () -> {
                    var user = new UserModel();
                    user.setUsername("admin");
                    user.setEmail("sandesjean.sandes@gmail.com");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(role));

                    userRepository.save(user);
                    emailServices.sendTxtMail(user.getEmail()
                            , "Logado com sucesso no twitter!"
                            ,"parabéns "+user.getUsername()+" voce foi cadastrado com sucesso!");
                }
        );
    }
}













