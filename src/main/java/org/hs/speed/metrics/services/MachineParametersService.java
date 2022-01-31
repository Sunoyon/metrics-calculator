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
import org.hs.speed.metrics.utils.dto.AggrOfLatestParametersOfMachineDto;
import org.hs.speed.metrics.utils.dto.MachineLatestParametersResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.Metrics;
import org.hs.speed.metrics.utils.dto.MachineMetricsResponseDto.ParameterMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

@Service
public class MachineParametersService {

    @Autowired
    private MachineParametersRepository machineParametersRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     * Store MachineParameter object in Database
     * 
     * @param machineParameters: MachineParameter object
     * @return stored MachineParameter object
     */
    public MachineParameters save(MachineParameters machineParameters) {
        machineParameters.setId(UUID.randomUUID().toString());
        machineParameters.setCreatedAt(new Date());
        return machineParametersRepository.save(machineParameters);
    }

    /***
     * Find `MachineKeyParameter` objects of given `machineKey` and stored within past `nMinutes`
     * 
     * @param machineKey: machine key in String format
     * @param nMinutes: past n minutes in Integer format
     * @return
     */
    public List<MachineParameters> findMachineKeyOfPastNMinutes(String machineKey,
            Integer nMinutes) {
        Date nMinutesBefore = DateUtils.addMinutes(new Date(), (-1) * nMinutes);
        return machineParametersRepository.findByMachineKeyAndCreatedAtAfter(machineKey,
                nMinutesBefore);
    }

    /***
     * calculate metrics of parameters of given machine key
     * 
     * @param machineKey: machine key in String format
     * @param machines: List of MachineParameters objects
     * @return returns the metric values in MachineMetricsResponseDto format
     */
    public MachineMetricsResponseDto calculateMetrics(String machineKey,
            List<MachineParameters> machines) {

        Map<String, List<MachineParameters>> parametersGroupByMachineKey =
                machines.stream().collect(Collectors.groupingBy(MachineParameters::getMachineKey));

        List<ParameterMetric> parameterMetrics = new ArrayList<ParameterMetric>();
        parametersGroupByMachineKey.forEach((mKey, parameterList) -> {

            Map<String, List<Entry<String, Double>>> parametersGroupByParameterKey = parameterList
                    .stream()
                    .map(mc -> mc.getParameters().entrySet().stream().collect(Collectors.toList()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(Entry<String, Double>::getKey));

            parametersGroupByParameterKey.forEach((key, value) -> {
                List<Metrics> metrics = new ArrayList<Metrics>();

                calculateMinAndAppendToMetric(value, metrics);
                calculateMaxAndAppendToMetric(value, metrics);
                calculateAvgAndAppendToMetric(value, metrics);
                calculateMedianAndAppendToMetric(value, metrics);

                parameterMetrics
                        .add(ParameterMetric.builder().parameter(key).metrics(metrics).build());
            });
        });

        return MachineMetricsResponseDto.builder().machineKey(machineKey)
                .parameterMetrics(parameterMetrics).build();
    }

    /***
     * Calculate min of values of a given list of Entry objects and append the result in the given
     * list of `Metrics`
     * 
     * @param value: List of entry objects
     * @param metrics: Already created List containing Metrics object
     */
    private void calculateMinAndAppendToMetric(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        Optional<Entry<String, Double>> min =
                value.stream().min(Comparator.comparing(Entry<String, Double>::getValue));
        min.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_MIN).value(val.getValue()).build()));
    }

    /***
     * Calculate max of values of a given list of Entry objects and append the result in the given
     * list of `Metrics`
     * 
     * @param value: List of entry objects
     * @param metrics: Already created List containing Metrics object
     */
    private void calculateMaxAndAppendToMetric(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        Optional<Entry<String, Double>> max =
                value.stream().max(Comparator.comparing(Entry<String, Double>::getValue));
        max.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_MAX).value(val.getValue()).build()));
    }

    /***
     * Calculate avg of values of a given list of Entry objects and append the result in the given
     * list of `Metrics`
     * 
     * @param value: List of entry objects
     * @param metrics: Already created List containing Metrics object
     */
    private void calculateAvgAndAppendToMetric(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        OptionalDouble average =
                value.stream().mapToDouble(Entry<String, Double>::getValue).average();
        average.ifPresent(val -> metrics
                .add(Metrics.builder().metric(Constants.METRIC_AVERAGE).value(val).build()));
    }

    /***
     * Calculate median of values of a given list of Entry objects and append the result in the
     * given list of `Metrics`
     * 
     * @param value: List of entry objects
     * @param metrics: Already created List containing Metrics object
     */
    private void calculateMedianAndAppendToMetric(List<Entry<String, Double>> value,
            List<Metrics> metrics) {
        DoubleStream sorted = value.stream().mapToDouble(Entry<String, Double>::getValue).sorted();
        double median = value.size() % 2 == 0
                ? sorted.skip(value.size() / 2 - 1).limit(2).average().getAsDouble()
                : sorted.skip(value.size() / 2).findFirst().getAsDouble();
        metrics.add(Metrics.builder().metric(Constants.METRIC_MEDIAN).value(median).build());
    }

    /***
     * Get the latest parameters of machines
     * 
     * @return Latest parameters of machines
     */
    public List<MachineLatestParametersResponseDto> latestParameters() {
        Aggregation aggregation = Aggregation.newAggregation(
                        Aggregation.sort(Sort.Direction.DESC, MachineParameters.ATTR_CREATED_AT),
                        Aggregation.group(MachineParameters.ATTR_MACHINE_KEY)
                        .first(MachineParameters.ATTR_PARAMETERS)
                        .as(MachineParameters.ATTR_PARAMETERS));

        List<AggrOfLatestParametersOfMachineDto> latestMachineParameters =
                mongoTemplate.aggregate(aggregation, Constants.MACHINE_PARAMETER_DOCUMENT,
                        AggrOfLatestParametersOfMachineDto.class)
                        .getMappedResults();

        List<MachineLatestParametersResponseDto> resultParameters =
                new ArrayList<MachineLatestParametersResponseDto>();

        latestMachineParameters.forEach(mk -> {
            List<String> parameters = mk.getParameters().entrySet().stream()
                    .map(Entry<String, Double>::getKey).collect(Collectors.toList());

            MachineLatestParametersResponseDto machineLatestParameter =
                    MachineLatestParametersResponseDto.builder().machineKey(mk.getId())
                            .parameters(parameters).build();
            resultParameters.add(machineLatestParameter);
        });
        return resultParameters;
    }
}
