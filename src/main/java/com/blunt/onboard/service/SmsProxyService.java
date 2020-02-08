package com.blunt.onboard.service;


import com.blunt.onboard.dto.MessageDto;
import com.blunt.onboard.proxy.SmsServiceProxyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsProxyService {

  private final SmsServiceProxyClient smsServiceProxyClient;

  @Async("threadPoolTaskExecutor")
  public void sendSMSText(String mobile, String smsContext) {
    MessageDto messageDto = new MessageDto();
    messageDto.setMessageContext(smsContext);
    messageDto.setToMobile(mobile);
    log.info("MessageContext:" + messageDto.getMessageContext());
    try{
      System.out.println("Execute method with configured executor - "
          + Thread.currentThread().getName());
      Thread.currentThread().sleep(50000);
      smsServiceProxyClient.sendTextSMS(messageDto);
    } catch (Exception e){
      log.info("Exception", e);
      log.warn("Sending SMS Failed.");
    }

  }
}
