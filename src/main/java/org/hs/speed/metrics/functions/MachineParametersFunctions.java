package org.hs.speed.metrics.functions;

import java.util.function.Function;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.services.MachineParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MachineParametersFunctions {

    @Autowired
    private MachineParametersService machineParametersService;

    @Bean("saveMachineParameters")
    public Function<MachineParameters, MachineParameters> save() {
        return machineParameter -> machineParametersService.save(machineParameter);
    }
}
