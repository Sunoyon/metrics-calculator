package org.hs.speed.metrics.services;

import static org.junit.Assert.assertEquals;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.repositories.MachineParametersRepository;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.Metrics;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.ParameterMetric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MachineParametersService.class)
public class MachineParametersServiceTest {

    @Mock
    private MachineParametersRepository machineParametersRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MachineParametersService machineParametersService;

    @Test
    public void calculateAvgAndAppendToMetricTest() throws Exception {
        List<Metrics> metrics = new ArrayList<Metrics>();
        MathMetrics mathMetrics = generateMathMetrics();

        WhiteboxImpl.invokeMethod(new MachineParametersService(), "calculateAvgAndAppendToMetric",
                mathMetrics.getParameters(), metrics);
        assertEquals(mathMetrics.getAvg(), metrics.get(0).getValue(), .0009);
    }

    @Test
    public void calculateMinAndAppendToMetricTest() throws Exception {
        List<Metrics> metrics = new ArrayList<Metrics>();
        MathMetrics mathMetrics = generateMathMetrics();

        WhiteboxImpl.invokeMethod(new MachineParametersService(), "calculateMinAndAppendToMetric",
                mathMetrics.getParameters(), metrics);
        assertEquals(mathMetrics.getMin(), metrics.get(0).getValue(), .0009);
    }

    @Test
    public void calculateMaxAndAppendToMetricTest() throws Exception {
        List<Metrics> metrics = new ArrayList<Metrics>();
        MathMetrics mathMetrics = generateMathMetrics();

        WhiteboxImpl.invokeMethod(new MachineParametersService(), "calculateMaxAndAppendToMetric",
                mathMetrics.getParameters(), metrics);
        assertEquals(mathMetrics.getMax(), metrics.get(0).getValue(), .0009);
    }

    @Test
    public void calculateMedianAndAppendToMetricTest() throws Exception {
        List<Metrics> metrics = new ArrayList<Metrics>();
        MathMetrics mathMetrics = generateMathMetrics();

        WhiteboxImpl.invokeMethod(new MachineParametersService(),
                "calculateMedianAndAppendToMetric",
                mathMetrics.getParameters(), metrics);
        assertEquals(mathMetrics.getMedian(), metrics.get(0).getValue(), .0009);
    }

    @Test
    public void calculateMetricsTest() {
        String machineKey = "embosser";
        List<MachineParameters> machineParameters = Arrays.asList(
                MachineParameters.builder().machineKey(machineKey)
                        .parameters(Map.of("core_diameter", 3.0)).build(),
                MachineParameters.builder().machineKey(machineKey)
                        .parameters(Map.of("speed", 20.0)).build(),
                MachineParameters.builder().machineKey(machineKey)
                        .parameters(Map.of("core_diameter", 1.0, "speed", 20.0)).build());
        
        MachineMetricsResponseDto metrics =
                machineParametersService.calculateMetrics(machineKey, machineParameters);
        assertEquals(machineKey, metrics.getMachineKey());

        ParameterMetric coreDiameter = metrics.getParameterMetrics().stream()
                .filter(m -> "core_diameter".equals(m.getParameter())).findFirst().get();
        assertEquals(2.0, coreDiameter.getMetrics().stream()
                .filter(m -> "median".equals(m.getMetric())).findFirst().get().getValue(), 0.0005);
        assertEquals(2.0, coreDiameter.getMetrics().stream()
                .filter(m -> "avg".equals(m.getMetric())).findFirst().get().getValue(), 0.0005);
    }

    private MathMetrics generateMathMetrics() {
        double v1 = 3.0, v2 = 6.0, v3 = 1.0, v4 = 11.0, v5 = 3.0;
        double[] doubles = new double[] {v1, v2, v3, v4, v5};

        double medianVal = new Median().evaluate(doubles);
        double meanVal = new Mean().evaluate(doubles);
        double minVal = new Min().evaluate(doubles);
        double maxVal = new Max().evaluate(doubles);

        List<Entry<String, Double>> entries = Arrays.asList(
                new SimpleEntry<String, Double>("core_diameter", v1),
                new SimpleEntry<String, Double>("core_diameter", v2),
                new SimpleEntry<String, Double>("core_diameter", v3),
                new SimpleEntry<String, Double>("core_diameter", v4),
                new SimpleEntry<String, Double>("core_diameter", v5)
                );
                return MathMetrics.builder().min(minVal).max(maxVal).avg(meanVal).median(medianVal)
                        .parameters(entries).build();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class MathMetrics {
        private Double median;
        private Double min;
        private Double max;
        private Double avg;

        private List<Entry<String, Double>> parameters;
    }
}
