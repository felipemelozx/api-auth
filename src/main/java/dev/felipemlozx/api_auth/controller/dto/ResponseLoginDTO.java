package dev.felipemlozx.api_auth.controller.dto;

public record ResponseLoginDTO  (String status,
    String message,
    String email,
    String token,
boolean success  // Indica se a operação foi bem-sucedida
) {

public ResponseLoginDTO(String email) {
  this("error", "Email not verify.", email, null, false);
}

public ResponseLoginDTO(String email, String token) {
  this("success", "successful login", email, token, true);
}
}