-- ============================================================
-- Sistema Bancário — Script de Inicialização
-- Banco: banco_db
-- ============================================================
-- Senhas: todos os usuários têm a senha  ->  senha123
-- ============================================================
-- ------------------------------------------------------------
-- Tabelas
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS cliente (
    id    BIGSERIAL    PRIMARY KEY,
    nome  VARCHAR(255) NOT NULL,
    cpf   VARCHAR(14)  NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role  VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE'
    );

CREATE TABLE IF NOT EXISTS conta (
    id         BIGSERIAL      PRIMARY KEY,
    numero     VARCHAR(20)    NOT NULL UNIQUE,
    tipo       VARCHAR(20)    NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA', 'ELETRONICA')),
    cliente_id BIGINT         NOT NULL REFERENCES cliente(id)
    );

CREATE TABLE IF NOT EXISTS transacao (
    id               BIGSERIAL      PRIMARY KEY,
    tipo             VARCHAR(25)    NOT NULL CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')),
    valor            DECIMAL(15, 2) NOT NULL,
    data_hora        TIMESTAMP      NOT NULL DEFAULT NOW(),
    conta_destino_id BIGINT,              -- nullable, sem NOT NULL
    conta_origem_id  BIGINT,              -- nullable, sem NOT NULL
    CONSTRAINT fk_destino FOREIGN KEY (conta_destino_id) REFERENCES conta(id),
    CONSTRAINT fk_origem  FOREIGN KEY (conta_origem_id)  REFERENCES conta(id)
    );

DROP TABLE IF EXISTS view_saldo;

CREATE VIEW view_saldo AS
SELECT
    c.id,
    c.numero,
    c.tipo,
    COALESCE(SUM(
                     CASE
                         WHEN t.tipo = 'DEPOSITO'      AND t.conta_destino_id = c.id THEN  t.valor
                         WHEN t.tipo = 'SAQUE'         AND t.conta_origem_id  = c.id THEN  t.valor   -- já negativo
                         WHEN t.tipo = 'TRANSFERENCIA' AND t.conta_destino_id = c.id THEN  t.valor   -- crédito
                         WHEN t.tipo = 'TRANSFERENCIA' AND t.conta_origem_id  = c.id THEN -t.valor   -- débito
                         ELSE 0
                         END
             ), 0) AS saldo
FROM conta c
         LEFT JOIN transacao t
                   ON c.id = t.conta_origem_id
                       OR c.id = t.conta_destino_id
GROUP BY c.id, c.numero, c.tipo
ORDER BY c.id;

-- ------------------------------------------------------------
-- Índices
-- ------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_conta_cliente    ON conta     (cliente_id);
CREATE INDEX IF NOT EXISTS idx_trans_origem     ON transacao (conta_origem_id);
CREATE INDEX IF NOT EXISTS idx_trans_destino    ON transacao (conta_destino_id);
CREATE INDEX IF NOT EXISTS idx_trans_data_hora  ON transacao (data_hora);
CREATE INDEX IF NOT EXISTS idx_trans_tipo       ON transacao (tipo);

-- ------------------------------------------------------------
-- Dados de exemplo
-- ------------------------------------------------------------

INSERT INTO cliente (nome, cpf, email, senha, role)
VALUES
    ('Alice Silva', '000.000.000-01', 'alice.silva@bancada.com.br', '$2a$10$yE2csTomDi2KbiYnpmH7ouebPir/eJL1leG1nOuk35ZPGmH8I9WXy', 'GERENTE'),
    ('Carlos Oliveira', '000.000.000-03', 'carlos.oliveira@bancada.com.br', '$2a$10$yE2csTomDi2KbiYnpmH7ouebPir/eJL1leG1nOuk35ZPGmH8I9WXy', 'CLIENTE'),
    ('Diana Souza', '000.000.000-04', 'diana.souza@bancada.com.br', '$2a$10$yE2csTomDi2KbiYnpmH7ouebPir/eJL1leG1nOuk35ZPGmH8I9WXy', 'CLIENTE'),
    ('Eduardo Ferreira', '000.000.000-05', 'eduardo.ferreira@bancada.com.br', '$2a$10$yE2csTomDi2KbiYnpmH7ouebPir/eJL1leG1nOuk35ZPGmH8I9WXy', 'CLIENTE');

INSERT INTO conta (numero, tipo, cliente_id)
VALUES
    ('0001-1', 'CORRENTE', 2),
    ('0002-2', 'POUPANCA', 3),
    ('0003-3', 'ELETRONICA', 4);

INSERT INTO transacao (tipo, valor, data_hora, conta_origem_id, conta_destino_id)
VALUES
    -- Depósitos iniciais
    ('DEPOSITO', 500.00, NOW(), NULL, 1),
    ('DEPOSITO', 800.00, NOW(), NULL, 2),
    -- Saques iniciais
    ('SAQUE', -120.00, NOW(), 1, NULL),
    ('SAQUE', -80.00, NOW(), 2, NULL),
    -- Transferências (valor positivo, de conta_origem → conta_destino)
    ('TRANSFERENCIA', 20.00, NOW(), 1, 2),
    ('TRANSFERENCIA', 90.00, NOW(), 2, 3);
