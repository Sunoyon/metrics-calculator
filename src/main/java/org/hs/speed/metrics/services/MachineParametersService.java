package org.hs.speed.metrics.services;

import java.util.Date;
import java.util.UUID;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.repositories.MachineParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MachineParametersService {

    @Autowired
    private MachineParametersRepository machineParametersRepository;

    public MachineParameters save(MachineParameters machineParameters) {
        machineParameters.setId(UUID.randomUUID().toString());
        machineParameters.setCreatedAt(new Date());
        return machineParametersRepository.save(machineParameters);
    }
}
