package com.blunt.onboard.proxy;

import com.blunt.onboard.dto.FollowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "follow-service")
public interface FollowServiceProxyClient {

  @PostMapping(path="/api/v1/follow")
  public ResponseEntity<Object> followBlunt(@RequestBody FollowDto followDto);

}
