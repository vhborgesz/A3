CREATE DATABASE flowbank; 
USE flowbank; 

CREATE TABLE usuarios (
	id INT AUTO_INCREMENT PRIMARY KEY, 
	nome VARCHAR(100) NOT NULL,
    email VARCHAR (100) NOT NULL UNIQUE, 
    celular VARCHAR(20) NOT NULL, 
    data_nascimento DATE,
    senha VARCHAR(255) NOT NULL
    ); 
    
    CREATE TABLE transacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL, 
    tipo ENUM('ENTRADA', 'SAIDA'),
    descricao VARCHAR(255),
    valor DECIMAL(10, 2) NOT NULL, 
    data_transacao TIMESTAMP
    DEFAULT CURRENT_TIMESTAMP, 
    
    FOREIGN KEY (usuario_id) 
    REFERENCES usuarios(id) 
    );
    

    