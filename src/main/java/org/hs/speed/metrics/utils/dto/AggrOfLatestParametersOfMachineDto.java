package org.hs.speed.metrics.utils.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggrOfLatestParametersOfMachineDto {

    private String id;
    private Map<String, Double> parameters = new HashMap<String, Double>();

}
