package com.blunt.onboard.proxy;

import com.blunt.onboard.dto.FollowDto;
import com.blunt.onboard.dto.MessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-service")
public interface SmsServiceProxyClient {

  @PostMapping(path="/api/v1/sms/text")
  public String  sendTextSMS(@RequestBody MessageDto messageDto);

}
