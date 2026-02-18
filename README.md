# Sistema de Confeitaria (Java + Swing + PostgreSQL)

Aplicação desktop em Java (Swing) para gerenciar e comprar produtos de confeitaria.  
O sistema possui fluxo de **Admin (cadastro de produtos)** e fluxo de **Cliente (loja, carrinho, checkout, pedidos)**, persistindo dados em **PostgreSQL** via **JDBC**.

---

## Sumário

1. [Visão Geral](#visão-geral)  
2. [Tecnologias](#tecnologias)  
3. [Arquitetura e Padrões](#arquitetura-e-padrões)  
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
7. [Documentação UML (Astah)](#documentação-uml-astah)  
   7.1 [Casos de Uso](#71-diagrama-de-casos-de-uso)  
   7.2 [Diagrama de Classes](#72-diagrama-de-classes)  
   7.3 [Diagramas de Sequência](#73-diagramas-de-sequência)  
8. [Checklist de Prints (Evidências)](#checklist-de-prints-evidências)  
9. [Melhorias Futuras](#melhorias-futuras)  

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

O controle de acesso ao cadastro de produtos usa whitelist de e-mails no arquivo [`EmailWhitelist.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/auth/EmailWhitelist.java#L1-L20).

---

## Tecnologias

- **Java** (aplicação desktop)
- **Swing** (UI)
- **PostgreSQL** (banco relacional)
- **JDBC** (acesso a dados)
- **Hash de senha** com PBKDF2 em [`EncryptionService.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/services/EncryptionService.java)

---

## Arquitetura e Padrões

A aplicação organiza o código em camadas/pacotes:

- `view`: telas Swing (UI)
- `model.entities`: entidades (representação dos dados em objetos)
- `model.repositories`: acesso a dados e SQL (padrão **Repository**)
- `app`: utilitários de aplicação (sessão, carrinho, main)
- `services`: serviços transversais (seed inicial, criptografia)

### Padrão Repository (Acesso a dados)
Repositórios encapsulam SQL e operações JDBC.
Ex.: `RepositoryProduct` centraliza `INSERT/DELETE/SELECT` de produto e faz JOIN para retornar objetos completos:
- [`RepositoryProduct.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/RepositoryProduct.java)

### Sessão
A sessão do usuário logado é mantida em memória (variável `static`) em:
- [`Session.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/app/Session.java)

Isso permite que qualquer tela valide login com `Session.isLoggedIn()` e recupere dados com `Session.getLoggedUser()`.

### Carrinho
O carrinho também é mantido em memória:
- [`CartSession.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/app/CartSession.java)

Estrutura: `Map<Integer, Integer>` (productId → quantidade).  
Justificativa: facilita somar quantidades do mesmo produto e consultar rapidamente o que está no carrinho.

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

O sistema foi configurado para **falhar com mensagem clara** se não existir configuração.  
A classe [`DBConnection.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/DBConnection.java) lê primeiro variáveis de ambiente e, se não existirem, lê arquivo.

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

As tabelas são criadas automaticamente em runtime por [`CreateTables.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/CreateTables.java).

### Ordem de criação
O método `createAllTables()` cria as tabelas em ordem respeitando chaves estrangeiras:

> area → address → person → flavor_level → flavor → size → user → product → order → order_items  
(ver comentário no início do método: [`CreateTables.java#L9-L24`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/CreateTables.java#L9-L24))

### Tabelas principais do fluxo de venda

- `"order"`: pedido (cabeçalho)
  - `id_user`, `datetime`, `total_price`, `delivery`, `observations`
  - criada em: [`CreateTables.java#L143-L168`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/CreateTables.java#L143-L168)

- `order_items`: itens do pedido
  - `id_order`, `id_product`, `quantity`, `price_at_moment`
  - criada em: [`CreateTables.java#L197-L222`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/CreateTables.java#L197-L222)

> Por que `price_at_moment`?  
> Para registrar o preço no momento da compra, evitando que mudanças futuras de preço alterem pedidos antigos.

---

## Fluxos do Sistema

### Inicialização (criação de tabelas e seed)

A classe `Main` faz:
1. imprime working dir (debug)
2. cria as tabelas
3. injeta seeds (áreas, níveis e tamanhos)
4. abre a `ViewHome`

Arquivo: [`Main.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/app/Main.java)

O seed inicial é feito em [`SeedService.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/services/SeedService.java).

---

### Home e Controle de Acesso

Tela: [`ViewHome.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewHome.java)

- Botões:
  - Cadastrar
  - Entrar
  - Cadastro de Produtos (Admin)
  - Comprar produtos

Regras:
- **Comprar produtos** exige login (`Session.isLoggedIn()`).
- **Admin** exige login e email presente na whitelist.

---

### Admin: Cadastro de Produtos

Tela: [`ViewProducts.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewProducts.java)

Funcionalidades:
- formulário para criar produto
- tabela para listar produtos
- exclusão de produto

Decisão importante (documentável):
- Ao salvar um produto, a tela **sempre cria um novo sabor** (`Flavor`) com o nível selecionado e então cria o `Product` apontando para esse `Flavor` e para o `Size` escolhido (ver comentário “Opção A” em [`ViewProducts.java#L19-L24`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewProducts.java#L19-L24)).

---

### Cliente: Loja e Detalhe do Produto

#### Loja (listagem)
Tela: [`ViewShopProducts.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewShopProducts.java)

- carrega produtos via `RepositoryProduct.findAllProduct()`
- mostra na tabela:
  - Produto, Sabor, Nível, Tamanho, Preço base
- ações:
  - Atualizar
  - Ver/Adicionar (abre detalhes)
  - Carrinho
  - Meus pedidos

#### Detalhe + quantidade
Tela: [`ViewProductDetails.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewProductDetails.java)

- calcula preço unitário como:
  - `base_price + size.price + flavor_level.price`  
  (ver `computeUnitPrice`: [`ViewProductDetails.java#L97-L114`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewProductDetails.java#L97-L114))
- adiciona ao carrinho: `CartSession.add(product, qty)`

---

### Carrinho

Tela: [`ViewCart.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewCart.java)

Responsabilidades:
- listar itens do carrinho (nome, qtd, unitário, total)
- calcular subtotal somando os itens
- remover item selecionado
- iniciar checkout

Decisão técnica:
- A tela recarrega o `Product` do banco via `RepositoryProduct.findByIdProduct(productId)` para garantir dados completos do produto (incluindo size e flavor_level para cálculo do unitário).

---

### Checkout (Entrega/Retirada + Taxa por Área)

Tela: [`ViewCheckout.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewCheckout.java)

Regras:
- exige login
- exige carrinho não vazio
- total = subtotal + taxaEntrega (somente se entrega)

#### Taxa por área (bairro)
A taxa é obtida do endereço do usuário:
- busca a pessoa pelo email do usuário logado (`RepositoryPerson.findByEmailPerson`)
- extrai `person.address.area.fee`
(ver método `computeDeliveryFeeFromUserArea`: [`ViewCheckout.java#L213-L228`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewCheckout.java#L213-L228)).

#### Persistência do pedido
- cria pedido e obtém id com `RepositoryOrder.createOrderAndReturnId`:
  - [`RepositoryOrder.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/RepositoryOrder.java)
- cria itens com `RepositoryOrderItems.createOrderItem`:
  - [`RepositoryOrderItems.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/RepositoryOrderItems.java)

---

### Meus Pedidos

Tela: [`ViewMyOrders.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/view/ViewMyOrders.java)

- tabela 1: lista pedidos do usuário logado
- tabela 2: lista itens do pedido selecionado

Repositório de consulta:
- [`RepositoryMyOrders.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/798366cdbbe6787a31b6db7859f11641ef19657e/confeitaria/src/model/repositories/RepositoryMyOrders.java)

Modelos auxiliares (DTO/Resumo):
- [`OrderSummary.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/entities/OrderSummary.java)
- [`OrderItemSummary.java`](https://github.com/GiovannahCosta/MyFirstSoftware/blob/main/confeitaria/src/model/entities/OrderItemSummary.java)

---

## Documentação UML (Astah)

### 7.1 Diagrama de Casos de Uso

**Atores**
- Cliente
- Administrador

**Casos de uso (Cliente)**
- Visualizar produtos
- Ver detalhes do produto
- Adicionar ao carrinho
- Remover item do carrinho
- Finalizar compra
  - <<include>> Calcular subtotal
  - <<include>> Calcular taxa por área (se entrega)
  - <<include>> Salvar pedido
  - <<include>> Salvar itens do pedido
- Ver meus pedidos
  - <<include>> Ver itens do pedido

**Casos de uso (Admin)**
- Cadastrar produto
- Listar produtos
- Excluir produto

**Observação de permissão**
- “Acessar cadastro de produtos” é permitido apenas se o email estiver na whitelist.

---

### 7.2 Diagrama de Classes

Inclua pelo menos:

**app**
- `Main`
- `Session`
- `CartSession`

**entities**
- `User`, `Person`, `Address`, `Area`
- `Product`, `Flavor`, `FlavorLevel`, `Size`
- `OrderSummary`, `OrderItemSummary` (opcional, pois são DTOs)

**repositories**
- `DBConnection`, `CreateTables`
- `RepositoryProduct`, `RepositoryPerson`
- `RepositoryOrder`, `RepositoryOrderItems`, `RepositoryMyOrders`

**views**
- `ViewHome`, `ViewProducts`, `ViewShopProducts`, `ViewProductDetails`, `ViewCart`, `ViewCheckout`, `ViewMyOrders`

**Relações**
- `Person` 1..1 `Address`
- `Address` *..1 `Area`
- `Product` *..1 `Flavor`
- `Flavor` *..1 `FlavorLevel`
- `Product` *..1 `Size`

---

### 7.3 Diagramas de Sequência

Faça ao menos 3:

1) **Checkout / Confirmar pedido**
- Cliente → `ViewCheckout.onConfirm()`
- `RepositoryOrder.createOrderAndReturnId()`
- loop itens:
  - `RepositoryProduct.findByIdProduct()`
  - `RepositoryOrderItems.createOrderItem()`
- `CartSession.clear()`

2) **Adicionar ao carrinho**
- Cliente → `ViewProductDetails.onAdd()` → `CartSession.add()`

3) **Meus pedidos**
- Cliente → `ViewMyOrders.loadOrders()` → `RepositoryMyOrders.findOrdersByUser()`
- Cliente seleciona pedido → `loadItems()` → `RepositoryMyOrders.findItemsByOrder()`

---

## Checklist de Prints (Evidências)

### Banco e configuração
- `db.properties` (ocultando senha, se necessário)
- pgAdmin mostrando banco e tabelas criadas

### Fluxo do Cliente
- Home
- Login (sucesso)
- Loja (lista produtos)
- Detalhe do produto com quantidade e total
- Carrinho com subtotal
- Checkout:
  - entrega selecionada e taxa aparecendo
  - retirada selecionada e taxa zerada
- Mensagem “Pedido confirmado”
- Meus pedidos com:
  - lista de pedidos
  - itens do pedido selecionado

### Fluxo do Admin
- acesso ao cadastro de produtos (com email permitido)
- criação de produto
- listagem após criação
- exclusão de produto

### Evidência no banco
- `SELECT * FROM "order";`
- `SELECT * FROM order_items;`
- query join (opcional) mostrando produtos do pedido

---

## Melhorias Futuras

- Transação no checkout (salvar pedido + itens com commit/rollback único)
- Persistir carrinho no banco (para não perder ao fechar o app)
- Tela de gerenciamento de sabores (evitar duplicar sabores criados)
- Melhorar validações de formulário e mensagens de erro
- Logs e padronização de exceções