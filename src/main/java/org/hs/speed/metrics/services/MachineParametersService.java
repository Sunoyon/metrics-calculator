package org.hs.speed.metrics.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.apache.commons.lang3.time.DateUtils;
import org.hs.speed.metrics.models.MachineParameters;
import org.hs.speed.metrics.repositories.MachineParametersRepository;
import org.hs.speed.metrics.utils.Constants;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.Metrics;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.ParameterMetric;
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

    public List<MachineParameters> findMachineKeyOfPastNMinutes(String machineKey,
            Integer nMinutes) {
        Date nMinutesBefore = DateUtils.addMinutes(new Date(), (-1) * nMinutes);
        return machineParametersRepository.findByMachineKeyAndCreatedAtAfter(machineKey,
                nMinutesBefore);
    }

    public MachineMetricsResponseDto calculateMetrics(String machineKey,
            List<MachineParameters> machines) {
        Map<String, List<MachineParameters>> parametersGroupByMachineKey =
                machines.stream().collect(Collectors.groupingBy(MachineParameters::getMachineKey));

        List<ParameterMetric> parameterMetrics = new ArrayList<ParameterMetric>();
        parametersGroupByMachineKey.forEach((mKey, parameterList) -> {

            Map<String, List<Entry<String, Double>>> parameterGroupByParameterKey = parameterList.stream()
                    .map(mc -> mc.getParameters().entrySet().stream().collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(Entry<String, Double>::getKey));

            parameterGroupByParameterKey.forEach((key, value) -> {
                ParameterMetric parameterMetric = new ParameterMetric();
                List<Metrics> metrics = new ArrayList<Metrics>();
                Optional<Entry<String, Double>> min =
                        value.stream().min(Comparator.comparing(Entry<String, Double>::getValue));
                min.ifPresent(val -> metrics.add(Metrics.builder()
                        .metric(Constants.METRIC_MIN).value(val.getValue()).build()));

                Optional<Entry<String, Double>> max =
                        value.stream().max(Comparator.comparing(Entry<String, Double>::getValue));
                max.ifPresent(val -> metrics.add(Metrics.builder()
                        .metric(Constants.METRIC_MAX).value(val.getValue()).build()));


                OptionalDouble average =
                        value.stream().mapToDouble(Entry<String, Double>::getValue).average();
                average.ifPresent(val -> metrics.add(Metrics.builder()
                        .metric(Constants.METRIC_AVERAGE).value(val).build()));

                DoubleStream sorted =
                        value.stream().mapToDouble(Entry<String, Double>::getValue).sorted();
                double median = value.size() % 2 == 0
                        ? sorted.skip(value.size() / 2 - 1).limit(2).average().getAsDouble()
                        : sorted.skip(value.size() / 2).findFirst().getAsDouble();
                metrics.add(Metrics.builder()
                        .metric(Constants.METRIC_MEDIAN).value(median).build());

                parameterMetric.setMetrics(metrics);
                parameterMetric.setParameter(key);

                parameterMetrics.add(parameterMetric);
            });
        });

        return MachineMetricsResponseDto.builder().machineKey(machineKey)
                .parameterMetrics(parameterMetrics)
                .build();
    }

    private void calculateAndStoreMinOfParameters(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        Optional<Entry<String, Double>> min =
                value.stream().min(Comparator.comparing(Entry<String, Double>::getValue));
        min.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_MIN).value(val.getValue()).build()));
    }

    private void calculateAndStoreMaxOfParameters(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        Optional<Entry<String, Double>> max =
                value.stream().max(Comparator.comparing(Entry<String, Double>::getValue));
        max.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_MAX).value(val.getValue()).build()));
    }

    private void calculateAndStoreAvgOfParameters(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        OptionalDouble average =
                value.stream().mapToDouble(Entry<String, Double>::getValue).average();
        average.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_AVERAGE).value(val).build()));
    }

    private void calculateAndStoreMedianOfParameters(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        DoubleStream sorted = value.stream().mapToDouble(Entry<String, Double>::getValue).sorted();
        double median = value.size() % 2 == 0
                ? sorted.skip(value.size() / 2 - 1).limit(2).average().getAsDouble()
                : sorted.skip(value.size() / 2).findFirst().getAsDouble();
        metrics.add(Metrics.builder().metric(Constants.METRIC_MEDIAN).value(median).build());
    }
}
