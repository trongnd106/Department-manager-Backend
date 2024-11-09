package com.hththn.dev.department_manager.constant;

public enum ResidentEnum {
    Resident, Temporary, Absent, Moved;
    public static ResidentEnum fromString(String status) {
        for (ResidentEnum value : ResidentEnum.values()) {
            if (value.name().equalsIgnoreCase(status)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}
