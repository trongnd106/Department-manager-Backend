package com.hththn.dev.department_manager.entity;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.hththn.dev.department_manager.constant.GenderEnum;
import com.hththn.dev.department_manager.constant.ResidentEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "residents")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resident {
    @Id
    Long id;

    String name;

    LocalDate dob;

    GenderEnum gender;

    String cic;

    @ManyToOne()
    @JoinColumn(name = "addressNumber")
    @JsonIgnore
    Apartment apartment;
    @Enumerated(EnumType.STRING)
    ResidentEnum status;
    LocalDate statusDate;

    Instant createdAt;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
        this.statusDate = LocalDate.now();
    }

    @Transient  // field used to compare with status, not saved into database
    ResidentEnum previousStatus;

    @Transient
    Long apartmentId;

    @PostLoad
    public void onLoad() {
        this.previousStatus = this.status;
        this.apartmentId = apartment != null ? apartment.getAddressNumber() : null;
    }

    @PreUpdate
    public void beforeUpdate() {
        if (!status.equals(previousStatus)) {  // if status changed
            this.statusDate = LocalDate.now();  // update statusDate
        }
        this.previousStatus = this.status;
    }

}
