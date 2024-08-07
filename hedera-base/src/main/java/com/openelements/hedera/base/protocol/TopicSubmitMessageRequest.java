package com.openelements.hedera.base.protocol;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.TopicId;
import org.jspecify.annotations.NonNull;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

public record TopicSubmitMessageRequest(Hbar maxTransactionFee,

                                        Duration transactionValidDuration,

                                        @NonNull TopicId topicId,
                                        @NonNull byte[] message) implements TransactionRequest {

    static final int MAX_MESSAGE_LENGTH = 1024;

    public TopicSubmitMessageRequest {
       Objects.requireNonNull(topicId, "TopicId cannot be null");
         Objects.requireNonNull(message, "Message cannot be null");
         if(message.length > MAX_MESSAGE_LENGTH) {
             throw new IllegalArgumentException("Message cannot be longer than " + MAX_MESSAGE_LENGTH + " bytes");
         }
    }

    public static TopicSubmitMessageRequest of(@NonNull TopicId topicId, @NonNull String message) {
        Objects.requireNonNull(message, "Message cannot be null");
        return new TopicSubmitMessageRequest(DEFAULT_MAX_TRANSACTION_FEE, DEFAULT_TRANSACTION_VALID_DURATION, topicId, message.getBytes(StandardCharsets.UTF_8));
    }

    public static TopicSubmitMessageRequest of(@NonNull TopicId topicId, @NonNull byte[] message) {
        return new TopicSubmitMessageRequest(DEFAULT_MAX_TRANSACTION_FEE, DEFAULT_TRANSACTION_VALID_DURATION, topicId, message);
    }
}
