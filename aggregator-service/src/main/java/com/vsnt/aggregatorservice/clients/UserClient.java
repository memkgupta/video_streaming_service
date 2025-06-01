package com.vsnt.aggregatorservice.clients;

import com.vsnt.aggregatorservice.config.FeignConfig;
import com.vsnt.aggregatorservice.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user" , configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/all")
    List<UserDTO> getAllUser(@RequestParam("ids") List<String> ids);
}
