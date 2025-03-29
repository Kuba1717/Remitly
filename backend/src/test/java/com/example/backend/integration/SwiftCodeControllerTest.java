package com.example.backend.integration;

import com.example.backend.dto.MessageDto;
import com.example.backend.dto.SwiftCodeDto;
import com.example.backend.model.Country;
import com.example.backend.model.SwiftCode;
import com.example.backend.repository.CountryRepository;
import com.example.backend.repository.SwiftCodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SwiftCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SwiftCodeRepository swiftCodeRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        swiftCodeRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void testAddAndGetSwiftCode_Headquarter() throws Exception {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setSwiftCode("ABCDEFGHXXX");
        dto.setBankName("Bank Testowy");
        dto.setAddress("Adres siedziby");
        dto.setIsHeadquarter(true);
        dto.setCountryISO2("PL");
        dto.setCountryName("Polska");

        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Swift code added successfully"));

        mockMvc.perform(get("/v1/swift-codes/ABCDEFGHXXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value("ABCDEFGHXXX"))
                .andExpect(jsonPath("$.branches").exists());
    }

    @Test
    void testAddAndGetSwiftCode_Branch() throws Exception {
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        countryRepository.save(country);

        SwiftCode headquarter = new SwiftCode();
        headquarter.setSwiftCode("ABCDEFGHXXX");
        headquarter.setBankName("HQ Bank");
        headquarter.setAddress("HQ Address");
        headquarter.setIsHeadquarter(true);
        headquarter.setCountry(country);
        swiftCodeRepository.save(headquarter);

        SwiftCodeDto branchDto = new SwiftCodeDto();
        branchDto.setSwiftCode("ABCDEFGH123");
        branchDto.setBankName("Branch Bank");
        branchDto.setAddress("Branch Address");
        branchDto.setIsHeadquarter(false);
        branchDto.setCountryISO2("US");
        branchDto.setCountryName("USA");

        String branchJson = objectMapper.writeValueAsString(branchDto);
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(branchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Swift code added successfully"));

        mockMvc.perform(get("/v1/swift-codes/ABCDEFGH123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value("ABCDEFGH123"));
    }

    @Test
    void testGetSwiftCodesByCountry_Found() throws Exception {
        Country country = new Country();
        country.setIso2("US");
        country.setName("USA");
        countryRepository.save(country);

        SwiftCode swiftCode1 = new SwiftCode();
        swiftCode1.setSwiftCode("CODE1XXX");
        swiftCode1.setBankName("Bank1");
        swiftCode1.setAddress("Address1");
        swiftCode1.setIsHeadquarter(true);
        swiftCode1.setCountry(country);
        SwiftCode swiftCode2 = new SwiftCode();
        swiftCode2.setSwiftCode("CODE2XXX");
        swiftCode2.setBankName("Bank2");
        swiftCode2.setAddress("Address2");
        swiftCode2.setIsHeadquarter(true);
        swiftCode2.setCountry(country);
        swiftCodeRepository.save(swiftCode1);
        swiftCodeRepository.save(swiftCode2);

        mockMvc.perform(get("/v1/swift-codes/country/US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value("US"))
                .andExpect(jsonPath("$.countryName").value("USA"))
                .andExpect(jsonPath("$.swiftCodes").isArray())
                .andExpect(jsonPath("$.swiftCodes.length()").value(2));
    }

    @Test
    void testGetSwiftCodesByCountry_NotFound() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/country/XX"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSwiftCode() throws Exception {
        Country country = new Country();
        country.setIso2("PL");
        country.setName("Polska");
        countryRepository.save(country);

        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode("ABCDEFGHXXX");
        swiftCode.setBankName("Bank Delete");
        swiftCode.setAddress("Address Delete");
        swiftCode.setIsHeadquarter(true);
        swiftCode.setCountry(country);
        swiftCodeRepository.save(swiftCode);

        mockMvc.perform(delete("/v1/swift-codes/ABCDEFGHXXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Swift code deleted successfully"));

        assertTrue(swiftCodeRepository.findById("ABCDEFGHXXX").isEmpty());
    }
}
