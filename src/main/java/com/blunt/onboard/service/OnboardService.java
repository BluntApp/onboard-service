package com.blunt.onboard.service;


import com.blunt.onboard.dto.BluntDto;
import com.blunt.onboard.dto.FollowDto;
import com.blunt.onboard.dto.InviteDto;
import com.blunt.onboard.dto.ValidateDto;
import com.blunt.onboard.entity.Blunt;
import com.blunt.onboard.entity.Capacitor;
import com.blunt.onboard.error.BluntException;
import com.blunt.onboard.mapper.BluntMapper;
import com.blunt.onboard.proxy.FollowServiceProxyClient;
import com.blunt.onboard.repository.BluntRepository;
import com.blunt.onboard.repository.custom.CapacitorRepository;
import com.blunt.onboard.type.Status;
import com.blunt.onboard.util.BluntConstant;
import com.blunt.onboard.util.BluntUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardService {

  private final BluntMapper bluntMapper;
  private final BluntRepository bluntRepository;
  private final CapacitorRepository capacitorRepository;
  private final PasswordEncoder passwordEncoder;
  private final FollowServiceProxyClient followServiceProxyClient;
  private final SmsService smsService;

  public ResponseEntity<Object> signUp(BluntDto bluntDto) {
    Capacitor capacitor = capacitorRepository.findByUserId(bluntDto.getUserId());
    if (ObjectUtils.isEmpty(capacitor)) {
      throw new BluntException(BluntConstant.INVALID_MOBILE, HttpStatus.NOT_FOUND.value(),
          HttpStatus.NOT_FOUND);
    }
    if (!validateMobile(bluntDto.getMobile())) {
      throw new BluntException(BluntConstant.MOBILE_UNAVAILABLE, HttpStatus.CONFLICT.value(),
          HttpStatus.CONFLICT);
    }
    if (!(boolean) validateOtp(bluntDto.getOtp(), bluntDto.getMobile()).getBody()) {
      throw new BluntException(BluntConstant.OTP_MISMATCH, HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED);
    }
    Blunt blunt = bluntMapper.bluntDtoToBlunt(bluntDto);
    blunt = bluntRepository.save(blunt);
    if (!ObjectUtils.isEmpty(bluntDto.getInvitedBy())) {
      followServiceProxyClient.followBlunt(frameFollow(blunt.getId(), bluntDto.getInvitedBy()));
    }
    capacitorRepository.delete(capacitor);
    return new ResponseEntity<>(bluntMapper.bluntToBluntDto(blunt), HttpStatus.OK);
  }

  private ResponseEntity<Object> validateOtp(String otp, String mobile) {
    Capacitor capacitor = capacitorRepository.findByMobile(mobile);
    if (ObjectUtils.isEmpty(capacitor) || capacitor.getStatus().equals(Status.EXPIRED)) {
      throw new BluntException(BluntConstant.REGENERATE_OTP,
          HttpStatus.BANDWIDTH_LIMIT_EXCEEDED.value(),
          HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
    }
    return new ResponseEntity<>(capacitor.getOtp().equals(otp), HttpStatus.OK);
  }

  private FollowDto frameFollow(ObjectId bluntId, String invitedBy) {
    FollowDto followDto = new FollowDto();
    followDto.setBluntId(bluntId);
    followDto.setStatus(Status.PENDING);
    Blunt invitedBlunt = bluntRepository.findBluntByUserId(invitedBy);
    if (ObjectUtils.isEmpty(invitedBlunt)) {
      throw new BluntException(BluntConstant.INVALID_INVITER_ID, HttpStatus.NOT_FOUND.value(),
          HttpStatus.NOT_FOUND);
    }
    followDto.setFollowerId(invitedBlunt.getId());
    followDto.setFollowerName(invitedBlunt.getFirstName());
    followDto.setFollowerUserId(invitedBlunt.getUserId());
    return followDto;
  }

  private Boolean validateMobile(String mobile) {
    Blunt blunt = bluntRepository.findByMobile(mobile);
    return ObjectUtils.isEmpty(blunt);
  }

  public ResponseEntity<Object> checkAvailability(String mobile) {
    if (validateMobile(mobile)) {
      generateAndSendOTP(mobile);
      return new ResponseEntity<>(BluntConstant.OTP_SENT, HttpStatus.OK);
    }
    throw new BluntException(BluntConstant.MOBILE_UNAVAILABLE, HttpStatus.CONFLICT.value(),
        HttpStatus.CONFLICT);
  }

  private void generateAndSendOTP(String mobile) {
    String otp = BluntUtil.generateOTP(4);
    //send otp to mobile
    Capacitor capacitor =
        ObjectUtils.isEmpty(capacitorRepository.findByMobile(mobile)) ? new Capacitor()
            : capacitorRepository.findByMobile(mobile);
    capacitor.setMobile(mobile);
    capacitor.setOtp(otp);
    capacitor.setStatus(Status.ACTIVE);
    capacitor.setTime(LocalDateTime.now());
    capacitorRepository.save(capacitor);
  }

  public ResponseEntity<Object> validateOtpAndGenerateUserId(String otp, String mobile) {
    if (!(boolean) validateOtp(otp, mobile).getBody()) {
      throw new BluntException(BluntConstant.OTP_MISMATCH, HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED);
    }
    String userId = generateAndValidateUserId();
    Capacitor capacitor = capacitorRepository.findByMobile(mobile);
    capacitor.setUserId(userId);
    capacitorRepository.save(capacitor);
    return new ResponseEntity<>(userId, HttpStatus.OK);
  }

  // do more validation
  private String generateAndValidateUserId() {
    String userId = BluntUtil.generateUserId(8);
    if (!ObjectUtils.isEmpty(capacitorRepository.findByUserId(userId))) {
      userId = BluntUtil.generateUserId(8);
    }
    return userId;
  }

  public ResponseEntity<Object> resendOtp(String mobile) {
    Capacitor capacitor = capacitorRepository.findByMobile(mobile);
    if (ObjectUtils.isEmpty(capacitor)) {
      throw new BluntException(BluntConstant.INVALID_MOBILE, HttpStatus.NOT_ACCEPTABLE.value(),
          HttpStatus.NOT_ACCEPTABLE);
    }
    String otp = BluntUtil.generateOTP(4);
    capacitor.setOtp(otp);
    capacitor.setStatus(Status.ACTIVE);
    capacitor.setTime(LocalDateTime.now());
    capacitorRepository.save(capacitor);
    //send otp sms TODO
    return new ResponseEntity<>(BluntConstant.OTP_SENT, HttpStatus.OK);
  }

  public ResponseEntity<Object> signIn(ValidateDto validateDto) {
    Blunt blunt = bluntRepository.findByMobile(validateDto.getMobile());
    if (ObjectUtils.isEmpty(blunt)) {
      throw new BluntException(BluntConstant.MOBILE_NOT_REGISTERED, HttpStatus.NOT_FOUND.value(),
          HttpStatus.NOT_FOUND);
    } else if (!passwordEncoder.matches(validateDto.getPassword(), blunt.getPassword())) {
      throw new BluntException(BluntConstant.INVALID_CREDENTIAL, HttpStatus.UNAUTHORIZED.value(),
          HttpStatus.UNAUTHORIZED);
    }
    // get User post  TODO
    if (!ObjectUtils.isEmpty(validateDto.getInvitedBy())) {
      followServiceProxyClient.followBlunt(frameFollow(blunt.getId(), validateDto.getInvitedBy()));
    }

    return new ResponseEntity<>(BluntConstant.LOGGED_IN, HttpStatus.OK);
  }

  public ResponseEntity<Object> invite(InviteDto inviteDto) {
    inviteDto.getMobileList().stream().forEach(mobile -> {
      Blunt blunt = bluntRepository.findByMobile(mobile);
      if(ObjectUtils.isEmpty(blunt)) {
        signUpInvite(mobile, inviteDto.getFollowDto());
      } else {
        signIn(mobile, inviteDto.getFollowDto());
      }
    });
    return new ResponseEntity<>(BluntConstant.NOTIFICATION_SMS_SENT, HttpStatus.OK);
  }

  private void signIn(String mobile, FollowDto followDto) {
    String link = "https://localhost:3000/blunt/signin/"+ followDto.getFollowerUserId();
    String smsContext = prepareSmsContext(link, followDto.getFollowerName());
    sendInvitation(mobile, smsContext);
  }

  private void signUpInvite(String mobile, FollowDto followDto) {
    String link = "https://localhost:3000/blunt/signup/"+ followDto.getFollowerUserId();
    String smsContext = prepareSmsContext(link, followDto.getFollowerName());
    sendInvitation(mobile, smsContext);
  }

  private String prepareSmsContext(String inviteLink, String firstName) {
    String context = "Hi ,\n" + firstName + " Invited to link:" + inviteLink;
    return context;
  }

  private void sendInvitation(String mobile, String smsContext) {
    smsService.sendWhatsappMessage(smsContext,mobile);
  }
}
