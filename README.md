# Sistema de Confeitaria (Java + Swing + PostgreSQL)

Aplicação desktop em Java (Swing) para gerenciar e comprar produtos de confeitaria.  
O sistema possui fluxo de **Admin (cadastro de produtos)** e fluxo de **Cliente (loja, carrinho, checkout, pedidos)**, persistindo dados em **PostgreSQL** via **JDBC**.

> **Observação de performance (primeira abertura)**  
> Ao executar, a **ViewHome pode demorar um pouco para abrir**, porque antes dela o sistema cria tabelas e executa o **seed**, incluindo a leitura do arquivo **CSV de áreas/bairros**.  
> Isso é proposital para garantir que **todos os bairros já existam no cadastro** assim que a tela de cadastro for aberta.

---

## Sumário

1. [Visão Geral](#visão-geral)  
2. [Tecnologias](#tecnologias)  
3. [Arquitetura (MVC)](#arquitetura-mvc)  
4. [Como Executar](#como-executar)  
   4.1 [Dependência do driver PostgreSQL (JAR manual)](#dependência-do-driver-postgresql-jar-manual)  
   4.2 [Configuração do banco (obrigatória)](#configuração-do-banco-obrigatória)  
   4.3 [Criando usuário/banco dedicados (recomendado)](#criando-usuáriobanco-dedicados-recomendado)  
5. [Modelo de Dados (Tabelas)](#modelo-de-dados-tabelas)  
6. [Fluxos do Sistema](#fluxos-do-sistema)  
   6.1 [Inicialização (criação de tabelas e seed)](#inicialização-criação-de-tabelas-e-seed)  
   6.2 [Home e Controle de Acesso](#home-e-controle-de-acesso)  
   6.3 [Admin: Cadastro de Produtos](#admin-cadastro-de-produtos)  
   6.4 [Cliente: Loja e Detalhe do Produto](#cliente-loja-e-detalhe-do-produto)  
   6.5 [Carrinho](#carrinho)  
   6.6 [Checkout (Entrega/Retirada + Taxa por Área)](#checkout-entregaretirada--taxa-por-área)  
   6.7 [Meus Pedidos](#meus-pedidos)    
7. [Seed de Áreas via CSV (Bairros)](#seed-de-áreas-via-csv-bairros)  
8. [Melhorias Futuras](#melhorias-futuras)

---

## Visão Geral

### Perfis e permissões

- **Cliente (usuário comum)**:
  - visualizar produtos (loja)
  - ver detalhes e adicionar ao carrinho
  - finalizar compra (entrega ou retirada)
  - visualizar pedidos realizados ("Meus pedidos")

- **Administrador (restrito por whitelist de e-mails)**:
  - cadastrar produtos
  - listar produtos
  - excluir produtos

O controle de acesso ao cadastro de produtos usa whitelist de e-mails no arquivo:
- [`EmailWhitelist.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/auth/EmailWhitelist.java)

---

## Tecnologias

- **Java** (aplicação desktop)
- **Swing** (UI)
- **PostgreSQL** (banco relacional)
- **JDBC** (acesso a dados)
- **Hash de senha** com PBKDF2 em:
  - [`EncryptionService.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/services/EncryptionService.java)

---

## Arquitetura (MVC)

O projeto segue o padrão **MVC**:

- **View (`view/`)**: telas Swing e interação com usuário  
- **Controller (`controller/`)**: coordena ações, valida regras e orquestra operações  
- **Model (`model/`)**:
  - `model.entities`: entidades
  - `model.repositories`: acesso a dados via JDBC/SQL

Outros pacotes relevantes:
- `app/`: sessão (`Session`) e carrinho (`CartSession`)
- `services/`: seed inicial e serviços auxiliares

---

## Como Executar

### Dependência do driver PostgreSQL (JAR manual)

Como o projeto usa dependências via JAR manual, é necessário adicionar o driver JDBC do PostgreSQL ao build path:

1. Baixe o `postgresql-xx.x.x.jar` (driver oficial)
2. No Eclipse:  
   **Project → Properties → Java Build Path → Libraries → Add External JARs...**
3. Se for exportar/running fora da IDE, garantir que o JAR esteja no classpath.

---

### Configuração do banco (obrigatória)

A classe:
- [`DBConnection.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/repositories/DBConnection.java)

lê primeiro variáveis de ambiente e, se não existirem, lê arquivo.

#### 1) Arquivo `db.properties`

O arquivo deve existir em:

- `confeitaria/src/db.properties`

> Importante: o caminho é relativo ao diretório de execução (working directory).  
> Em geral, execute o projeto com working directory sendo a pasta `confeitaria/`, para que `src/db.properties` seja encontrado.

Conteúdo esperado:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=confeitaria_db
DB_USER=confeitaria_user
DB_PASSWORD=confeitaria
```

#### 2) Variáveis de ambiente

Alternativamente, configure:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

---

### Criando usuário/banco dedicados (recomendado)

Execute no PostgreSQL (via pgAdmin Query Tool ou psql):

```sql
CREATE USER confeitaria_user WITH PASSWORD 'confeitaria';
CREATE DATABASE confeitaria_db OWNER confeitaria_user;
GRANT ALL PRIVILEGES ON DATABASE confeitaria_db TO confeitaria_user;
```

Se houver erro de permissão ao criar tabelas:

```sql
GRANT ALL ON SCHEMA public TO confeitaria_user;
```

---

## Modelo de Dados (Tabelas)

As tabelas são criadas automaticamente em runtime por:
- [`CreateTables.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/repositories/CreateTables.java)

### Ordem de criação

O método `createAllTables()` cria as tabelas em ordem respeitando chaves estrangeiras:

> area → address → person → flavor_level → flavor → size → user → product → order → order_items

### Tabelas principais do fluxo de venda

- `"order"`: pedido (cabeçalho)
  - `id_user`, `datetime`, `total_price`, `delivery`, `observations`

- `order_items`: itens do pedido
  - `id_order`, `id_product`, `quantity`, `price_at_moment`

> Por que `price_at_moment`?  
> Para registrar o preço no momento da compra, evitando que mudanças futuras de preço alterem pedidos antigos.

---

## Fluxos do Sistema

### Inicialização (criação de tabelas e seed)

A classe `Main` faz:
1. imprime working dir (debug)
2. cria as tabelas
3. executa seed (áreas, níveis e tamanhos)
4. abre a `ViewHome`

Arquivo:
- [`Main.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/app/Main.java)

Seed:
- [`SeedService.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/services/SeedService.java)

> Observação: por causa do seed de áreas via CSV, a primeira abertura pode demorar um pouco.

---

### Home e Controle de Acesso

Tela:
- [`ViewHome.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewHome.java)

Botões:
- Cadastrar
- Entrar
- Cadastro de Produtos (Admin)
- Comprar produtos

Regras:
- **Comprar produtos** exige login (`Session.isLoggedIn()`).
- **Admin** exige login e email presente na whitelist.

---

### Admin: Cadastro de Produtos

Tela:
- [`ViewProducts.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewProducts.java)

Funcionalidades:
- formulário para criar produto
- tabela para listar produtos
- exclusão de produto

---

### Cliente: Loja e Detalhe do Produto

#### Loja (listagem)
Tela:
- [`ViewShopProducts.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewShopProducts.java)

Ações:
- Atualizar
- Ver/Adicionar (abre detalhes)
- Carrinho
- Meus pedidos

#### Detalhe + quantidade
Tela:
- [`ViewProductDetails.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewProductDetails.java)

Regra de cálculo (preço unitário):
- `base_price + size.price + flavor_level.price`

---

### Carrinho

Tela:
- [`ViewCart.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewCart.java)

Responsabilidades:
- listar itens do carrinho (nome, qtd, unitário, total)
- calcular subtotal somando os itens
- remover item selecionado
- iniciar checkout

---

### Checkout (Entrega/Retirada + Taxa por Área)

Tela:
- [`ViewCheckout.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewCheckout.java)

Regras:
- exige login
- exige carrinho não vazio
- total = subtotal + taxaEntrega (somente se entrega)

#### Taxa por área (bairro)
A taxa é obtida do endereço do usuário:
- busca a pessoa pelo email do usuário logado (`RepositoryPerson.findByEmailPerson`)
- extrai `person.address.area.fee`

#### Persistência do pedido
- cria pedido e obtém id com `RepositoryOrder.createOrderAndReturnId`
- cria itens com `RepositoryOrderItems.createOrderItem`

---

### Meus Pedidos

Tela:
- [`ViewMyOrders.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/view/ViewMyOrders.java)

- tabela 1: lista pedidos do usuário logado
- tabela 2: lista itens do pedido selecionado

Modelos auxiliares:
- [`OrderSummary.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/entities/OrderSummary.java)
- [`OrderItemSummary.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/entities/OrderItemSummary.java)

---

## Seed de Áreas via CSV (Bairros)

Objetivo:
- garantir que todos os bairros/áreas estejam disponíveis no cadastro desde o início.

Como funciona:
- antes de abrir a `ViewHome`, o sistema executa o seed que **lê um arquivo CSV** com as áreas e insere no banco.

Onde configurar:
- arquivo CSV (ex.: `areas.csv`) deve existir conforme configurado no `SeedService`.

Dica:
- se você mudar o CSV, os novos bairros passam a aparecer nas próximas execuções (dependendo da estratégia de seed adotada).

---

## Melhorias Futuras

- Transação no checkout (salvar pedido + itens com commit/rollback único)
- Persistir carrinho no banco (para não perder ao fechar o app)
- Tela de gerenciamento de sabores (evitar duplicar sabores criados)
- Melhorar validações de formulário e mensagens de erro
- Logs e padronização de exceções