package com.JsonDiffApi.controller;

import com.JsonDiffApi.dto.JsonRequest;
import com.JsonDiffApi.service.HtmlService;
import com.JsonDiffApi.service.JsonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/json")
public class JsonController {
    private final JsonService jsonService;
    private final HtmlService htmlService;

    public JsonController(JsonService jsonService, HtmlService htmlService) {
        this.jsonService = jsonService;
        this.htmlService = htmlService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveJson(@RequestBody JsonRequest request) {
        jsonService.saveJson(request.getId(), request.getJsonData());
        return ResponseEntity.ok("JSON kaydedildi.");
    }

    @PostMapping("/compare")
    public ResponseEntity<?> compareJson(@RequestBody JsonRequest request) {
        try {
            List<Map<String, Object>> diff = jsonService.compareJson(request.getId(), request.getJsonData());
            return ResponseEntity.ok(diff);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/compare/html")
    public ResponseEntity<?> compareJsonAsHtml(@RequestBody JsonRequest request) {
        try {
            List<Map<String, Object>> diff = jsonService.compareJson(request.getId(), request.getJsonData());
            String originalJson = jsonService.getOriginalJsonById(request.getId());
            String incomingJson = request.getJsonData();
            String html = htmlService.generateHtml(originalJson, incomingJson, diff);
            return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}