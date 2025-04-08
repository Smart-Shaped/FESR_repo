package com.smartshaped.fesr.gencast.serving.repository;

import com.smartshaped.fesr.gencast.serving.model.GencastModel;
import com.smartshaped.fesr.gencast.serving.model.GencastModelId;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GencastRepository extends CassandraRepository<GencastModel, GencastModelId> {}
