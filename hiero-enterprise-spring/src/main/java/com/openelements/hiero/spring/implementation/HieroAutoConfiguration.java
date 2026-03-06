package com.openelements.hiero.spring.implementation;

import com.openelements.hiero.base.AccountClient;
import com.openelements.hiero.base.FungibleTokenClient;
import com.openelements.hiero.base.SmartContractClient;
import com.openelements.hiero.base.FileClient;
import com.openelements.hiero.base.HieroContext;
import com.openelements.hiero.base.NftClient;
import com.openelements.hiero.base.TopicClient;
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
import com.openelements.hiero.base.implementation.TopicClientImpl;
import com.openelements.hiero.base.implementation.TopicRepositoryImpl;
import com.openelements.hiero.base.implementation.TransactionRepositoryImpl;
import com.openelements.hiero.base.interceptors.ReceiveRecordInterceptor;
import com.openelements.hiero.base.mirrornode.AccountRepository;
import com.openelements.hiero.base.mirrornode.ContractRepository;
import com.openelements.hiero.base.mirrornode.MirrorNodeClient;
import com.openelements.hiero.base.mirrornode.NetworkRepository;
import com.openelements.hiero.base.mirrornode.NftRepository;
import com.openelements.hiero.base.mirrornode.TokenRepository;
import com.openelements.hiero.base.mirrornode.TopicRepository;
import com.openelements.hiero.base.mirrornode.TransactionRepository;
import com.openelements.hiero.base.protocol.ProtocolLayerClient;
import com.openelements.hiero.base.verification.ContractVerificationClient;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.annotation.ApplicationScope;

@AutoConfiguration
@EnableConfigurationProperties({HieroProperties.class, HieroNetworkProperties.class})
@Import({MicrometerSupportConfig.class})
public class HieroAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(HieroAutoConfiguration.class);

  @Bean
  @ApplicationScope
  HieroConfig hieroConfig(final HieroProperties properties) {
    return new HieroConfigImpl(properties);
  }

  @Bean
  @ApplicationScope
  HieroContext hieroContext(final HieroConfig hieroConfig) {
    return hieroConfig.createHieroContext();
  }

  @Bean
  ProtocolLayerClient protocolLevelClient(
      final HieroContext hieroContext,
      @Autowired(required = false) final ReceiveRecordInterceptor interceptor) {
    ProtocolLayerClientImpl protocolLayerClient = new ProtocolLayerClientImpl(hieroContext);
    if (interceptor != null) {
      protocolLayerClient.setRecordInterceptor(interceptor);
    }
    return protocolLayerClient;
  }

  @Bean
  FileClient fileClient(final ProtocolLayerClient protocolLayerClient) {
    return new FileClientImpl(protocolLayerClient);
  }

  @Bean
  SmartContractClient smartContractClient(
      final ProtocolLayerClient protocolLayerClient, FileClient fileClient) {
    return new SmartContractClientImpl(protocolLayerClient, fileClient);
  }

  @Bean
  AccountClient accountClient(final ProtocolLayerClient protocolLayerClient) {
    return new AccountClientImpl(protocolLayerClient);
  }

  @Bean
  NftClient nftClient(final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new NftClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  FungibleTokenClient tokenClient(
      final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new FungibleTokenClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  TopicClient topicClient(
      final ProtocolLayerClient protocolLayerClient, HieroContext hieroContext) {
    return new TopicClientImpl(protocolLayerClient, hieroContext.getOperatorAccount());
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  MirrorNodeClient mirrorNodeClient(final HieroContext hieroContext) {
    final String mirrorNodeEndpoint;
    final List<String> mirrorNetwork = hieroContext.getClient().getMirrorNetwork();
    if (mirrorNetwork.isEmpty()) {
      throw new IllegalArgumentException("Mirror node endpoint must be set");
    }
    mirrorNodeEndpoint = mirrorNetwork.get(0);
    final String baseUri;
    try {
      URL url = new URI(mirrorNodeEndpoint).toURL();
      final String mirrorNodeEndpointProtocol = url.getProtocol();
      final String mirrorNodeEndpointHost = url.getHost();
      final int mirrorNodeEndpointPort;
      if (mirrorNodeEndpointProtocol == "https" && url.getPort() == -1) {
        mirrorNodeEndpointPort = 443;
      } else if (mirrorNodeEndpointProtocol == "http" && url.getPort() == -1) {
        mirrorNodeEndpointPort = 80;
      } else if (url.getPort() == -1) {
        mirrorNodeEndpointPort = 443;
      } else {
        mirrorNodeEndpointPort = url.getPort();
      }
      baseUri =
          mirrorNodeEndpointProtocol
              + "://"
              + mirrorNodeEndpointHost
              + ":"
              + mirrorNodeEndpointPort;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Error parsing mirrorNodeEndpoint '" + mirrorNodeEndpoint + "'", e);
    }
    RestClient.Builder builder = RestClient.builder().baseUrl(baseUri);
    return new MirrorNodeClientImpl(builder);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  NftRepository nftRepository(final MirrorNodeClient mirrorNodeClient) {
    return new NftRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  ContractRepository contractRepository(final MirrorNodeClient mirrorNodeClient) {
    return new ContractRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  AccountRepository accountRepository(final MirrorNodeClient mirrorNodeClient) {
    return new AccountRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  NetworkRepository networkRepository(final MirrorNodeClient mirrorNodeClient) {
    return new NetworkRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TokenRepository tokenRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TokenRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TransactionRepository transactionRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TransactionRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.hiero",
      name = "mirrorNodeSupported",
      havingValue = "true",
      matchIfMissing = true)
  TopicRepository topicRepository(final MirrorNodeClient mirrorNodeClient) {
    return new TopicRepositoryImpl(mirrorNodeClient);
  }

  @Bean
  ContractVerificationClient contractVerificationClient(final HieroConfig hieroConfig) {
    return new ContractVerificationClientImplementation(hieroConfig);
  }
}
