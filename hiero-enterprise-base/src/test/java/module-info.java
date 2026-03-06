open module com.openelements.hiero.base.test {
  requires com.openelements.hiero.base;
  requires io.github.cdimascio.dotenv.java;
  requires static org.jspecify;
  requires org.junit.jupiter.api;
  requires org.junit.jupiter.params;
  requires org.mockito;
  requires org.slf4j;
  requires okio;

  provides com.openelements.hiero.base.config.NetworkSettingsProvider with
      com.openelements.hiero.base.test.config.SoloActionNetworkSettingsProvider;
}
