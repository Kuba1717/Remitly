package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodeBranchesDto {
    private String swiftCode;
    private String bankName;
    private String address;
    private Boolean isHeadquarter;
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDto> branches;
}
