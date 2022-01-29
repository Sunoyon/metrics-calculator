package org.hs.speed.metrics.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("machineParameters")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MachineParameters {

    private String id;
    public static final String ATTR_ID = "id";

    private String machineKey;
    public static final String ATTR_MACHINE_KEY = "machineKey";

    private Date createdAt;
    public static final String ATTR_CREATED_AT = "createdAt";

    private Map<String, Double> parameters = new HashMap<String, Double>();
    public static final String ATTR_PARAMETERS = "parameteres";
}
