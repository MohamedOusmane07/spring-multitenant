package net.mohamed.springmultitenant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(schema = "main")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private double amount;
    private String currency;
    private String status;
}
