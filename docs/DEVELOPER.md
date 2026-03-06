# Hiero Enterprise Java - Developer Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Architecture Overview](#architecture-overview)
4. [Module Structure](#module-structure)
5. [Core Concepts](#core-concepts)
6. [How Everything Works Together](#how-everything-works-together)


## Introduction

Welcome to the **Hiero Enterprise Java** codebase! This is a Java library that provides enterprise-grade integration with the Hiero blockchain network. If you're new to Java and blockchain development, don't worry - this documentation will guide you through everything step by step.

### What is Hiero?
Hiero is a blockchain network (currently based on Hedera Hashgraph) that allows you to:
- Create and manage accounts
- Store files on the blockchain
- Create and transfer tokens (fungible and NFTs)
- Deploy and interact with smart contracts
- Create topics for messaging
- Query network information

### What does this library do?
This library provides a **high-level, easy-to-use interface** for Java applications to interact with the Hiero blockchain. It handles all the complex blockchain communication details so you can focus on your business logic.

## Project Overview

### Project Structure
```
hiero-enterprise/
├── hiero-enterprise-base/          # Core functionality
├── hiero-enterprise-spring/        # Spring Boot integration
├── hiero-enterprise-microprofile/  # MicroProfile integration
├── hiero-enterprise-test/          # Testing utilities
├── hiero-enterprise-spring-sample/ # Spring Boot example
├── hiero-enterprise-microprofile-sample/ # MicroProfile example
└── pom.xml                         # Main Maven configuration
```

### Key Technologies Used
- **Java 17** - Modern Java features
- **Maven** - Build and dependency management
- **Spring Boot** - Enterprise Java framework
- **Hedera SDK** - Blockchain communication
- **MicroProfile** - Enterprise Java standards
- **JUnit 5** - Testing framework

## Architecture Overview

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Your Application                         │
├─────────────────────────────────────────────────────────────┤
│              Spring Boot / MicroProfile                     │
├─────────────────────────────────────────────────────────────┤
│              Hiero Enterprise Library                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │   Client Layer  │  │  Protocol Layer │  │ Mirror Node │  │
│  │                 │  │                 │  │   Layer     │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                    Hedera SDK                               │
├─────────────────────────────────────────────────────────────┤
│                    Hiero Network                            │
└─────────────────────────────────────────────────────────────┘
```

### Three-Layer Architecture

1. **Client Layer** - High-level, business-focused APIs
2. **Protocol Layer** - Low-level blockchain communication
3. **Mirror Node Layer** - Query historical data

## Module Structure

### 1. hiero-enterprise-base
**Purpose**: Core functionality that works with any Java framework

**Key Components**:
- `AccountClient` - Create and manage accounts
- `FileClient` - Store and retrieve files
- `TokenClient` - Handle fungible tokens
- `NftClient` - Handle non-fungible tokens
- `SmartContractClient` - Deploy and interact with smart contracts
- `TopicClient` - Create and manage messaging topics

### 2. hiero-enterprise-spring
**Purpose**: Spring Boot integration

**Key Components**:
- `@EnableHiero` annotation
- Auto-configuration classes
- Spring Boot properties support

### 3. hiero-enterprise-microprofile
**Purpose**: MicroProfile integration (for Quarkus, Helidon, etc.)

### 4. Sample Applications
**Purpose**: Working examples showing how to use the library

## Core Concepts

### 1. Operator Account
The **operator account** is the account that pays for all transactions. Think of it as your "service account" that funds all operations.

```java
// Configuration
spring.hiero.accountId=0.0.12345678
spring.hiero.privateKey=your-private-key
```

### 2. Clients
Each client provides a specific set of blockchain operations:

```java
@Autowired
private AccountClient accountClient;    // Account operations
@Autowired
private FileClient fileClient;          // File operations
@Autowired
private TokenClient tokenClient;        // Token operations
```

### 3. Transactions vs Queries
- **Transactions**: Modify the blockchain state (create account, transfer tokens)
- **Queries**: Read data from the blockchain (get balance, read file)

## How Everything Works Together

### 1. Application Startup
```java
@SpringBootApplication
@EnableHiero  // ← This is the magic!
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**What happens when you start the application:**

1. **Spring Boot starts** and looks for `@EnableHiero`
2. **Auto-configuration kicks in** (`HieroAutoConfiguration`)
3. **Configuration is loaded** from `application.properties`
4. **HieroContext is created** with your account details
5. **All clients are created** and injected into your beans
6. **Your application is ready** to use blockchain features!

### 2. Making a Blockchain Call
```java
@Service
public class MyService {
    @Autowired
    private AccountClient accountClient;

    public void createAccount() {
        Account newAccount = accountClient.createAccount();
        System.out.println("Created account: " + newAccount.accountId());
    }
}
```

**What happens when you call `createAccount()`:**

1. **AccountClient** receives the request
2. **ProtocolLayerClient** creates the transaction
3. **Hedera SDK** sends transaction to network
4. **Network** processes and confirms transaction
5. **Response** returns with new account details
6. **Account object** is created and returned

### 3. Data Flow Example
```
Your Code → AccountClient → ProtocolLayerClient → Hedera SDK → Hiero Network
     ↑                                                             ↓
     └────────────── Account Object ←──────────────────────────────┘
```
