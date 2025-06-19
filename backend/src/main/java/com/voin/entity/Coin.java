package com.voin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coins")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "color", length = 7)
    private String color;
} 