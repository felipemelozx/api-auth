package dev.felipemlozx.api_auth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CheckUtils {
  private CheckUtils(){}

  private static final String LOWERCASE_REGEX = "(?=.*[a-z])";
  private static final String UPPERCASE_REGEX = "(?=.*[A-Z])";
  private static final String DIGIT_REGEX = "(?=.*\\d)";
  private static final String SPECIAL_CHAR_REGEX = "(?=.*[@#$%^&+=!])";
  private static final String LENGTH_REGEX = ".{8,}";
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  private static final String LOWERCASE_ERROR = "Password must contain at least one lowercase letter.";
  private static final String UPPERCASE_ERROR = "Password must contain at least one uppercase letter.";
  private static final String DIGIT_ERROR = "Password must contain at least one number.";
  private static final String SPECIAL_CHAR_ERROR = "Password must contain at least one special character.";
  private static final String LENGTH_ERROR = "Password must be at least 8 characters long.";
  private static final String EMAIL_INVALID = "Email is not valid";
  public static List<String> validatePasswordAndEmail(String password, String email) {
    List<String> errors = new ArrayList<>();

    if (!Pattern.compile(LOWERCASE_REGEX).matcher(password).find()) {
      errors.add(LOWERCASE_ERROR);
    }
    if (!Pattern.compile(UPPERCASE_REGEX).matcher(password).find()) {
      errors.add(UPPERCASE_ERROR);
    }
    if (!Pattern.compile(DIGIT_REGEX).matcher(password).find()) {
      errors.add(DIGIT_ERROR);
    }
    if (!Pattern.compile(SPECIAL_CHAR_REGEX).matcher(password).find()) {
      errors.add(SPECIAL_CHAR_ERROR);
    }
    if (!Pattern.compile(LENGTH_REGEX).matcher(password).matches()) {
      errors.add(LENGTH_ERROR);
    }
    if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
      errors.add(EMAIL_INVALID);
    }

    return errors;
  }
}
