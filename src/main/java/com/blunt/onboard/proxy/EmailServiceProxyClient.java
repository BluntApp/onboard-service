package com.blunt.onboard.proxy;

import com.blunt.onboard.dto.EmailContentDto;
import com.blunt.onboard.dto.MessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service")
public interface EmailServiceProxyClient {

  @PostMapping(path="/api/v1/email/send")
  public String sendEmail(@RequestBody EmailContentDto emailContentDto);

}
