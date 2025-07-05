package dev.felipemlozx.api_auth.utils;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckUtilsTest {

  @Test
  void testValidPasswordAndEmail() {
    String password = "Valid1@Password";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.isEmpty());
  }

  @Test
  void testPasswordMissingLowercase() {
    String password = "PASSWORD1@";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must contain at least one lowercase letter."));
  }

  @Test
  void testPasswordMissingUppercase() {
    String password = "password1@";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must contain at least one uppercase letter."));
  }

  @Test
  void testPasswordMissingDigit() {
    String password = "Password@";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must contain at least one number."));
  }

  @Test
  void testPasswordMissingSpecialChar() {
    String password = "Password1";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must contain at least one special character."));
  }

  @Test
  void testPasswordTooShort() {
    String password = "P1@a";
    String email = "user@example.com";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must be at least 8 characters long."));
  }

  @Test
  void testInvalidEmail() {
    String password = "Valid1@Password";
    String email = "invalid-email";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Email is not valid"));
  }

  @Test
  void testMultipleErrors() {
    String password = "short";
    String email = "invalid";
    List<String> errors = CheckUtils.validatePasswordAndEmail(password, email);
    assertTrue(errors.contains("Password must contain at least one uppercase letter."));
    assertTrue(errors.contains("Password must contain at least one number."));
    assertTrue(errors.contains("Password must contain at least one special character."));
    assertTrue(errors.contains("Password must be at least 8 characters long."));
    assertTrue(errors.contains("Email is not valid"));
  }
}

