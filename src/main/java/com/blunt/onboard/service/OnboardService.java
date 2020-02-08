package com.blunt.onboard.service;


import com.blunt.onboard.dto.BluntDto;
import com.blunt.onboard.dto.FollowDto;
import com.blunt.onboard.dto.MessageDto;
import com.blunt.onboard.dto.ValidateDto;
import com.blunt.onboard.entity.Blunt;
import com.blunt.onboard.entity.Capacitor;
import com.blunt.onboard.error.BluntException;
import com.blunt.onboard.mapper.BluntMapper;
import com.blunt.onboard.proxy.FollowServiceProxyClient;
import com.blunt.onboard.proxy.SmsServiceProxyClient;
import com.blunt.onboard.repository.BluntRepository;
import com.blunt.onboard.repository.custom.CapacitorRepository;
import com.blunt.onboard.type.Status;
import com.blunt.onboard.util.BluntConstant;
import com.blunt.onboard.util.BluntUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
  private final SmsServiceProxyClient smsServiceProxyClient;
  private final SmsProxyService smsProxyService;

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
    manageFollowers(blunt);
    capacitorRepository.delete(capacitor);
    return new ResponseEntity<>(bluntMapper.bluntToBluntDto(blunt), HttpStatus.OK);
  }

  private void manageFollowers(Blunt blunt) {
    ResponseEntity<Object> followersResponseEntity = followServiceProxyClient
        .fetchInactiveFollowers(blunt.getMobile());
    List<LinkedHashMap> inActiveFollowers = (List<LinkedHashMap>) followersResponseEntity.getBody();
    ArrayList<FollowDto> followDtoList = new ArrayList();
    for(LinkedHashMap followDtoMap : inActiveFollowers){
      FollowDto followDto = new FollowDto();
      followDto.setStatus(Status.PENDING);
      followDto.setBluntId(blunt.getId());
      followDto.setFollowerUserId(followDtoMap.get("followerUserId").toString());
      followDto.setFollowerName(followDtoMap.get("followerName").toString());
      followDto.setFollowerId(new ObjectId(followDtoMap.get("followerId").toString()));
      followDto.setId(new ObjectId(followDtoMap.get("id").toString()));
      followDtoList.add(followDto);
    }
    try{
      ResponseEntity<Object> followResponseEntity = followServiceProxyClient
          .updateFollowBlunt(followDtoList);
    } catch(Exception e){
      log.info(e.getMessage());
    }

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

  private FollowDto frameSignInFollow(ObjectId bluntId, Blunt inviterBlunt) {
    validateInviter(inviterBlunt);
    FollowDto followDto = new FollowDto();
    followDto.setStatus(Status.PENDING);
    followDto.setBluntId(bluntId);
    followDto.setFollowerId(inviterBlunt.getId());
    followDto.setFollowerName(inviterBlunt.getFirstName());
    followDto.setFollowerUserId(inviterBlunt.getUserId());
    return followDto;
  }

  private FollowDto frameSignUpFollow(Blunt inviterBlunt, String mobile) {
    validateInviter(inviterBlunt);
    FollowDto followDto = new FollowDto();
    followDto.setStatus(Status.IN_ACTIVE);
    followDto.setMobile(mobile);
    followDto.setFollowerId(inviterBlunt.getId());
    followDto.setFollowerName(inviterBlunt.getFirstName());
    followDto.setFollowerUserId(inviterBlunt.getUserId());
    return followDto;
  }

  private void validateInviter(Blunt inviterBlunt) {
    if (ObjectUtils.isEmpty(inviterBlunt)) {
      throw new BluntException(BluntConstant.INVALID_INVITER_ID, HttpStatus.NOT_FOUND.value(),
          HttpStatus.NOT_FOUND);
    }
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
    String smsContext = prepareOTPSmsContext(otp);
    smsProxyService.sendSMSText(mobile, smsContext);
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

    String smsContext = prepareOTPSmsContext(otp);
    smsProxyService.sendSMSText(mobile, smsContext);
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
    return new ResponseEntity<>(blunt, HttpStatus.OK);
  }

  public ResponseEntity<Object> invite(List<String> mobileList, String inviterId) {
    mobileList.stream().forEach(mobile -> {
      Blunt blunt = bluntRepository.findByMobile(mobile);
      if (ObjectUtils.isEmpty(blunt)) {
        signUpInvite(mobile, inviterId);
      } else {
        signInInvite(blunt, inviterId);
      }
    });
    return new ResponseEntity<>(BluntConstant.NOTIFICATION_SMS_SENT, HttpStatus.OK);
  }

  private void signInInvite(Blunt receiverBlunt, String inviterId) {
    Optional<Blunt> inviterBlunt = bluntRepository.findById(new ObjectId(inviterId));
    inviterBlunt.ifPresent(iBlunt -> {
      String smsContext = "Hi " + receiverBlunt.getFirstName() + ",\n" + iBlunt.getFirstName()
          + "is Interested to follow you on Blunt";
      ResponseEntity<Object> followResponseEntity = followServiceProxyClient
          .followBlunt(frameSignInFollow(receiverBlunt.getId(), iBlunt));

      if (followResponseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
        throw new BluntException(
            ((LinkedHashMap) followResponseEntity.getBody()).get("message").toString(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT);
      }
      smsProxyService.sendSMSText(receiverBlunt.getMobile(), smsContext);
    });
  }

  private void signUpInvite(String mobile, String inviterId) {
    Optional<Blunt> inviterBlunt = bluntRepository.findById(new ObjectId(inviterId));
    inviterBlunt.ifPresent(iBlunt -> {
      String link = "https://localhost:3000/blunt/signup";
      String smsContext = prepareSignUpSmsContext(link, iBlunt);
      log.info("smsContext"+smsContext);
      ResponseEntity<Object> followResponseEntity = followServiceProxyClient
          .followBlunt(frameSignUpFollow(iBlunt, mobile));
      if (followResponseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
        throw new BluntException(
            ((LinkedHashMap) followResponseEntity.getBody()).get("message").toString(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT);
      }
      smsProxyService.sendSMSText(mobile, smsContext);
    });
  }

  private String prepareSignUpSmsContext(String inviteLink, Blunt inviterBlunt) {
    String context = "Hi ,\n" + inviterBlunt.getFirstName() + " Invited to link:" + inviteLink;
    return context;
  }

  private String prepareOTPSmsContext(String otp) {
    String context = "OTP for Mobile validation is :" + otp;
    return context;
  }

  public ResponseEntity<Object> profileUpdate(BluntDto bluntDto) {
    Blunt blunt = bluntMapper.bluntDtoToBlunt(bluntDto);
    blunt = bluntRepository.save(blunt);
    return new ResponseEntity<>(bluntMapper.bluntToBluntDto(blunt), HttpStatus.OK);
  }

}
