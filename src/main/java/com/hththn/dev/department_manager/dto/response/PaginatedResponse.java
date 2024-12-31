package com.hththn.dev.department_manager.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse <T> {
    int totalPages;
    int pageSize;
    int curPage;
    int totalElements;
    List<T> result;
}
