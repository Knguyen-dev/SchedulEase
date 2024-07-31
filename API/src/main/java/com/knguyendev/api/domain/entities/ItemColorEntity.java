package com.knguyendev.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="ItemColor")
public class ItemColorEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", columnDefinition="VARCHAR(32) NOT NULL UNIQUE")
    private String name;

    @Column(name="hexCode", columnDefinition="CHAR(7) NOT NULL UNIQUE")
    private String hexCode;
}
