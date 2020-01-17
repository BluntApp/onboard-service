package com.blunt.onboard.dto;

import java.util.List;
import lombok.Data;

@Data
public class InviteDto {
  private FollowDto followDto;
  private List<String> mobileList;
}
