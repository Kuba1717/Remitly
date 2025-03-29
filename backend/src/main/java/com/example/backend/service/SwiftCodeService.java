package com.example.backend.service;

import com.example.backend.dto.CountrySwiftCodesDto;
import com.example.backend.dto.SwiftCodeDto;
import com.example.backend.dto.SwiftCodeBranchesDto;
import com.example.backend.mapper.SwiftCodeMapper;
import com.example.backend.model.Country;
import com.example.backend.model.SwiftCode;
import com.example.backend.repository.CountryRepository;
import com.example.backend.repository.SwiftCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SwiftCodeService {
    @Autowired
    private SwiftCodeRepository swiftCodeRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private SwiftCodeMapper swiftCodeMapper;

    public Optional<Object> getSwiftCodeResponse(String swiftCode) {
        Optional<SwiftCode> swiftCodeOpt = swiftCodeRepository.findById(swiftCode);
        if (swiftCodeOpt.isEmpty()) {
            return Optional.empty();
        }
        SwiftCode entity = swiftCodeOpt.get();
        SwiftCodeDto dto = swiftCodeMapper.toDto(entity);

        if (Boolean.TRUE.equals(dto.getIsHeadquarter())) {
            List<SwiftCode> branchEntities = swiftCodeRepository.findByBranchOf(entity.getSwiftCode());
            List<SwiftCodeDto> branchDtos = new ArrayList<>();
            for (SwiftCode branch : branchEntities) {
                branchDtos.add(swiftCodeMapper.toDto(branch));
            }
            SwiftCodeBranchesDto responseDto = new SwiftCodeBranchesDto();
            responseDto.setSwiftCode(dto.getSwiftCode());
            responseDto.setBankName(dto.getBankName());
            responseDto.setAddress(dto.getAddress());
            responseDto.setIsHeadquarter(dto.getIsHeadquarter());
            responseDto.setCountryISO2(dto.getCountryISO2());
            responseDto.setCountryName(dto.getCountryName());
            responseDto.setBranches(branchDtos);
            return Optional.of(responseDto);
        } else {
            return Optional.of(dto);
        }
    }

    public Optional<CountrySwiftCodesDto> getSwiftCodesByCountryResponse(String iso2) {
        List<SwiftCode> codes = swiftCodeRepository.findByCountry_Iso2(iso2);
        if (codes == null || codes.isEmpty()) {
            return Optional.empty();
        }
        List<SwiftCodeDto> swiftCodeDtos = new ArrayList<>();
        for (SwiftCode code : codes) {
            swiftCodeDtos.add(swiftCodeMapper.toDto(code));
        }
        CountrySwiftCodesDto response = new CountrySwiftCodesDto();
        Country firstCountry = codes.get(0).getCountry();
        response.setCountryISO2(firstCountry.getIso2());
        response.setCountryName(firstCountry.getName());
        response.setSwiftCodes(swiftCodeDtos);
        return Optional.of(response);
    }

    public SwiftCode addSwiftCode(SwiftCodeDto swiftCodeDto) {
        SwiftCode swiftCode = swiftCodeMapper.toEntity(swiftCodeDto);
        saveCountryIfNotExists(swiftCode.getCountry());

        String code = swiftCode.getSwiftCode();
        if (code.endsWith("XXX")) {
            swiftCode.setIsHeadquarter(true);
            swiftCode.setBranchOf(null);
        } else {
            swiftCode.setIsHeadquarter(false);
            if (code.length() >= 8) {
                String expectedHQ = code.substring(0, 8) + "XXX";
                Optional<SwiftCode> headquarterOpt = swiftCodeRepository.findById(expectedHQ);
                headquarterOpt.ifPresent(hq -> swiftCode.setBranchOf(expectedHQ));
            }
        }
        return swiftCodeRepository.save(swiftCode);
    }

    public void deleteSwiftCode(String swiftCode) {
        swiftCodeRepository.deleteById(swiftCode);
    }

    private void saveCountryIfNotExists(Country country) {
        if (country != null && !countryRepository.existsById(country.getIso2())) {
            countryRepository.save(country);
        }
    }
}
