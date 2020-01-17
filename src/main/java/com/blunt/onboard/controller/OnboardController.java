package com.blunt.onboard.controller;

import com.blunt.onboard.dto.BluntDto;
import com.blunt.onboard.dto.InviteDto;
import com.blunt.onboard.dto.ValidateDto;
import com.blunt.onboard.proxy.FollowServiceProxyClient;
import com.blunt.onboard.service.OnboardService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboard")
public class OnboardController {

  private final OnboardService onboardService;
  private final FollowServiceProxyClient followServiceProxyClient;

  @GetMapping("test")
  public ResponseEntity<Object> testService() {
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  @GetMapping("check/{mobile}")
  public ResponseEntity<Object> checkAvailability(@PathVariable String mobile) {
    return onboardService.checkAvailability(mobile);
  }

  @PostMapping("create")
  public ResponseEntity<Object> validateOtpAndGenerateUserId(@RequestBody ValidateDto validateDto) {
    return onboardService.validateOtpAndGenerateUserId(validateDto.getOtp(), validateDto.getMobile());
  }

  @PostMapping("signup")
  public ResponseEntity<Object> signUp(@Valid @RequestBody BluntDto bluntDto) {
    return onboardService.signUp(bluntDto);
  }

  @PostMapping("signin")
  public ResponseEntity<Object> signIn(@Valid @RequestBody ValidateDto validateDto) {
    return onboardService.signIn(validateDto);
  }

  @GetMapping("invite")
  public ResponseEntity<Object> invite(@RequestBody InviteDto inviteDto){
    return onboardService.invite(inviteDto);
  }

  @GetMapping("resend/{mobile}")
  public ResponseEntity<Object> resendOtp(@PathVariable String mobile) {
    return onboardService.resendOtp(mobile);
  }

}
