package com.hththn.dev.department_manager.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResponse <T> {
    int totalPages;
    int pageSize;
    int curPage;
    int totalElements;
    List<T> result;
}
