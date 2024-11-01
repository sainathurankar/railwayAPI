package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.SearchInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class Controller {
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok().body(Map.of("status", "UP"));
    }

    @RequestMapping(value = "/status", method = RequestMethod.HEAD)
    public ResponseEntity<?> getStatusMonitor() {
        return ResponseEntity.ok().body(Map.of("status", "UP"));
    }
}
