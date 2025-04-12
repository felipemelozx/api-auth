package dev.felipemlozx.api_auth.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "tb_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  @Column(unique = true)
  private String email;
  private String password;
  @Column(name = "timeVerify", updatable = false)
  private Instant timeVerify;
  private boolean verified;

  public User() {
  }

  @PrePersist
  protected void onCreate() {
    if (timeVerify == null) {
      timeVerify = Instant.now().plusSeconds(1800);
    }
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

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public Instant getTimeVerify() {
    return timeVerify;
  }

  public void setTimeVerify(Instant timeVerify) {
    this.timeVerify = timeVerify;
  }
}