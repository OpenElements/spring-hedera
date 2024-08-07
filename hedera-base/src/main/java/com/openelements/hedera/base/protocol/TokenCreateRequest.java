package com.openelements.hedera.base.protocol;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TopicId;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

public record TokenCreateRequest(Hbar maxTransactionFee,

                                 Duration transactionValidDuration,

                                 @NonNull String name,
                                 @NonNull String symbol,
                                 @NonNull AccountId treasuryAccountId,
                                 @NonNull PrivateKey treasuryKey,
                                 @NonNull TokenType tokenType,
                                 @Nullable PrivateKey supplyKey) implements TransactionRequest {

    static final int MAX_SYMBOL_LENGTH = 100;

    public TokenCreateRequest {
        Objects.requireNonNull(maxTransactionFee, "Max transaction fee cannot be null");
        Objects.requireNonNull(transactionValidDuration, "Transaction valid duration cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(symbol, "Symbol cannot be null");
        Objects.requireNonNull(treasuryAccountId, "Treasury account ID cannot be null");
        Objects.requireNonNull(tokenType, "Token type cannot be null");
        if (symbol.length() > MAX_SYMBOL_LENGTH) {
            throw new IllegalArgumentException("Symbol length must be less than or equal to " + MAX_SYMBOL_LENGTH);
        }
    }

    public static TokenCreateRequest of(@NonNull String name, @NonNull String symbol, @NonNull AccountId treasuryAccountId, @NonNull PrivateKey treasuryKey) {
        return of(name, symbol, treasuryAccountId, treasuryKey, TokenType.FUNGIBLE_COMMON);
    }

    public static TokenCreateRequest of(@NonNull String name, @NonNull String symbol, @NonNull AccountId treasuryAccountId, @NonNull PrivateKey treasuryKey, @NonNull TokenType tokenType) {
        return new TokenCreateRequest(Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION, name, symbol, treasuryAccountId, treasuryKey, tokenType, null);
    }

    public static TokenCreateRequest of(@NonNull String name, @NonNull String symbol, @NonNull AccountId treasuryAccountId, @NonNull PrivateKey treasuryKey, @NonNull TokenType tokenType, @NonNull PrivateKey supplyKey) {
        return new TokenCreateRequest(Hbar.from(100), TransactionRequest.DEFAULT_TRANSACTION_VALID_DURATION, name, symbol, treasuryAccountId, treasuryKey, tokenType, supplyKey);
    }
}
