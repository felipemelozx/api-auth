package dev.felipemlozx.api_auth.controller;

import dev.felipemlozx.api_auth.controller.dto.CreateUserDto;
import dev.felipemlozx.api_auth.controller.dto.UserResponse;
import dev.felipemlozx.api_auth.entity.User;
import dev.felipemlozx.api_auth.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> findById(@PathVariable(name = "id") Long id){
    User user = userService.findById(id);
    UserResponse userResponse = UserResponse.fromUser(user);
    return  ResponseEntity.ok(userResponse);
  }

  @PostMapping
  public ResponseEntity<List<String>> create(@RequestBody CreateUserDto user){
    var created = userService.register(user);
    if (created.isEmpty()){
    return  ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().body(created);
  }

}
