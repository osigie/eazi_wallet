CREATE TABLE charges
(
    id         UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    percentage BIGINT                   NOT NULL,
    type       VARCHAR(255)             NOT NULL,
    is_active  BOOLEAN                  NOT NULL,
    CONSTRAINT pk_charges PRIMARY KEY (id)
);

CREATE TABLE ledger_entries
(
    id              UUID                     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE,
    idempotency_key VARCHAR(255)             NOT NULL,
    wallet_id       UUID                     NOT NULL,
    balance         DECIMAL                  NOT NULL,
    balance_after   DECIMAL                  NOT NULL,
    type            VARCHAR(255)             NOT NULL,
    CONSTRAINT pk_ledger_entries PRIMARY KEY (id)
);

CREATE TABLE wallets
(
    id             UUID                     NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE,
    balance_cached DECIMAL                  NOT NULL,
    CONSTRAINT pk_wallets PRIMARY KEY (id)
);

ALTER TABLE ledger_entries
    ADD CONSTRAINT unique_idempotency_key UNIQUE (idempotency_key);

ALTER TABLE ledger_entries
    ADD CONSTRAINT FK_LEDGER_ENTRIES_ON_WALLET FOREIGN KEY (wallet_id) REFERENCES wallets (id);


-- SEED CHARGES
INSERT INTO charges (id, created_at, updated_at, percentage, type, is_active)
VALUES (gen_random_uuid(),
        now(),
        now(), 1, 'TRANSFER_FEE', true);