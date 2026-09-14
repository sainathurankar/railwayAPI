package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.service.RisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Rail information services: PNR status, live running status, train schedule
 * and coach position. Backed by the redBus RIS host via {@link RisService}.
 */
@RestController
@CrossOrigin
@RequestMapping("/ris")
public class RisController {

    @Autowired
    private RisService risService;

    private final Logger logger = LoggerFactory.getLogger(RisController.class);

    @Operation(summary = "PNR status", description = "Returns PNR status with passenger and journey details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PNR status (or upstream error body for an invalid PNR)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/pnr/{pnr}")
    public ResponseEntity<?> pnrStatus(
            @Parameter(description = "10-digit PNR number", example = "1234567890")
            @PathVariable("pnr") String pnr,
            @Parameter(description = "Mobile number linked to the PNR (optional)")
            @RequestParam(value = "mobile", required = false) String mobile) {
        return handle("pnrStatus", () -> risService.getPnrStatus(pnr, mobile));
    }

    @Operation(summary = "Train schedule", description = "Full station-by-station schedule for a train.")
    @GetMapping("/schedule/{trainNo}")
    public ResponseEntity<?> trainSchedule(
            @Parameter(description = "5-digit train number", example = "12951")
            @PathVariable("trainNo") String trainNo) {
        return handle("trainSchedule", () -> risService.getTrainSchedule(trainNo));
    }

    @Operation(summary = "Live train status", description = "Current position, delays and per-station timeline.")
    @GetMapping("/live/{trainNo}")
    public ResponseEntity<?> liveStatus(
            @Parameter(description = "5-digit train number", example = "12951")
            @PathVariable("trainNo") String trainNo) {
        return handle("liveStatus", () -> risService.getLiveTrainStatus(trainNo));
    }

    @Operation(summary = "Coach position", description = "Rake / coach layout for a train at a station.")
    @GetMapping("/coach/{trainNo}")
    public ResponseEntity<?> coachPosition(
            @Parameter(description = "5-digit train number", example = "12951")
            @PathVariable("trainNo") String trainNo,
            @Parameter(description = "Station code", example = "NDLS")
            @RequestParam("stn") String stn) {
        return handle("coachPosition", () -> risService.getCoachPosition(trainNo, stn));
    }

    private ResponseEntity<?> handle(String name, java.util.function.Supplier<Map<String, Object>> op) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Inside '/ris' {}()", name);
            return ResponseEntity.ok(op.get());
        } catch (Exception e) {
            logger.error("Exception caught in {}():", name, e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            logger.info("{}() Executed in {}ms", name, System.currentTimeMillis() - startTime);
        }
    }
}
