package org.hs.speed.metrics.utils.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MachineMetricsRequestDto {

    private String machineKey;
    private Integer minutesFrom;
}
