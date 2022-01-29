package org.hs.speed.metrics.repositories;

import org.hs.speed.metrics.models.MachineParameters;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineParametersRepository extends MongoRepository<MachineParameters, String> {

}
