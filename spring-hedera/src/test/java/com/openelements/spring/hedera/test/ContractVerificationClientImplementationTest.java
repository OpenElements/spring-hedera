package com.openelements.spring.hedera.test;

import com.hedera.hashgraph.sdk.ContractId;
import com.openelements.hedera.base.HederaClient;
import com.openelements.hedera.base.data.ContractVerificationState;
import com.openelements.hedera.base.implementation.HederaNetwork;
import com.openelements.spring.hedera.api.ContractVerificationClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.condition.DisabledIf;

@SpringBootTest(classes = TestConfig.class)
class ContractVerificationClientImplementationTest {


    @Autowired
    private HederaClient hederaClient;

    @Autowired
    private HederaNetwork hederaNetwork;

    @Autowired
    private ContractVerificationClient contractVerificationClient;

    private Path getResource(String resource) {
        return Path.of(ContractVerificationClientImplementationTest.class.getResource(resource).getPath());
    }

    private boolean isNotSupportedChain() {
        return hederaNetwork == HederaNetwork.CUSTOM;
    }

    @Test
    @DisabledIf(value = "isNotSupportedChain", disabledReason = "Verification is currently not supported for custom chains")
    void test() throws Exception {
        //given
        final String contractName = "HelloWorld";
        final Path binPath = getResource("/HelloWorld.bin");
        final Path solPath = getResource("/HelloWorld.sol");
        final String contractSource = Files.readString(solPath, StandardCharsets.UTF_8);
        final Path metadataPath = getResource("/HelloWorld.metadata.json");
        final String contractMetadata = Files.readString(metadataPath, StandardCharsets.UTF_8);
        final ContractId contractId = hederaClient.createContract(binPath);

        //when
        final ContractVerificationState state = contractVerificationClient.verify(
                contractId, contractName, contractSource, contractMetadata);

        //then
        Assertions.assertEquals(ContractVerificationState.FULL, state);
    }

}