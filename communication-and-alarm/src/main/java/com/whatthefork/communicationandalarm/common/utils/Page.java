package com.whatthefork.communicationandalarm.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page<T> {
    private List<T> contents;
    private Boolean hasNext;
}
