package com.blunt.onboard.dto;

import com.blunt.onboard.serializer.ObjectIdSerializer;
import com.blunt.onboard.type.Status;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class FollowDto {

  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId bluntId;
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId followerId;
  private String bluntName;
  private String bluntNickName;
  private String followerName;
  private String followerUserId;
  private Status status;
  private String mobile;
}
