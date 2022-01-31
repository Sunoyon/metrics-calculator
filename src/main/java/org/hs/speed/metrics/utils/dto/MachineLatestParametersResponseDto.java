package org.hs.speed.metrics.utils.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MachineLatestParametersResponseDto {

    private String machineKey;
    private List<String> parameters;
}
