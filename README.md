# Modelo de Banco de Dados para Locadora de Veículos

## Esquema Relacional

### Tabela `clientes`
| Campo      | Tipo          | Restrições                           | Descrição                              |
|------------|---------------|--------------------------------------|----------------------------------------|
| id         | INT           | `AUTO_INCREMENT PRIMARY KEY`        | ID único do cliente                    |
| nome       | VARCHAR(100)  | `NOT NULL`                           | Nome completo                          |
| cpf        | CHAR(11)      | `UNIQUE NOT NULL`                    | CPF (apenas números)                   |
| cnh        | CHAR(11)      | `UNIQUE NOT NULL`                    | CNH (apenas números)                   |
| endereco   | VARCHAR(255)  | -                                    | Endereço completo                      |
| telefone   | VARCHAR(20)   | -                                    | Telefone de contato                    |
| email      | VARCHAR(100)  | -                                    | E-mail do cliente                      |

---

### Tabela `categorias`
| Campo | Tipo         | Restrições                    | Descrição               |
|-------|--------------|-------------------------------|-------------------------|
| id    | INT          | `AUTO_INCREMENT PRIMARY KEY` | ID único da categoria   |
| nome  | VARCHAR(50)  | `NOT NULL`                    | Nome da categoria (ex: econômico, SUV, luxo) |

---

### Tabela `veiculos`
| Campo           | Tipo                          | Restrições                           | Descrição                                   |
|-----------------|-------------------------------|--------------------------------------|---------------------------------------------|
| id              | INT                           | `AUTO_INCREMENT PRIMARY KEY`        | ID único do veículo                        |
| modelo          | VARCHAR(100)                  | `NOT NULL`                           | Modelo do veículo (ex: Onix, HB20)          |
| marca           | VARCHAR(100)                  | `NOT NULL`                           | Marca do veículo (ex: Chevrolet, Hyundai)   |
| placa           | VARCHAR(10)                   | `UNIQUE NOT NULL`                    | Placa do veículo                           |
| ano             | INT                           | `NOT NULL`                           | Ano de fabricação                          |
| quilometragem   | INT                           | `NOT NULL`                           | Quilometragem atual                        |
| cor             | VARCHAR(30)                   | -                                    | Cor predominante                           |
| status          | ENUM                          | `DEFAULT 'disponível'`               | Valores: `disponível`, `alugado`, `manutenção`, `reservado` |
| categoria_id    | INT                           | `FOREIGN KEY (categorias.id)`        | Categoria do veículo                       |

---

### Tabela `locacoes`
| Campo                     | Tipo         | Restrições                           | Descrição                              |
|---------------------------|--------------|--------------------------------------|----------------------------------------|
| id                        | INT          | `AUTO_INCREMENT PRIMARY KEY`        | ID único da locação                    |
| cliente_id                | INT          | `FOREIGN KEY (clientes.id) NOT NULL` | Cliente associado                      |
| veiculo_id                | INT          | `FOREIGN KEY (veiculos.id) NOT NULL` | Veículo alugado                        |
| data_retirada             | DATETIME     | `NOT NULL`                           | Data/hora de retirada                  |
| data_prevista_devolucao   | DATETIME     | `NOT NULL`                           | Data/hora prevista para devolução      |
| data_real_devolucao       | DATETIME     | -                                    | Data/hora real da devolução            |
| km_inicial                | INT          | `NOT NULL`                           | Quilometragem no início da locação     |
| km_final                  | INT          | -                                    | Quilometragem na devolução             |
| valor_total               | DECIMAL(10,2)| -                                    | Valor calculado automaticamente        |

---

### Tabela `pagamentos`
| Campo             | Tipo                     | Restrições                           | Descrição                              |
|-------------------|--------------------------|--------------------------------------|----------------------------------------|
| id                | INT                      | `AUTO_INCREMENT PRIMARY KEY`        | ID único do pagamento                  |
| locacao_id        | INT                      | `FOREIGN KEY (locacoes.id) NOT NULL` | Locação associada                      |
| valor             | DECIMAL(10,2)            | `NOT NULL`                           | Valor do pagamento                     |
| forma_pagamento   | ENUM                     | `NOT NULL`                           | Método: `crédito`, `débito`, `PIX`, `boleto` |
| status            | ENUM                     | `DEFAULT 'pendente'`                 | Valores: `pago`, `pendente`, `cancelado` |

---

## Relacionamentos
1. **Veículos → Categorias**:  
   `veiculos.categoria_id` referencia `categorias.id`.

2. **Locações → Clientes e Veículos**:  
   `locacoes.cliente_id` referencia `clientes.id`  
   `locacoes.veiculo_id` referencia `veiculos.id`.

3. **Pagamentos → Locações**:  
   `pagamentos.locacao_id` referencia `locacoes.id`.


# SQL

   CREATE DATABASE IF NOT EXISTS locadora;
USE locadora;

-- Tabela de Clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) UNIQUE NOT NULL,
    cnh CHAR(11) UNIQUE NOT NULL,
    endereco VARCHAR(255),
    telefone VARCHAR(20),
    email VARCHAR(100)
);

-- Tabela de Categorias de Veículos
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

-- Tabela de Veículos
CREATE TABLE veiculos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    placa VARCHAR(10) UNIQUE NOT NULL,
    ano INT NOT NULL,
    quilometragem INT NOT NULL,
    cor VARCHAR(30),
    status ENUM('disponível', 'alugado', 'manutenção', 'reservado') DEFAULT 'disponível',
    categoria_id INT,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Tabela de Locações
CREATE TABLE locacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    veiculo_id INT NOT NULL,
    data_retirada DATETIME NOT NULL,
    data_prevista_devolucao DATETIME NOT NULL,
    data_real_devolucao DATETIME,
    km_inicial INT NOT NULL,
    km_final INT,
    valor_total DECIMAL(10,2),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (veiculo_id) REFERENCES veiculos(id)
);

-- Tabela de Pagamentos
CREATE TABLE pagamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    locacao_id INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    forma_pagamento ENUM('crédito', 'débito', 'PIX', 'boleto') NOT NULL,
    status ENUM('pago', 'pendente', 'cancelado') DEFAULT 'pendente',
    FOREIGN KEY (locacao_id) REFERENCES locacoes(id)
);

