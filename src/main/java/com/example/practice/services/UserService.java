package com.example.practice.services;

import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo)
    {
        this.userRepo=userRepo;
    }

    public User getByUsername(String username)
    {
        return userRepo.findByUsername(username);
    }

    public void saveUser(User user)
    {
        userRepo.save(user);
    }

    public static boolean isValidString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z._1-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }
}
