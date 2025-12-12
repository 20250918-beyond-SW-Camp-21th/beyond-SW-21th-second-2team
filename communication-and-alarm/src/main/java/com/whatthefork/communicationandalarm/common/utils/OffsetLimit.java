package com.whatthefork.communicationandalarm.common.utils;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
public class OffsetLimit {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public OffsetLimit(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    public OffsetLimit(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public Pageable toPageable() {
        int page = offset / limit;
        return PageRequest.of(page, limit, sort);
    }
}
