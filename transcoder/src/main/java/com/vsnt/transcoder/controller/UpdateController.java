package com.vsnt.transcoder.controller;

import com.vsnt.transcoder.config.KafkaProducer;
import com.vsnt.transcoder.dtos.UpdateRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UpdateController {
    private final KafkaProducer kafkaProducer;

    public UpdateController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/update")
    public String updateUpload(@RequestBody UpdateRequestDTO dto)
    {
        kafkaProducer.produce(dto);
        return "success";
    }
}
