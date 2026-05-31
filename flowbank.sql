CREATE TABLE Usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    celular VARCHAR(20) NOT NULL,
    dataDeNascimento VARCHAR(20),
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE Transacao (
    id SERIAL PRIMARY KEY,
    usuarioId INT NOT NULL,
    transacao VARCHAR(50),
    descricao VARCHAR(255),
    valor DECIMAL(10, 2) NOT NULL,
    data VARCHAR(20),
    hora VARCHAR(20),

    FOREIGN KEY (usuarioId) REFERENCES Usuario(id)
);
   ALTER TABLE Usuario ADD COLUMN saldo DECIMAL(10,2) DEFAULT 0.00;

    