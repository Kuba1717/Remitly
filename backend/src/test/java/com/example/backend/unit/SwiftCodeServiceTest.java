package com.example.backend.unit;

import com.example.backend.dto.CountrySwiftCodesDto;
import com.example.backend.dto.SwiftCodeBranchesDto;
import com.example.backend.dto.SwiftCodeDto;
import com.example.backend.mapper.SwiftCodeMapper;
import com.example.backend.model.Country;
import com.example.backend.model.SwiftCode;
import com.example.backend.repository.CountryRepository;
import com.example.backend.repository.SwiftCodeRepository;
import com.example.backend.service.SwiftCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private SwiftCodeMapper swiftCodeMapper;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSwiftCodeResponse_NotFound() {
        String code = "ABCDEFXX";
        when(swiftCodeRepository.findById(code)).thenReturn(Optional.empty());
        Optional<Object> result = swiftCodeService.getSwiftCodeResponse(code);
        assertFalse(result.isPresent());
        verify(swiftCodeRepository, times(1)).findById(code);
    }

    @Test
    void testGetSwiftCodeResponse_HeadquarterWithBranches() {
        String code = "ABCDEFGHXXX";
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(code);
        when(swiftCodeRepository.findById(code)).thenReturn(Optional.of(swiftCode));
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode(code);
        dto.setIsHeadquarter(true);
        dto.setBankName("Bank HQ");
        dto.setAddress("Address HQ");
        dto.setCountryISO2("US");
        dto.setCountryName("USA");

        when(swiftCodeMapper.toDto(swiftCode)).thenReturn(dto);
        SwiftCode branch = new SwiftCode();
        branch.setSwiftCode("ABCDEFGH001");
        SwiftCodeDto branchDto = new SwiftCodeDto();
        branchDto.setSwiftCode("ABCDEFGH001");
        branchDto.setIsHeadquarter(false);
        when(swiftCodeMapper.toDto(branch)).thenReturn(branchDto);
        when(swiftCodeRepository.findByBranchOf(code)).thenReturn(List.of(branch));
        Optional<Object> result = swiftCodeService.getSwiftCodeResponse(code);
        assertTrue(result.isPresent());
        assertInstanceOf(SwiftCodeBranchesDto.class, result.get());
        SwiftCodeBranchesDto responseDto = (SwiftCodeBranchesDto) result.get();

        assertEquals(code, responseDto.getSwiftCode());
        assertEquals("Bank HQ", responseDto.getBankName());
        assertEquals("Address HQ", responseDto.getAddress());
        assertTrue(responseDto.getIsHeadquarter());
        assertEquals("US", responseDto.getCountryISO2());
        assertEquals("USA", responseDto.getCountryName());
        assertEquals(1, responseDto.getBranches().size());
        assertEquals("ABCDEFGH001", responseDto.getBranches().get(0).getSwiftCode());
    }

    @Test
    void testGetSwiftCodeResponse_NonHeadquarter() {
        String code = "ABCDEFGHXYZ";
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(code);

        when(swiftCodeRepository.findById(code)).thenReturn(Optional.of(swiftCode));
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode(code);
        dto.setIsHeadquarter(false);

        when(swiftCodeMapper.toDto(swiftCode)).thenReturn(dto);
        Optional<Object> result = swiftCodeService.getSwiftCodeResponse(code);
        assertTrue(result.isPresent());
        assertInstanceOf(SwiftCodeDto.class, result.get());
        assertEquals(code, ((SwiftCodeDto) result.get()).getSwiftCode());
    }

    @Test
    void testGetSwiftCodesByCountryResponse_Empty() {
        String iso2 = "PL";
        when(swiftCodeRepository.findByCountry_Iso2(iso2)).thenReturn(Collections.emptyList());
        Optional<CountrySwiftCodesDto> result = swiftCodeService.getSwiftCodesByCountryResponse(iso2);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetSwiftCodesByCountryResponse_Found() {
        String iso2 = "US";
        Country country = new Country();
        country.setIso2(iso2);
        country.setName("USA");
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGHXXX");
        swiftCode.setCountry(country);
        when(swiftCodeRepository.findByCountry_Iso2(iso2)).thenReturn(List.of(swiftCode));
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGHXXX");
        when(swiftCodeMapper.toDto(swiftCode)).thenReturn(dto);
        Optional<CountrySwiftCodesDto> result = swiftCodeService.getSwiftCodesByCountryResponse(iso2);
        assertTrue(result.isPresent());
        CountrySwiftCodesDto response = result.get();
        assertEquals(iso2, response.getCountryISO2());
        assertEquals("USA", response.getCountryName());
        assertEquals(1, response.getSwiftCodes().size());
        assertEquals("ABCDEFGHXXX", response.getSwiftCodes().get(0).getSwiftCode());
    }

    @Test
    void testAddSwiftCode_Headquarter_NewCountry() {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGHXXX");
        dto.setCountryISO2("US");
        dto.setCountryName("USA");
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGHXXX");
        swiftCode.setCountry(country);

        when(swiftCodeMapper.toEntity(dto)).thenReturn(swiftCode);
        when(countryRepository.existsById("US")).thenReturn(false);
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(swiftCode);
        SwiftCode result = swiftCodeService.addSwiftCode(dto);
        assertTrue(result.getSwiftCode().endsWith("XXX"));
        assertTrue(result.getIsHeadquarter());
        assertNull(result.getBranchOf());
        verify(countryRepository, times(1)).save(country);
    }

    @Test
    void testAddSwiftCode_Headquarter_ExistingCountry() {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGHXXX");
        dto.setCountryISO2("US");
        dto.setCountryName("USA");
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGHXXX");
        swiftCode.setCountry(country);

        when(swiftCodeMapper.toEntity(dto)).thenReturn(swiftCode);
        when(countryRepository.existsById("US")).thenReturn(true);
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(swiftCode);
        SwiftCode result = swiftCodeService.addSwiftCode(dto);
        assertTrue(result.getSwiftCode().endsWith("XXX"));
        assertTrue(result.getIsHeadquarter());
        assertNull(result.getBranchOf());
        verify(countryRepository, never()).save(any());
    }

    @Test
    void testAddSwiftCode_Branch_WithExistingHeadquarter() {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGH123");
        dto.setCountryISO2("US");
        dto.setCountryName("USA");
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGH123");
        swiftCode.setCountry(country);

        when(swiftCodeMapper.toEntity(dto)).thenReturn(swiftCode);
        when(countryRepository.existsById("US")).thenReturn(true);
        SwiftCode headquarter = new SwiftCode();
        headquarter.setSwiftCode("ABCDEFGHXXX");
        when(swiftCodeRepository.findById("ABCDEFGHXXX")).thenReturn(Optional.of(headquarter));
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(swiftCode);
        SwiftCode result = swiftCodeService.addSwiftCode(dto);
        assertFalse(result.getIsHeadquarter());
        assertEquals("ABCDEFGHXXX", result.getBranchOf());
    }

    @Test
    void testAddSwiftCode_Branch_NoHeadquarterFound() {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGH123");
        dto.setCountryISO2("US");
        dto.setCountryName("USA");
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGH123");
        swiftCode.setCountry(country);

        when(swiftCodeMapper.toEntity(dto)).thenReturn(swiftCode);
        when(countryRepository.existsById("US")).thenReturn(true);
        when(swiftCodeRepository.findById("ABCDEFGHXXX")).thenReturn(Optional.empty());
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(swiftCode);
        SwiftCode result = swiftCodeService.addSwiftCode(dto);
        assertFalse(result.getIsHeadquarter());
        assertNull(result.getBranchOf());
    }

    @Test
    void testDeleteSwiftCode() {
        String code = "ABCDEFGHXXX";
        doNothing().when(swiftCodeRepository).deleteById(code);
        swiftCodeService.deleteSwiftCode(code);
        verify(swiftCodeRepository, times(1)).deleteById(code);
    }
}
