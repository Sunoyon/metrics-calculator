package org.hs.speed.metrics.models;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("machine")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    private String id;
    public static final String ATTR_ID = "id";

    private String key;
    public static final String ATTR_KEY = "key";

    private String name;
    public static final String ATTR_NAME = "name";
}
