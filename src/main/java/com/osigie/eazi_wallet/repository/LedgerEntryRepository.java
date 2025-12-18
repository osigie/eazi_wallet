package com.osigie.eazi_wallet.repository;

import com.osigie.eazi_wallet.domain.EntryTypeEnum;
import com.osigie.eazi_wallet.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> id(UUID id);

    boolean existsByWalletIdAndTypeAndIdempotencyKey(@Param("id") UUID id, @Param("type") EntryTypeEnum type, @Param("idempotencyKey") String idempotencyKey);
}
