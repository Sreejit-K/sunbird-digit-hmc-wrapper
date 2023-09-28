package org.egov.sunbird.Repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.data.query.builder.SelectQueryBuilder;
import org.egov.common.data.repository.GenericRepository;
import org.egov.common.producer.Producer;
import org.egov.sunbird.Constants;
import org.egov.sunbird.Repository.rowmapper.VcServiceDeliveryRowMapper;
import org.egov.sunbird.models.VcServiceDelivery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class VCServiceDeliveryRepository extends GenericRepository<VcServiceDelivery> {

    protected VCServiceDeliveryRepository(Producer producer, NamedParameterJdbcTemplate namedParameterJdbcTemplate, RedisTemplate<String, Object> redisTemplate, SelectQueryBuilder selectQueryBuilder, VcServiceDeliveryRowMapper vcServiceDeliveryRowMapper , Optional<String> tableName) {
        super(producer, namedParameterJdbcTemplate, redisTemplate, selectQueryBuilder, vcServiceDeliveryRowMapper, Optional.of(Constants.NAME_OF_THE_MAPPER_TABLE));
    }

}
