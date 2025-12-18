ALTER TABLE ledger_entries
DROP
CONSTRAINT unique_idempotency_key;

ALTER TABLE ledger_entries
    ADD CONSTRAINT unique_wallet_id_and_type_and_idempotencyKey
        UNIQUE (wallet_id, type, idempotency_key);
