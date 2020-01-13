package com.blunt.onboard.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BluntException extends RuntimeException {

  private String message;
  private int code;
  private HttpStatus status;
}
