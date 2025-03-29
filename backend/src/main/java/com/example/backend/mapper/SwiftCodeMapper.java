package com.example.backend.mapper;

import com.example.backend.dto.SwiftCodeDto;
import com.example.backend.model.Country;
import com.example.backend.model.SwiftCode;
import org.springframework.stereotype.Component;


@Component
public class SwiftCodeMapper {

    public SwiftCodeDto toDto(SwiftCode swiftCode) {
        if (swiftCode == null) {
            return null;
        }
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode(swiftCode.getSwiftCode());
        dto.setBankName(swiftCode.getBankName());
        dto.setAddress(swiftCode.getAddress());
        dto.setIsHeadquarter(swiftCode.getIsHeadquarter());
        if (swiftCode.getCountry() != null) {
            dto.setCountryISO2(swiftCode.getCountry().getIso2());
            dto.setCountryName(swiftCode.getCountry().getName());
        }
        return dto;
    }

    public SwiftCode toEntity(SwiftCodeDto dto) {
        if (dto == null) {
            return null;
        }
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(dto.getSwiftCode());
        swiftCode.setBankName(dto.getBankName());
        swiftCode.setAddress(dto.getAddress());
        swiftCode.setIsHeadquarter(dto.getIsHeadquarter());
        swiftCode.setCountry(createCountry(dto.getCountryISO2(), dto.getCountryName()));
        return swiftCode;
    }

    public Country createCountry(String iso2, String name) {
        if (iso2 == null || name == null) {
            return null;
        }
        Country country = new Country();
        country.setIso2(iso2);
        country.setName(name);
        return country;
    }
}
