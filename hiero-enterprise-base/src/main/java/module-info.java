module com.openelements.hiero.base {
  exports com.openelements.hiero.base;
  exports com.openelements.hiero.base.protocol;
  exports com.openelements.hiero.base.mirrornode;
  exports com.openelements.hiero.base.verification;
  exports com.openelements.hiero.base.data;
  exports com.openelements.hiero.base.config;
  exports com.openelements.hiero.base.implementation to
      com.openelements.hiero.base.test;
  exports com.openelements.hiero.base.implementation.data to
      com.openelements.hiero.base.test;
  exports com.openelements.hiero.base.config.implementation;
  exports com.openelements.hiero.base.protocol.data;
  exports com.openelements.hiero.base.interceptors to
      com.openelements.hiero.base.test;

  uses com.openelements.hiero.base.config.NetworkSettingsProvider;

  provides com.openelements.hiero.base.config.NetworkSettingsProvider with
      com.openelements.hiero.base.config.hedera.HederaNetworkSettingsProvider;

  requires transitive sdk; // Hedera SDK
  requires org.slf4j;
  requires com.google.protobuf; // TODO: We should not have the need to use it
  requires static org.jspecify;
  requires com.google.auto.service;
  requires okio;
}
