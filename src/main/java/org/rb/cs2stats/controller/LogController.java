package org.rb.cs2stats.controller;

import org.rb.cs2stats.service.LogParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/cs2")
public class LogController {

    @Autowired
    private LogParserService logParserService;

    @GetMapping("/parse-log")
    public ResponseEntity<Map<String, Object>> parseLog() {
        try {
            Map<String, Object> parsedData = logParserService.parseLogFile("src/main/resources/cs2.log");
            return ResponseEntity.ok(parsedData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to parse log file."));
        }
    }
}
