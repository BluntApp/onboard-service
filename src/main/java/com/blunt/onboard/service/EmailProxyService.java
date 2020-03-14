package com.blunt.onboard.service;


import com.blunt.onboard.dto.EmailContentDto;
import com.blunt.onboard.dto.MessageDto;
import com.blunt.onboard.proxy.EmailServiceProxyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailProxyService {

  private final EmailServiceProxyClient emailServiceProxyClient;

  @Async("threadPoolTaskExecutor")
  public void sendEmail(String subject, String toEmail, String emailContext) {
    EmailContentDto emailContentDto = new EmailContentDto();
    emailContentDto.setContent(emailContext);
    emailContentDto.setSubject(subject);
    emailContentDto.setToEmail(toEmail);
    log.info("MessageContext:" + emailContentDto.getContent());
    try{
     String emailSentStatus = emailServiceProxyClient.sendEmail(emailContentDto);
     log.info(emailSentStatus);
    } catch (Exception e){
      log.info("Exception", e);
      log.warn("Sending Email Failed.");
    }

  }
}
