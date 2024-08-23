package org.project.currencyexchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.exception.ExchangeErrorResponse;
import org.project.currencyexchange.service.CacheService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Cache Service")
public class CacheController {

    private final CacheService cacheService;

    @Operation(summary = "Evict cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request successful"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class)))
    })
    @GetMapping("/cache/evict")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAllCaches(){
        cacheService.evictAllCache();
    }

}
