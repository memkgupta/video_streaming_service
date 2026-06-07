package com.vsnt.ai_service.controllers;

import com.vsnt.ai_service.dtos.QueryRequestDTO;
import com.vsnt.ai_service.services.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/query")
public class QueryController {
    private final QueryService queryService;
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> query(@RequestBody QueryRequestDTO query)
    {
        String res = queryService.query(query.query() , query.mediaId());
        return ResponseEntity.ok(Map.of("response",res));
    }
}
