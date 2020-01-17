package com.blunt.onboard.service;

import com.blunt.onboard.config.TwilioAccount;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

  private final TwilioAccount twilioAccount;

  public Message sendWhatsappMessage(String body, String to) {
    Twilio.init(twilioAccount.getSid(), twilioAccount.getToken());
    Message message = Message.creator(
        new com.twilio.type.PhoneNumber("whatsapp:" + to),
        new com.twilio.type.PhoneNumber(twilioAccount.getMessageCenter()),
        body)
        .create();
    return message;
  }
}
