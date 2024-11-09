package com.hththn.dev.department_manager.entity;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.hththn.dev.department_manager.constant.ResidentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "residents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resident {
    @Id
    Long id;

    String name;

    LocalDate dob;
    @Enumerated(EnumType.STRING)
    ResidentEnum status;
    LocalDate statusDate;
    String addressNumber;

    Instant createdAt;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
        this.statusDate = LocalDate.now();
    }

    @Transient  // field used to compare with status, not saved into database
    ResidentEnum previousStatus;

    @PostLoad
    public void onLoad() {
        this.previousStatus = this.status;
    }

    @PreUpdate
    public void beforeUpdate() {
        if (!status.equals(previousStatus)) {  // if status changed
            this.statusDate = LocalDate.now();  // update statusDate
        }
        this.previousStatus = this.status;
    }

}
