package com.example.backend.repository;

import com.example.backend.model.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    List<SwiftCode> findByCountry_Iso2(String iso2);
    List<SwiftCode> findByBranchOf(String headquarterSwiftCode);
}
