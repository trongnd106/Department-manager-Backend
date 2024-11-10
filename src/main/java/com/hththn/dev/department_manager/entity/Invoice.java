package com.hththn.dev.department_manager.entity;

import com.hththn.dev.department_manager.constant.FeeTypeEnum;
import com.hththn.dev.department_manager.service.SecurityUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    String id;
    String name;
    String description;

    //trong một số trường hợp, referencedColumnName không thực sự cần thiết. Theo mặc định, JPA sẽ tự động hiểu khóa ngoại trong @JoinColumn nếu cột đó trỏ đến cột chính của bảng được tham chiếu (primary key). Điều này có nghĩa là nếu cột khóa ngoại address_number ở bảng Invoice trỏ đến cột address_number là khóa chính (primary key) của bảng Apartment, bạn có thể bỏ referencedColumnName, và JPA sẽ tự động ánh xạ.
    @ManyToOne()
    @JoinColumn(name = "address_number")
    Apartment apartment;

    Instant createdAt;
    Instant updatedAt;

    String createdBy;
    String updatedBy;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.updatedAt = Instant.now();
    }

}
