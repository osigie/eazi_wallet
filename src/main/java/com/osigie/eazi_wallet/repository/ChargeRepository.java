package com.osigie.eazi_wallet.repository;

import com.osigie.eazi_wallet.domain.Charge;
import com.osigie.eazi_wallet.domain.ChargeTypeEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChargeRepository extends CrudRepository<Charge, UUID> {

    Optional<Charge> findByTypeAndIsActive(@Param("type") ChargeTypeEnum type, @Param("isActive") Boolean isActive);
}
