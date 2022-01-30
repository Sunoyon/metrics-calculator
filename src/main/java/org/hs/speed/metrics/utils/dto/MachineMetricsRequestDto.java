package org.hs.speed.metrics.utils.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineMetricsRequestDto {

    private String machineKey;
    private Integer minutesFrom;
}
