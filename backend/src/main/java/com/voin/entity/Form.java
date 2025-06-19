package com.voin.entity;

import com.voin.constant.FormType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FormType type;
} 