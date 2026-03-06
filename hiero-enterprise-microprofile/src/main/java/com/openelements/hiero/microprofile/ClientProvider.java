package com.openelements.hiero.microprofile;

import com.openelements.hiero.base.FileClient;
import com.openelements.hiero.base.FungibleTokenClient;
import com.openelements.hiero.base.HieroContext;
import com.openelements.hiero.base.NftClient;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.config.HieroConfig;
import com.openelements.hiero.base.implementation.AccountClientImpl;
import com.openelements.hiero.base.implementation.AccountRepositoryImpl;
import com.openelements.hiero.base.implementation.ContractRepositoryImpl;
import com.openelements.hiero.base.implementation.FileClientImpl;
import com.openelements.hiero.base.implementation.FungibleTokenClientImpl;
import com.openelements.hiero.base.implementation.NetworkRepositoryImpl;
import com.openelements.hiero.base.implementation.NftClientImpl;
import com.openelements.hiero.base.implementation.NftRepositoryImpl;
import com.openelements.hiero.base.implementation.ProtocolLayerClientImpl;
import com.openelements.hiero.base.implementation.SmartContractClientImpl;
import com.openelements.hiero.base.implementation.TokenRepositoryImpl;
import com.openelements.hiero.base.implementation.TransactionRepositoryImpl;
import com.openelements.hiero.base.mirrornode.AccountRepository;
import com.openelements.hiero.base.mirrornode.ContractRepository;
import com.openelements.hiero.base.mirrornode.MirrorNodeClient;
import com.openelements.hiero.base.mirrornode.NetworkRepository;
import com.openelements.hiero.base.mirrornode.NftRepository;
import com.openelements.hiero.base.mirrornode.TokenRepository;
import com.openelements.hiero.base.mirrornode.TransactionRepository;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import com.openelements.hiero.base.verification.ContractVerificationClient;
import com.openelements.hiero.microprofile.implementation.ContractVerificationClientImpl;
import com.openelements.hiero.microprofile.implementation.HieroConfigImpl;
import com.openelements.hiero.microprofile.implementation.MirrorNodeClientImpl;
import com.openelements.hiero.microprofile.implementation.MirrorNodeJsonConverterImpl;
import com.openelements.hiero.microprofile.implementation.MirrorNodeRestClientImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.jspecify.annotations.NonNull;

public class ClientProvider {

  @Inject @ConfigProperties private HieroOperatorConfiguration configuration;

  @Inject @ConfigProperties private HieroNetworkConfiguration networkConfiguration;

  @NonNull
  @Produces
  @ApplicationScoped
  HieroConfig createHieroConfig() {
    return new HieroConfigImpl(configuration, networkConfiguration);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  HieroContext createHieroContext(@NonNull final HieroConfig hieroConfig) {
    return hieroConfig.createHieroContext();
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ProtocolLayerClient createProtocolLayerClient(@NonNull final HieroContext hieroContext) {
    return new ProtocolLayerClientImpl(hieroContext);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FileClient createFileClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    return new FileClientImpl(protocolLayerClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  SmartContractClient createSmartContractClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final FileClient fileClient) {
    return new SmartContractClientImpl(protocolLayerClient, fileClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftClient createNftClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    return new NftClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @NonNull
  @Produces
  @ApplicationScoped
  FungibleTokenClient createFungibleTokenClient(
      @NonNull final ProtocolLayerClient protocolLayerClient,
      @NonNull final HieroContext hieroContext) {
    return new FungibleTokenClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountClient createAccountClient(@NonNull final ProtocolLayerClient protocolLayerClient) {
    return new AccountClientImpl(protocolLayerClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractVerificationClient createContractVerificationClient(
      @NonNull final HieroConfig hieroConfig) {
    return new ContractVerificationClientImpl(hieroConfig);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  MirrorNodeClient createMirrorNodeClient(@NonNull final HieroConfig hieroConfig) {
    final String target =
        hieroConfig.getMirrorNodeAddresses().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No mirror node addresses configured"));
    final MirrorNodeRestClientImpl restClient = new MirrorNodeRestClientImpl(target);
    final MirrorNodeJsonConverterImpl jsonConverter = new MirrorNodeJsonConverterImpl();
    return new MirrorNodeClientImpl(restClient, jsonConverter);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  AccountRepository createAccountRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new AccountRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NetworkRepository createNetworkRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new NetworkRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  NftRepository createNftRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new NftRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TransactionRepository createTransactionRepository(
      @NonNull final MirrorNodeClient mirrorNodeClient) {
    return new TransactionRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  TokenRepository createTokenRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new TokenRepositoryImpl(mirrorNodeClient);
  }

  @NonNull
  @Produces
  @ApplicationScoped
  ContractRepository createContractRepository(@NonNull final MirrorNodeClient mirrorNodeClient) {
    return new ContractRepositoryImpl(mirrorNodeClient);
  }
}
