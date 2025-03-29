package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Table(name = "country")
public class Country {
    @Id
    @Column(name = "iso2", length = 2, nullable = false, unique = true)
    private String iso2;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "time_zone")
    private String timeZone;
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SwiftCode> swiftCodes;
}
