package com.osigie.eazi_wallet.repository;

import com.osigie.eazi_wallet.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
}
