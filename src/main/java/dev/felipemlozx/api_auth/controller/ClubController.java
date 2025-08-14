package dev.felipemlozx.api_auth.controller;

import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.felipemlozx.api_auth.dto.UserJwtDTO;
import dev.felipemlozx.api_auth.utils.ApiResponse;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("club/secret")
public class ClubController {
  private Random random = new Random();

  private List<String> phrasesList = List.of(
    "Acredite no seu potencial e svá além.",
    "Cada desafio é uma oportunidade de crescimento.",
    "Persistência é o caminho para o sucesso.",
    "Faça hoje o que outros não querem para conquistar amanhã.",
    "Transforme obstáculos em degraus para o seu progresso.",
    "O esforço de hoje constrói a vitória de amanhã.",
    "A disciplina supera o talento quando o talento não se esforça.",
    "Sonhe grande, comece pequeno, aja agora.",
    "Não espere por oportunidades, crie-as.",
    "Seu único limite é você mesmo."
);


  @GetMapping
  public ResponseEntity<ApiResponse<String>> getPhrases(){
    int index = random.nextInt(phrasesList.size());
    String mensagem = phrasesList.get(index);
    UserJwtDTO user = (UserJwtDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ResponseEntity.ok().body(ApiResponse.success(user.name() + " are authorization" , user.email() + " " + mensagem));
  }
}
