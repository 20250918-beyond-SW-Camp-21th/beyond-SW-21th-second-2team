package com.whatthefork.resourcereservation.resource.dto.request.create;

import com.whatthefork.resourcereservation.resource.entity.CorporateCar;
import com.whatthefork.resourcereservation.resource.entity.Supplies;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateSuppliesRequest (

        @NotBlank(message = "추가할 비품의 이름을 입력하세요.")
        String name,

        @Min(value = 1, message = "비품은 1개 이상 등록 가능합니다.")
        int capacity
) {
    public CreateSuppliesRequest(Supplies supplies) {
        this(
                supplies.getName(),
                supplies.getCapacity()
        );
    }
}
