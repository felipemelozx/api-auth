package dev.felipemlozx.api_auth.services;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  public void create(CreateUserDto userDto){
    var user = new User();
    user.setName(userDto.name());
    user.setPassword(userDto.password());
    user.setEmail(userDto.email());

    userRepository.save(user);
  }

  public List<User> listUser(){
    return userRepository.findAll();
  }

}
