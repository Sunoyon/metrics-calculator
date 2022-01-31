package org.hs.speed.metrics.functions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.services.MachineParametersService;
import org.hs.speed.metrics.utils.dto.MachineLatestParametersResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsRequestDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.Metrics;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.ParameterMetric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@PrepareForTest(MachineParametersFunctions.class)
public class MachineParametersFunctionsTests {

    @Mock
    private MachineParametersService machineParametersService;

    @InjectMocks
    private MachineParametersFunctions machineParametersFunctions;

    @Test
    public void saveTest() {
        Map<String, Double> parameters = Map.of("core_diameter", 3.0, "speed", 20.0);
        String machineKey = "embosser";
        Date createdAt = new Date();

        MachineParameters request = MachineParameters.builder().machineKey(machineKey)
                .parameters(parameters).createdAt(createdAt).build();
        MachineParameters expected =
                MachineParameters.builder().id("d7ca5c5f-a6f1-410d-88cf-8d2d54aadede")
                .machineKey(machineKey).parameters(parameters).createdAt(createdAt).build();

        when(machineParametersService.save(Mockito.any())).thenReturn(expected);
        MachineParameters actual = machineParametersFunctions.save().apply(request);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getMachineKey(), actual.getMachineKey());
        assertEquals(expected.getParameters().get("core_diameter"), 3.0, 0.00005);
        assertEquals(expected.getParameters().get("speed"), 20.0, 0.00005);

    }

    @Test
    public void metricsTest() {
        String machineKey = "embosser";
        Double expectedCoreDiameterMin = 1.0;
        Double expectedCoreDiameterAvg = 3.0;
        List<Metrics> metrics = Arrays.asList(Metrics.builder().metric("avg").value(expectedCoreDiameterAvg).build(),
                Metrics.builder().metric("min").value(expectedCoreDiameterMin).build());
        ParameterMetric expectedCoreDiameter =
                ParameterMetric.builder().parameter("core_diameter").metrics(metrics).build();
        List<ParameterMetric> expectedParameterMetrics = Arrays.asList(expectedCoreDiameter);
        MachineMetricsResponseDto expected = MachineMetricsResponseDto.builder()
                .machineKey(machineKey)
                .parameterMetrics(expectedParameterMetrics).build();

        List<MachineParameters> machineParameters = Arrays.asList(
                MachineParameters.builder().machineKey(machineKey)
                        .parameters(Map.of("core_diameter", 3.0)).build(),
                MachineParameters.builder().machineKey(machineKey).parameters(Map.of("speed", 20.0))
                        .build(),
                MachineParameters.builder().machineKey(machineKey)
                        .parameters(Map.of("core_diameter", 1.0, "speed", 20.0)).build());


        when(machineParametersService.findMachineKeyOfPastNMinutes(Mockito.anyString(),
                Mockito.anyInt())).thenReturn(machineParameters);
        when(machineParametersService.calculateMetrics(Mockito.anyString(), Mockito.anyList()))
                .thenReturn(expected);

        MachineMetricsRequestDto request =
                MachineMetricsRequestDto.builder().machineKey(machineKey).minutesFrom(10).build();
        MachineMetricsResponseDto actual = machineParametersFunctions.metrics().apply(request);
        assertEquals(expected.getMachineKey(), actual.getMachineKey());
        
        ParameterMetric actualCoreDiameter = actual.getParameterMetrics().stream().filter(m -> "core_diameter".equals(m.getParameter())).findFirst().get();
        Double actualCoreDiameterMin = actualCoreDiameter.getMetrics().stream().filter(m -> "min".equals(m.getMetric())).findFirst().get().getValue();
        Double actualCoreDiameterAvg = actualCoreDiameter.getMetrics().stream().filter(m -> "avg".equals(m.getMetric())).findFirst().get().getValue();

        assertEquals(expectedCoreDiameter.getParameter(), actualCoreDiameter.getParameter());
        assertEquals(expectedCoreDiameterMin, actualCoreDiameterMin, 0.0005);
        assertEquals(expectedCoreDiameterAvg, actualCoreDiameterAvg, 0.0005);
    }

    @Test
    public void latestParametersTest() {

        List<String> expectedParameters = Arrays.asList("core_diameter", "speed");
        String expectedMachineKey = "embosser";
        MachineLatestParametersResponseDto expectedEmbosserParameters = MachineLatestParametersResponseDto.builder()
                .machineKey(expectedMachineKey).parameters(expectedParameters).build();
        
        List<MachineLatestParametersResponseDto> expected = Arrays.asList(expectedEmbosserParameters);

        when(machineParametersService.latestParameters()).thenReturn(expected);
        
        List<MachineLatestParametersResponseDto> actual = machineParametersFunctions.latestParameters().get();
        
        MachineLatestParametersResponseDto actualEmbosserParameters = actual.stream().filter(m -> expectedMachineKey.equals(m.getMachineKey())).findFirst().get();
        assertEquals(expectedMachineKey, actualEmbosserParameters.getMachineKey());
        assertEquals("core_diameter", expectedParameters.get(0));
        assertEquals("speed", expectedParameters.get(1));
    }
}

