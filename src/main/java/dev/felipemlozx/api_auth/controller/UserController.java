package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody CreateUserDto user){
    userService.create(user);
    return  ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<User>> listAll(){
    var body = userService.listUser();
    return  ResponseEntity.ok(body);
  }
}
