package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountrySwiftCodesDto {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDto> swiftCodes;
}
