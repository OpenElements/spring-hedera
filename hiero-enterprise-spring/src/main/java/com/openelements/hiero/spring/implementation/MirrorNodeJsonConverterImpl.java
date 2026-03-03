package com.openelements.hiero.spring.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.ContractId;
import com.hedera.hashgraph.sdk.DelegateContractId;
import com.hedera.hashgraph.sdk.Key;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TransactionId;
import com.openelements.hiero.base.data.AccountInfo;
import com.openelements.hiero.base.data.Balance;
import com.openelements.hiero.base.data.ChunkInfo;
import com.openelements.hiero.base.data.Contract;
import com.openelements.hiero.base.data.CustomFee;
import com.openelements.hiero.base.data.ExchangeRate;
import com.openelements.hiero.base.data.ExchangeRates;
import com.openelements.hiero.base.data.FixedFee;
import com.openelements.hiero.base.data.FractionalFee;
import com.openelements.hiero.base.data.NetworkFee;
import com.openelements.hiero.base.data.NetworkStake;
import com.openelements.hiero.base.data.NetworkSupplies;
import com.openelements.hiero.base.data.Nft;
import com.openelements.hiero.base.data.NftTransfer;
import com.openelements.hiero.base.data.Page;
import com.openelements.hiero.base.data.RoyaltyFee;
import com.openelements.hiero.base.data.SinglePage;
import com.openelements.hiero.base.data.StakingRewardTransfer;
import com.openelements.hiero.base.data.Token;
import com.openelements.hiero.base.data.TokenInfo;
import com.openelements.hiero.base.data.TokenTransfer;
import com.openelements.hiero.base.data.Topic;
import com.openelements.hiero.base.data.TopicMessage;
import com.openelements.hiero.base.data.TransactionInfo;
import com.openelements.hiero.base.data.Transfer;
import com.openelements.hiero.base.implementation.MirrorNodeJsonConverter;
import com.openelements.hiero.base.protocol.data.TransactionType;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.NonNull;

public class MirrorNodeJsonConverterImpl implements MirrorNodeJsonConverter<JsonNode> {

