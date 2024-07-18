package dev.felipemlozx.api_auth.entity;

import dev.felipemlozx.api_auth.services.CryptoService;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String email;
  private String password;

  public User() {
  }


  public String getRawPassword() {
    return rawPassword;
  }

  public void setRawPassword(String rawPassword) {
    this.rawPassword = rawPassword;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @PrePersist
  public void prePersist(){
    this.password = CryptoService.encrypt(password);
  }

  @PostLoad
  public void postLoad(){
    this.password = CryptoService.decrypt(password);
  }
}