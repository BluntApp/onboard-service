package com.blunt.onboard.proxy;

import com.blunt.onboard.dto.FollowDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.NestedServletException;

@FeignClient(name = "follow-service")
public interface FollowServiceProxyClient {

  @PostMapping(path="/api/v1/follow")
  public ResponseEntity<Object> followBlunt(@RequestBody FollowDto followDto);

  @PutMapping(path="/api/v1/follow")
  public ResponseEntity<Object> updateFollowBlunt(@RequestBody List<FollowDto> followDtoList);

  @GetMapping("/api/v1/follow/followers/{mobile}")
  public ResponseEntity<Object> fetchInactiveFollowers(@PathVariable String mobile);

}
