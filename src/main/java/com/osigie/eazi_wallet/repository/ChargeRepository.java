package com.osigie.eazi_wallet.repository;

import com.osigie.eazi_wallet.domain.Charge;
import com.osigie.eazi_wallet.domain.ChargeTypeEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChargeRepository extends CrudRepository<Charge, UUID> {

    Optional<Charge> findByTypeAndIsActive(ChargeTypeEnum type, Boolean isActive);
}
