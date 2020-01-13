package com.blunt.onboard.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@RequiredArgsConstructor
public class BluntUtil {

  public static String generateOTP(int length) {
    return RandomStringUtils.randomNumeric(length);
  }

  public static String generateUserId(int length) {
    return RandomStringUtils.randomAlphanumeric(length);
  }

}
