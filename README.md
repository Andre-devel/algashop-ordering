# AlgaShop Ordering Service

## 📋 Sobre o Projeto

Este é o **Serviço de Pedidos (Ordering Service)** do AlgaShop, um microsserviço **em desenvolvimento** como parte do curso de **Especialista em Microsserviços** da AlgaWorks. O projeto implementa um sistema de gerenciamento de pedidos seguindo os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**.

> ⚠️ **Projeto em Desenvolvimento**: Este é um projeto educacional que está sendo desenvolvido durante o curso. Algumas funcionalidades podem estar incompletas ou em implementação.

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

- **Domain Layer**: Contém a lógica de negócio pura, entidades, value objects e regras de domínio
- **Infrastructure Layer**: Implementa persistência, repositórios e integrações externas
- **Application Layer**: Orquestra casos de uso e coordena entre domínio e infraestrutura

## 🎯 Funcionalidades Principais

### ✅ Implementado
- **Order**: Gerenciamento do ciclo de vida de pedidos
- **Customer**: Informações e validações de clientes
- **ShoppingCart**: Carrinho de compras com itens
- **OrderItem**: Itens individuais dos pedidos

### 🔄 Em Desenvolvimento
- **API REST**: Endpoints para operações CRUD
- **Event Sourcing**: Eventos de domínio
- **Integration Tests**: Testes de integração completos
- **Documentation**: Documentação da API

### 📋 Planejado
- **Payment Integration**: Integração com gateway de pagamento
- **Notification Service**: Notificações de status do pedido
- **Audit Logging**: Logs de auditoria
- **Metrics & Monitoring**: Métricas e monitoramento

### Estados do Pedido
- `DRAFT` → `PLACED` → `PAID` → `READY`
- `CANCELED` (pode ser cancelado a partir de qualquer estado anterior)

### Value Objects
- **Money**: Representação segura de valores monetários
- **Email**: Validação de endereços de email
- **Document**: Validação de documentos (CPF/CNPJ)
- **Address**: Endereços de cobrança e entrega
- **Product**: Informações de produtos

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Lombok** (redução de boilerplate)
- **Gradle** (gerenciamento de dependências)
- **JUnit 5** (testes)
- **AssertJ** (assertions)

## 📦 Dependências Principais

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'commons-validator:commons-validator:1.9.0'
implementation 'com.fasterxml.uuid:java-uuid-generator:5.1.0'
implementation 'io.hypersistence:hypersistence-tsid:2.1.4'
runtimeOnly 'com.h2database:h2'
```

## 🚀 Como Executar

### Pré-requisitos
- Java 21 ou superior
- Gradle 8.0 ou superior

### Executando a Aplicação

1. **Clone o repositório**
```bash
git clone <repository-url>
cd algashop-ordering-main
```

2. **Execute a aplicação**
```bash
./gradlew bootRun
```

3. **Acesse a aplicação**
- Aplicação: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:~/ordering`
  - Username: `sa`
  - Password: `123`

> ⚠️ **Nota**: Como o projeto está em desenvolvimento, algumas funcionalidades podem não estar totalmente implementadas ou podem apresentar comportamentos inesperados.

## 🧪 Executando Testes

### Testes Unitários
```bash
./gradlew test
```

### Testes de Integração
```bash
./gradlew integrationTest
```

### Todos os Testes
```bash
./gradlew check
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/algaworks/algashop/ordering/
│   │   ├── domain/                    # Camada de Domínio
│   │   │   ├── model/
│   │   │   │   ├── entity/           # Entidades de domínio
│   │   │   │   ├── valueobject/      # Value Objects
│   │   │   │   ├── repository/       # Interfaces de repositório
│   │   │   │   ├── factory/          # Factories de domínio
│   │   │   │   ├── exception/        # Exceções de domínio
│   │   │   │   └── validator/        # Validadores
│   │   │   └── service/              # Serviços de domínio
│   │   └── infrastructure/           # Camada de Infraestrutura
│   │       └── persistence/          # Persistência JPA
│   └── resources/
│       └── application.yml           # Configurações
└── test/                             # Testes
    ├── java/                         # Testes unitários e integração
    └── resources/                    # Configurações de teste
```
