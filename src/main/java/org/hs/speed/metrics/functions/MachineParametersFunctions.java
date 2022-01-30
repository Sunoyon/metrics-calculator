package org.hs.speed.metrics.functions;

import java.util.List;
import java.util.function.Function;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.services.MachineParametersService;
import org.hs.speed.metrics.utils.dto.MachineMetricsRequestDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MachineParametersFunctions {

    @Autowired
    private MachineParametersService machineParametersService;

    @Bean("save-machine-parameters")
    public Function<MachineParameters, MachineParameters> save() {
        return machineParameter -> machineParametersService.save(machineParameter);
    }

    @Bean("machine-metrics")
    public Function<MachineMetricsRequestDto, MachineMetricsResponseDto> metrics() {
        return value -> {
            List<MachineParameters> machineParameters = machineParametersService
                    .findMachineKeyOfPastNMinutes(value.getMachineKey(),
                    value.getMinutesFrom());
            return machineParametersService.calculateMetrics(value.getMachineKey(),
                    machineParameters);
        };
    }
}
