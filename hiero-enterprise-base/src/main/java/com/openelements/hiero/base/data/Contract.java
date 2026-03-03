package com.openelements.hiero.base.data;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.Key;
import java.time.Instant;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Represents a smart contract on the Hiero network. */
public record Contract(
    @NonNull ContractId contractId,
    @Nullable Key adminKey,
    @Nullable AccountId autoRenewAccount,
    int autoRenewPeriod,
    @NonNull Instant createdTimestamp,
    boolean deleted,
    @Nullable Instant expirationTimestamp,
    @Nullable String fileId,
    @Nullable String evmAddress,
    @Nullable String memo,
    @Nullable Integer maxAutomaticTokenAssociations,
    @Nullable Long nonce,
    @Nullable String obtainerId,
    boolean permanentRemoval,
    @Nullable String proxyAccountId,
    @NonNull Instant fromTimestamp,
    @NonNull Instant toTimestamp,
    @Nullable String bytecode,
    @Nullable String runtimeBytecode) {
  public Contract {
    Objects.requireNonNull(contractId, "contractId must not be null");
    Objects.requireNonNull(createdTimestamp, "createdTimestamp must not be null");
    Objects.requireNonNull(fromTimestamp, "fromTimestamp must not be null");
    Objects.requireNonNull(toTimestamp, "toTimestamp must not be null");
  }
}