  @Override
  public Optional<Nft> toNft(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final TokenId parsedTokenId = TokenId.fromString(node.get("token_id").asText());
      final AccountId account = AccountId.fromString(node.get("account_id").asText());
      final long serial = node.get("serial_number").asLong();
      final byte[] metadata = node.get("metadata").binaryValue();
      return Optional.of(new Nft(parsedTokenId, serial, account, metadata));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<NetworkSupplies> toNetworkSupplies(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final String releasedSupply = node.get("released_supply").asText();
      final String totalSupply = node.get("total_supply").asText();
      return Optional.of(new NetworkSupplies(releasedSupply, totalSupply));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<NetworkStake> toNetworkStake(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final long maxStakeReward = node.get("max_stake_rewarded").asLong();
      final long maxStakeRewardPerHbar = node.get("max_staking_reward_rate_per_hbar").asLong();
      final long maxTotalReward = node.get("max_total_reward").asLong();
      final double nodeRewardFeeFraction = node.get("node_reward_fee_fraction").asDouble();
      final long reservedStakingRewards = node.get("reserved_staking_rewards").asLong();
      final long rewardBalanceThreshold = node.get("reward_balance_threshold").asLong();
      final long stakeTotal = node.get("stake_total").asLong();
      final long stakingPeriodDuration = node.get("staking_period_duration").asLong();
      final long stakingPeriodsStored = node.get("staking_periods_stored").asLong();
      final double stakingRewardFeeFraction = node.get("staking_reward_fee_fraction").asDouble();
      final long stakingRewardRate = node.get("staking_reward_rate").asLong();
      final long stakingStartThreshold = node.get("staking_start_threshold").asLong();
      final long unreservedStakingRewardBalance =
          node.get("unreserved_staking_reward_balance").asLong();

      return Optional.of(
          new NetworkStake(
              maxStakeReward,
              maxStakeRewardPerHbar,
              maxTotalReward,
              nodeRewardFeeFraction,
              reservedStakingRewards,
              rewardBalanceThreshold,
              stakeTotal,
              stakingPeriodDuration,
              stakingPeriodsStored,
              stakingRewardFeeFraction,
              stakingRewardRate,
              stakingStartThreshold,
              unreservedStakingRewardBalance));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<ExchangeRates> toExchangeRates(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final int currentCentEquivalent = node.get("current_rate").get("cent_equivalent").asInt();
      final int currentHbarEquivalent = node.get("current_rate").get("hbar_equivalent").asInt();
      final Instant currentExpirationTime =
          Instant.ofEpochSecond(node.get("current_rate").get("expiration_time").asLong());

      final int nextCentEquivalent = node.get("next_rate").get("cent_equivalent").asInt();
      final int nextHbarEquivalent = node.get("next_rate").get("hbar_equivalent").asInt();
      final Instant nextExpirationTime =
          Instant.ofEpochSecond(node.get("next_rate").get("expiration_time").asLong());

      return Optional.of(
          new ExchangeRates(
              new ExchangeRate(currentCentEquivalent, currentHbarEquivalent, currentExpirationTime),
              new ExchangeRate(nextCentEquivalent, nextHbarEquivalent, nextExpirationTime)));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public Optional<AccountInfo> toAccountInfo(final JsonNode node) {
    try {
      final AccountId accountId = AccountId.fromString(node.get("account").asText());
      final String evmAddress = node.get("evm_address").asText();
      final long ethereumNonce = node.get("ethereum_nonce").asLong();
      final long pendingReward = node.get("pending_reward").asLong();
      final long balance = node.get("balance").get("balance").asLong();
      return Optional.of(
          new AccountInfo(accountId, evmAddress, balance, ethereumNonce, pendingReward));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public List<NetworkFee> toNetworkFees(final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return List.of();
    }

    if (!node.has("fees")) {
      return List.of();
    }

    final JsonNode feesNode = node.get("fees");
    return jsonArrayToStream(feesNode)
        .map(
            n -> {
              try {
                final long gas = n.get("gas").asLong();
                final String transactionType = n.get("transaction_type").asText();
                return new NetworkFee(gas, transactionType);
              } catch (final Exception e) {
                throw new JsonParseException(n, e);
              }
            })
        .toList();
  }

  @Override
  public @NonNull Optional<TransactionInfo> toTransactionInfo(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    if (node.has("transactions")) {
      node = jsonArrayToStream(node.get("transactions")).findFirst().get();
    }

    try {
      final String transactionId = node.get("transaction_id").asText();
      final byte[] bytes = node.get("bytes").asText().getBytes();
      final long chargedTxFee = node.get("charged_tx_fee").asLong();
      final Instant consensusTimestamp =
          Instant.ofEpochSecond(node.get("consensus_timestamp").asLong());
      final String entityId = node.get("entity_id").asText();
      final String maxFee = node.get("max_fee").asText();
      final byte[] memo = node.get("memo_base64").asText().getBytes();
      final TransactionType name = TransactionType.from(node.get("name").asText());
      final String _node = node.get("node").asText();
      final int nonce = node.get("nonce").asInt();
      final Instant parentConsensusTimestamp =
          node.get("parent_consensus_timestamp").isNull()
              ? null
              : Instant.ofEpochSecond(node.get("parent_consensus_timestamp").asLong());
      final String result = node.get("result").asText();
      final boolean scheduled = node.get("scheduled").asBoolean();
      final byte[] transactionHash = node.get("transaction_hash").asText().getBytes();
      final String validDurationSeconds = node.get("valid_duration_seconds").asText();
      final Instant validStartTimestamp =
          Instant.ofEpochSecond(node.get("valid_start_timestamp").asLong());

      final List<NftTransfer> nftTransfers =
          jsonArrayToStream(node.get("nft_transfers")).map(n -> toNftTransfer(n)).toList();

      final List<StakingRewardTransfer> stakingRewardTransfers =
          jsonArrayToStream(node.get("staking_reward_transfers"))
              .map(n -> toStakingRewardTransfer(n))
              .toList();

      final List<TokenTransfer> tokenTransfers =
          jsonArrayToStream(node.get("token_transfers")).map(n -> toTokenTransfer(n)).toList();

      final List<Transfer> transfers =
          jsonArrayToStream(node.get("transfers")).map(n -> toTransfer(n)).toList();

      return Optional.of(
          new TransactionInfo(
              transactionId,
              bytes,
              chargedTxFee,
              consensusTimestamp,
              entityId,
              maxFee,
              memo,
              name,
              nftTransfers,
              _node,
              nonce,
              parentConsensusTimestamp,
              result,
              scheduled,
              stakingRewardTransfers,
              tokenTransfers,
              transactionHash,
              transfers,
              validDurationSeconds,
              validStartTimestamp));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<TransactionInfo> toTransactionInfos(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return List.of();
    }
    if (!node.has("transactions")) {
      return List.of();
    }

    final JsonNode transactionsNode = node.get("transactions");

    return jsonArrayToStream(transactionsNode)
        .map(n -> toTransactionInfo(n))
        .filter(n -> n.isPresent())
        .map(n -> n.get())
        .toList();
  }

  private Transfer toTransfer(JsonNode node) {
    final AccountId account = AccountId.fromString(node.get("account").asText());
    final long amount = node.get("amount").asLong();
    final boolean isApproval = node.get("is_approval").asBoolean();

    return new Transfer(account, amount, isApproval);
  }

  private TokenTransfer toTokenTransfer(JsonNode node) {
    final TokenId tokenId = TokenId.fromString(node.get("token_id").asText());
    final AccountId account = AccountId.fromString(node.get("account").asText());
    final long amount = node.get("amount").asLong();
    final boolean isApproval = node.get("is_approval").asBoolean();

    return new TokenTransfer(tokenId, account, amount, isApproval);
  }

  private StakingRewardTransfer toStakingRewardTransfer(JsonNode node) {
    final AccountId account = AccountId.fromString(node.get("account").asText());
    long amount = node.get("amount").asLong();

    return new StakingRewardTransfer(account, amount);
  }

  private NftTransfer toNftTransfer(JsonNode node) {
    final boolean isApproval = node.get("is_approval").asBoolean();
    final AccountId receiverAccountId =
        node.get("receiver_account_id").isNull()
            ? null
            : AccountId.fromString(node.get("receiver_account_id").asText());
    final AccountId senderAccountId =
        node.get("sender_account_id").isNull()
            ? null
            : AccountId.fromString(node.get("sender_account_id").asText());
    final long serialNumber = node.get("serial_number").asLong();
    final TokenId tokenId = TokenId.fromString(node.get("token_id").asText());

    return new NftTransfer(isApproval, receiverAccountId, senderAccountId, serialNumber, tokenId);
  }

  @Override
  public List<Nft> toNfts(@NonNull JsonNode node) {
    if (!node.has("nfts")) {
      return List.of();
    }
    final JsonNode nftsNode = node.get("nfts");
    if (!nftsNode.isArray()) {
      throw new IllegalArgumentException("NFTs node is not an array: " + nftsNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(nftsNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toNft(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public Optional<TokenInfo> toTokenInfo(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final TokenId tokenId = TokenId.fromString(node.get("token_id").asText());
      final TokenType type = TokenType.valueOf(node.get("type").asText());
      final String name = node.get("name").asText();
      final String symbol = node.get("symbol").asText();
      final String memo = node.get("memo").asText();
      final long decimals = node.get("decimals").asLong();
      final byte[] metadata = node.get("metadata").asText().getBytes();
      final Instant createdTimeStamp =
          Instant.ofEpochSecond(node.get("created_timestamp").asLong());
      final Instant modifiedTimestamp =
          Instant.ofEpochSecond(node.get("modified_timestamp").asLong());
      final TokenSupplyType supplyType = TokenSupplyType.valueOf(node.get("supply_type").asText());
      final String totalSupply = node.get("total_supply").asText();
      final String initialSupply = node.get("initial_supply").asText();
      final AccountId treasuryAccountId =
          AccountId.fromString(node.get("treasury_account_id").asText());
      final boolean deleted = node.get("deleted").asBoolean();
      final String maxSupply = node.get("max_supply").asText();

      final Instant expiryTimestamp;
      if (!node.get("expiry_timestamp").isNull()) {
        BigInteger nanoseconds = new BigInteger(node.get("expiry_timestamp").asText());
        BigInteger expirySeconds = nanoseconds.divide(BigInteger.valueOf(1_000_000_000));
        expiryTimestamp = Instant.ofEpochSecond(expirySeconds.longValue());
      } else {
        expiryTimestamp = null;
      }

      final CustomFee customFees = getCustomFee(node.get("custom_fees"));

      return Optional.of(
          new TokenInfo(
              tokenId,
              type,
              name,
              symbol,
              memo,
              decimals,
              metadata,
              createdTimeStamp,
              modifiedTimestamp,
              expiryTimestamp,
              supplyType,
              initialSupply,
              totalSupply,
              maxSupply,
              treasuryAccountId,
              deleted,
              customFees));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  private CustomFee getCustomFee(JsonNode node) {
    List<FractionalFee> fractionalFees = List.of();
    List<FixedFee> fixedFees = List.of();
    List<RoyaltyFee> royaltyFees = List.of();

    if (node.has("fixed_fees")) {
      JsonNode fixedFeeNode = node.get("fixed_fees");
      if (!fixedFeeNode.isArray()) {
        throw new IllegalArgumentException("FixedFees node is not an array: " + fixedFeeNode);
      }
      fixedFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(fixedFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long amount = n.get("amount").asLong();
                    final AccountId accountId =
                        n.get("collector_account_id").isNull()
                            ? null
                            : AccountId.fromString(n.get("collector_account_id").asText());
                    final TokenId tokenId =
                        n.get("denominating_token_id").isNull()
                            ? null
                            : TokenId.fromString(n.get("denominating_token_id").asText());
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();
    }

    if (node.has("fractional_fees")) {
      JsonNode fractionalFeeNode = node.get("fractional_fees");
      if (!fractionalFeeNode.isArray()) {
        throw new IllegalArgumentException(
            "FractionalFee node is not an array: " + fractionalFeeNode);
      }
      fractionalFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      fractionalFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long numeratorAmount = n.get("amount").get("numerator").asLong();
                    final long denominatorAmount = n.get("amount").get("denominator").asLong();
                    final AccountId accountId =
                        n.get("collector_account_id").isNull()
                            ? null
                            : AccountId.fromString(n.get("collector_account_id").asText());
                    final TokenId tokenId =
                        n.get("denominating_token_id").isNull()
                            ? null
                            : TokenId.fromString(n.get("denominating_token_id").asText());
                    return new FractionalFee(
                        numeratorAmount, denominatorAmount, accountId, tokenId);
                  })
              .toList();
    }

    if (node.has("royalty_fees")) {
      JsonNode royaltyFeeNode = node.get("royalty_fees");
      if (!royaltyFeeNode.isArray()) {
        throw new IllegalArgumentException("RoyaltyFee node is not an array: " + royaltyFeeNode);
      }
      royaltyFees =
          StreamSupport.stream(
                  Spliterators.spliteratorUnknownSize(
                      royaltyFeeNode.iterator(), Spliterator.ORDERED),
                  false)
              .map(
                  n -> {
                    final long numeratorAmount = n.get("amount").get("numerator").asLong();
                    final long denominatorAmount = n.get("amount").get("denominator").asLong();
                    final long fallbackFeeAmount = n.get("fallback_fee").get("amount").asLong();
                    final AccountId accountId =
                        n.get("collector_account_id").isNull()
                            ? null
                            : AccountId.fromString(n.get("collector_account_id").asText());
                    final TokenId tokenId =
                        n.get("fallback_fee").get("denominating_token_id").isNull()
                            ? null
                            : TokenId.fromString(
                                n.get("fallback_fee").get("denominating_token_id").asText());
                    return new RoyaltyFee(
                        numeratorAmount, denominatorAmount, fallbackFeeAmount, accountId, tokenId);
                  })
              .toList();
    }

    return new CustomFee(fixedFees, fractionalFees, royaltyFees);
  }

  @Override
  public List<Balance> toBalances(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("balances")) {
      return List.of();
    }
    final JsonNode balancesNode = node.get("balances");
    if (!balancesNode.isArray()) {
      throw new IllegalArgumentException("TokenBalances node is not an array: " + balancesNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(balancesNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toBalance(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public List<Token> toTokens(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("tokens")) {
      return List.of();
    }
    final JsonNode tokens = node.get("tokens");
    if (!tokens.isArray()) {
      throw new IllegalArgumentException("Tokens node is not an array: " + tokens);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(tokens.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toToken(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  @Override
  public @NonNull Optional<Topic> toTopic(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final TopicId topicId = TopicId.fromString(node.get("topic_id").asText());
      final PublicKey adminKey =
          node.get("admin_key").isNull()
              ? null
              : PublicKey.fromString(node.get("admin_key").get("key").asText());
      final AccountId autoRenewAccount =
          node.get("auto_renew_account").isNull()
              ? null
              : AccountId.fromString(node.get("auto_renew_account").asText());
      final int autoRenewPeriod = node.get("auto_renew_period").asInt();
      final Instant createdTimestamp =
          Instant.ofEpochSecond(node.get("created_timestamp").asLong());
      final boolean deleted = node.get("deleted").asBoolean();
      final PublicKey feeScheduleKey =
          node.get("fee_schedule_key").isNull()
              ? null
              : PublicKey.fromString(node.get("fee_schedule_key").get("key").asText());
      final String memo = node.get("memo").asText();
      final PublicKey submitKey =
          node.get("submit_key").isNull()
              ? null
              : PublicKey.fromString(node.get("submit_key").get("key").asText());
      final Instant fromTimestamp =
          Instant.ofEpochSecond(node.get("timestamp").get("from").asLong());
      final Instant toTimestamp = Instant.ofEpochSecond(node.get("timestamp").get("to").asLong());

      final List<FixedFee> fixedFees =
          jsonArrayToStream(node.get("custom_fees").get("fixed_fees"))
              .map(
                  n -> {
                    final long amount = n.get("amount").asLong();
                    final AccountId accountId =
                        n.get("collector_account_id").isNull()
                            ? null
                            : AccountId.fromString(n.get("collector_account_id").asText());
                    final TokenId tokenId =
                        n.get("denominating_token_id").isNull()
                            ? null
                            : TokenId.fromString(n.get("denominating_token_id").asText());
                    return new FixedFee(amount, accountId, tokenId);
                  })
              .toList();

      final List<PublicKey> feeExemptKeyList =
          jsonArrayToStream(node.get("fee_exempt_key_list"))
              .map(n -> PublicKey.fromString(n.get("key").asText()))
              .toList();

      return Optional.of(
          new Topic(
              topicId,
              adminKey,
              autoRenewAccount,
              autoRenewPeriod,
              createdTimestamp,
              fixedFees,
              feeExemptKeyList,
              feeScheduleKey,
              submitKey,
              deleted,
              memo,
              fromTimestamp,
              toTimestamp));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull Optional<TopicMessage> toTopicMessage(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }
    try {
      final JsonNode chunk = node.get("chunk_info");
      ChunkInfo chunkInfo = null;
      if (!chunk.isNull()) {
        final TransactionId transactionId =
            TransactionId.fromString(chunk.get("initial_transaction_id").asText());
        final int nonce = chunk.get("nonce").asInt();
        final int number = chunk.get("number").asInt();
        final int total = chunk.get("total").asInt();
        final boolean scheduled = chunk.get("scheduled").asBoolean();
        chunkInfo = new ChunkInfo(transactionId, nonce, number, total, scheduled);
      }

      final Instant consensusTimestamp =
          Instant.ofEpochSecond(node.get("consensus_timestamp").asLong());
      final String message = new String(Base64.getDecoder().decode(node.get("message").asText()));
      final AccountId payerAccountId = AccountId.fromString(node.get("payer_account_id").asText());
      final byte[] runningHash = node.get("running_hash").asText().getBytes();
      final int runningHashVersion = node.get("running_hash_version").asInt();
      final long sequenceNumber = node.get("sequence_number").asLong();
      final TopicId topicId = TopicId.fromString(node.get("topic_id").asText());

      return Optional.of(
          new TopicMessage(
              chunkInfo,
              consensusTimestamp,
              message,
              payerAccountId,
              runningHash,
              runningHashVersion,
              sequenceNumber,
              topicId));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<TopicMessage> toTopicMessages(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("messages")) {
      return List.of();
    }

    final JsonNode messages = node.get("messages");
    if (!messages.isArray()) {
      throw new IllegalArgumentException("Messages node is not an array: " + messages);
    }

    return jsonArrayToStream(messages)
        .map(n -> toTopicMessage(n))
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .toList();
  }

  private Optional<Token> toToken(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final byte[] metadata = node.get("metadata").asText().getBytes();
      final String name = node.get("name").asText();
      final String symbol = node.get("symbol").asText();
      final long decimals = node.get("decimals").asLong();
      final TokenType type = TokenType.valueOf(node.get("type").asText());
      final TokenId tokenId =
          node.get("token_id").isNull() ? null : TokenId.fromString(node.get("token_id").asText());

      return Optional.of(new Token(decimals, metadata, name, symbol, tokenId, type));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  private Optional<Balance> toBalance(JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final AccountId account = AccountId.fromString(node.get("account").asText());
      final long balance = node.get("balance").asLong();
      final long decimals = node.get("decimals").asLong();

      return Optional.of(new Balance(account, balance, decimals));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @NonNull
  private Stream<JsonNode> jsonArrayToStream(@NonNull final JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.isArray()) {
      throw new JsonParseException("not an array", node);
    }
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(node.iterator(), Spliterator.ORDERED), false);
  }

  @Override
  public @NonNull Optional<Contract> toContract(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return Optional.empty();
    }

    try {
      final ContractId contractId = ContractId.fromString(node.get("contract_id").asText());
      final Key adminKey;

      if (node.get("admin_key").isNull()) {
        adminKey = null;
      } else {
        final String keyType = node.get("admin_key").get("_type").asText();
        final String key = node.get("admin_key").get("key").asText();

        if (keyType.equals("ProtobufEncoded")) {
          adminKey = parseProtoBufEncodedKey(key);
        } else {
          adminKey = PublicKey.fromString(key);
        }
      }

      final AccountId autoRenewAccount =
          node.get("auto_renew_account").isNull()
              ? null
              : AccountId.fromString(node.get("auto_renew_account").asText());
      final int autoRenewPeriod =
          node.get("auto_renew_period").isNull() ? 0 : node.get("auto_renew_period").asInt();
      final Instant createdTimestamp =
          Instant.ofEpochSecond(
              node.get("created_timestamp").isNumber()
                  ? node.get("created_timestamp").asLong()
                  : Long.parseLong(node.get("created_timestamp").asText().split("\\.")[0]));
      final boolean deleted = !node.get("deleted").isNull() && node.get("deleted").asBoolean();
      final Instant expirationTimestamp =
          node.get("expiration_timestamp").isNull()
              ? null
              : Instant.ofEpochSecond(
                  Long.parseLong(node.get("expiration_timestamp").asText().split("\\.")[0]));
      final String fileId = node.get("file_id").isNull() ? null : node.get("file_id").asText();
      final String evmAddress =
          node.get("evm_address").isNull() ? null : node.get("evm_address").asText();
      final String memo = node.get("memo").isNull() ? null : node.get("memo").asText();
      final Integer maxAutomaticTokenAssociations =
          node.get("max_automatic_token_associations").isNull()
              ? null
              : node.get("max_automatic_token_associations").asInt();
      final Long nonce = node.get("nonce").isNull() ? null : node.get("nonce").asLong();
      final String obtainerId =
          node.get("obtainer_id").isNull() ? null : node.get("obtainer_id").asText();
      final boolean permanentRemoval =
          !node.get("permanent_removal").isNull() && node.get("permanent_removal").asBoolean();
      final String proxyAccountId =
          node.get("proxy_account_id").isNull() ? null : node.get("proxy_account_id").asText();
      final Instant fromTimestamp =
          Instant.ofEpochSecond(node.get("timestamp").get("from").asLong());
      final Instant toTimestamp = Instant.ofEpochSecond(node.get("timestamp").get("to").asLong());
      final String bytecode =
          (!node.has("bytecode") || node.get("bytecode").isNull())
              ? null
              : node.get("bytecode").asText();
      final String runtimeBytecode =
          (!node.has("runtime_bytecode") || node.get("runtime_bytecode").isNull())
              ? null
              : node.get("runtime_bytecode").asText();

      return Optional.of(
          new Contract(
              contractId,
              adminKey,
              autoRenewAccount,
              autoRenewPeriod,
              createdTimestamp,
              deleted,
              expirationTimestamp,
              fileId,
              evmAddress,
              memo,
              maxAutomaticTokenAssociations,
              nonce,
              obtainerId,
              permanentRemoval,
              proxyAccountId,
              fromTimestamp,
              toTimestamp,
              bytecode,
              runtimeBytecode));
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull Page<Contract> toContractPage(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (node.isNull() || node.isEmpty()) {
      return new SinglePage<>(List.of());
    }

    try {
      final List<Contract> contracts = toContracts(node);
      return new SinglePage<>(contracts);
    } catch (final Exception e) {
      throw new JsonParseException(node, e);
    }
  }

  @Override
  public @NonNull List<Contract> toContracts(@NonNull JsonNode node) {
    Objects.requireNonNull(node, "jsonNode must not be null");
    if (!node.has("contracts")) {
      return List.of();
    }
    final JsonNode contractsNode = node.get("contracts");
    if (!contractsNode.isArray()) {
      throw new IllegalArgumentException("Contracts node is not an array: " + contractsNode);
    }
    Spliterator<JsonNode> spliterator =
        Spliterators.spliteratorUnknownSize(contractsNode.iterator(), Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false)
        .map(n -> toContract(n))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .toList();
  }

  private @NonNull Key parseProtoBufEncodedKey(@NonNull String key) throws Exception {
    Objects.requireNonNull(key, "key must not be null");
    final byte[] bytes = HexFormat.of().parseHex(key);
    final com.hedera.hashgraph.sdk.proto.Key protoKey =
        com.hedera.hashgraph.sdk.proto.Key.parseFrom(bytes);

    return switch (protoKey.getKeyCase()) {
      case ED25519 -> PublicKey.fromBytesED25519(protoKey.getEd25519().toByteArray());

      case ECDSA_SECP256K1 -> PublicKey.fromBytesECDSA(protoKey.getECDSASecp256K1().toByteArray());

      case CONTRACTID -> ContractId.fromBytes(protoKey.getContractID().toByteArray());

      case DELEGATABLE_CONTRACT_ID ->
          DelegateContractId.fromBytes(protoKey.getDelegatableContractId().toByteArray());

      default ->
          throw new IllegalArgumentException(
              "Unsupported protobuf key type: " + protoKey.getKeyCase());
    };
  }
}
