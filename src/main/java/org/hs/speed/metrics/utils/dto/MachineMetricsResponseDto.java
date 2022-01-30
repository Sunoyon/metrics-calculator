package org.hs.speed.metrics.utils.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MachineMetricsResponseDto {

    private String machineKey;
    private List<ParameterMetric> parameterMetrics;

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ParameterMetric {
        private String parameter;
        private List<Metrics> metrics;
    }

    @Getter
    @Setter
    @Builder
    public static class Metrics {
        private String metric;
        private Double value;
    }
}
