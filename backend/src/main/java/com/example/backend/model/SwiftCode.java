package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "swift_codes")
public class SwiftCode {
    @Id
    @Column(name = "swift_code", nullable = false, unique = true)
    private String swiftCode;
    @Column(name = "bank_name", nullable = false)
    private String bankName;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "is_headquarter", nullable = false)
    private Boolean isHeadquarter;
    @Column(name = "branch_of")
    private String branchOf;
    @Column(name = "code_type")
    private String codeType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_iso2", referencedColumnName = "iso2", nullable = false)
    private Country country;
}
