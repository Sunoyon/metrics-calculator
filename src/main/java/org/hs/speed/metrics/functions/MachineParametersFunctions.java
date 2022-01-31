package org.hs.speed.metrics.functions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.services.MachineParametersService;
import org.hs.speed.metrics.utils.dto.MachineLatestParametersResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsRequestDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MachineParametersFunctions {

    @Autowired
    private MachineParametersService machineParametersService;

    /**
     * Store MachineParameter object in database
     * 
     * @return returns MachineParameters object
     */
    @Bean("machines")
    public Function<MachineParameters, MachineParameters> save() {
        return machineParameter -> machineParametersService.save(machineParameter);
    }

    /**
     * Calculate the metrics of parameters of a machine within past n minutes
     * 
     * @return returns MachineMetricsResponseDto containing metrics
     */
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

    /**
     * Latest parameters of all machines
     * 
     * @return returns latest machine parameters
     */
    @Bean("machine-latest-parameters")
    public Supplier<List<MachineLatestParametersResponseDto>> latestParameters() {
        return () -> machineParametersService.latestParameters();
    }
}
