package com.example.practice.servingwebcontent;

import com.example.practice.domain.Role;
import com.example.practice.domain.User;
import com.example.practice.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // Инъекция PasswordEncoder

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    public static boolean isValidString(String str) {
        if (str == null || str.isEmpty()) {
            return false; // Или true, в зависимости от ваших требований
        }
        String regex = "^[a-zA-Z._1-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        if(user.getUsername()==null) {
            model.addAttribute("message", "Поле 'username' обязательно");
            return "registration";
        }
        if(!isValidString(user.getUsername())) {
            model.addAttribute("message", "Поле 'username' может содержать только латинские буквы, цифры и '_', '.'");
            return "registration";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);

        return "redirect:/login";
    }
}
