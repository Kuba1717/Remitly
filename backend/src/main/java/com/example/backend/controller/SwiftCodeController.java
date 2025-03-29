package com.example.backend.controller;

import com.example.backend.dto.CountrySwiftCodesDto;
import com.example.backend.dto.MessageDto;
import com.example.backend.dto.SwiftCodeDto;
import com.example.backend.dto.SwiftCodeBranchesDto;
import com.example.backend.service.SwiftCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @GetMapping("/{swiftCode}")
    public ResponseEntity<?> getSwiftCode(@PathVariable("swiftCode") String swiftCode) {
        Optional<Object> response = swiftCodeService.getSwiftCodeResponse(swiftCode);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable("countryISO2") String countryISO2) {
        Optional<CountrySwiftCodesDto> response = swiftCodeService.getSwiftCodesByCountryResponse(countryISO2);
        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<MessageDto> addSwiftCode(@RequestBody SwiftCodeDto swiftCodeDto) {
        swiftCodeService.addSwiftCode(swiftCodeDto);
        return ResponseEntity.ok(new MessageDto("Swift code added successfully"));
    }
    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<MessageDto> deleteSwiftCode(@PathVariable("swiftCode") String swiftCode) {
        swiftCodeService.deleteSwiftCode(swiftCode);
        return ResponseEntity.ok(new MessageDto("Swift code deleted successfully"));
    }
}
