package com.example.backend.data;

import com.example.backend.model.Country;
import com.example.backend.model.SwiftCode;
import com.example.backend.repository.CountryRepository;
import com.example.backend.repository.SwiftCodeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SwiftCodeRepository swiftCodeRepository;
    private final CountryRepository countryRepository;

    private static final String CSV_URL = "https://docs.google.com/spreadsheets/d/1iFFqsu_xruvVKzXAadAAlDBpIuU51v-pfIEU5HeGa8w/export?format=csv&id=1iFFqsu_xruvVKzXAadAAlDBpIuU51v-pfIEU5HeGa8w&gid=0";

    @Override
    public void run(String... args) throws Exception {
        try (InputStream inputStream = new URL(CSV_URL).openStream();
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                String countryIso2 = record.get("COUNTRY ISO2 CODE").trim();
                String swiftCodeStr = record.get("SWIFT CODE").trim();
                String bankName = record.get("NAME").trim();
                String address = record.get("ADDRESS").trim();
                String countryName = record.get("COUNTRY NAME").trim();

                Country country = countryRepository.findById(countryIso2)
                        .orElseGet(() -> {
                            Country newCountry = new Country();
                            newCountry.setIso2(countryIso2);
                            newCountry.setName(countryName);
                            return newCountry;
                        });
                countryRepository.save(country);

                SwiftCode swiftCode = new SwiftCode();
                swiftCode.setSwiftCode(swiftCodeStr);
                swiftCode.setBankName(bankName);
                swiftCode.setAddress(address);
                swiftCode.setCountry(country);

                if (swiftCodeStr.endsWith("XXX")) {
                    swiftCode.setIsHeadquarter(true);
                    swiftCode.setBranchOf(null);
                } else {
                    swiftCode.setIsHeadquarter(false);
                    if (swiftCodeStr.length() >= 8) {
                        String expectedHQ = swiftCodeStr.substring(0, 8) + "XXX";
                        Optional<SwiftCode> hqOpt = swiftCodeRepository.findById(expectedHQ);
                        hqOpt.ifPresent(hq -> swiftCode.setBranchOf(expectedHQ));
                    }
                }
                swiftCodeRepository.save(swiftCode);
            }
        }
    }
}
