create database estacionamento_db;

use estacionamento_db;
-- drop database estacionamento_db;

CREATE TABLE Patio(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    CapacidadeCarro INT NOT NULL,
	CapacidadeMoto INT NOT NULL,
	CapacidadeCaminhao INT NOT NULL
);

CREATE TABLE Vaga(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    Tipo VARCHAR(20) NOT NULL,
    StatusVaga ENUM('DISPONIVEL', 'INDISPONIVEL', 'EM MANUTENCAO', 'RESERVADA') NOT NULL,
    PatioId INT,
    
    CONSTRAINT fk_patio_v FOREIGN KEY (PatioId) REFERENCES Patio(Id)
);

CREATE TABLE Usuario(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    CPF VARCHAR(11) NOT NULL UNIQUE,
    Nome VARCHAR(100) NOT NULL,
    Sexo ENUM('MASCULINO', 'FEMININO'),
    DataNascimento DATE,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Telefone VARCHAR(14), -- (XX)XXXXX-XXXX
    Senha VARCHAR(100) NOT NULL
);

CREATE TABLE Funcionario(
	Id INT PRIMARY KEY,
    
    CONSTRAINT fk_usuario_f FOREIGN KEY (Id) REFERENCES Usuario(Id)
);

CREATE TABLE GERENTE(
	Id INT PRIMARY KEY,
    
    CONSTRAINT fk_funcionario FOREIGN KEY (Id) REFERENCES Funcionario(Id)
);

CREATE TABLE Cliente(
	Id INT PRIMARY KEY,
    Mensalista BOOLEAN NOT NULL,
    
    CONSTRAINT fk_usuario_c FOREIGN KEY (Id) REFERENCES Usuario(Id)
);

CREATE TABLE Veiculo(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    Placa VARCHAR(7) NOT NULL UNIQUE,
    Modelo VARCHAR(50) NOT NULL,
    Cor VARCHAR(15) NOT NULL,
    MotoristaPrincipal INT,
    TipoVeiculo ENUM('CARRO', 'MOTO', 'CAMINHAO') NOT NULL,
    
    CONSTRAINT fk_cliente_v FOREIGN KEY (MotoristaPrincipal) REFERENCES Cliente(Id)
);

-- Tabela Associativa:
CREATE TABLE VeiculoCliente(
	ClienteId INT,
    VeiculoId INT,
    
    PRIMARY KEY(VeiculoId, ClienteId),
    
    CONSTRAINT fk_cliente FOREIGN KEY (ClienteId) REFERENCES Cliente(Id),
    CONSTRAINT fk_veiculo FOREIGN KEY (VeiculoId) REFERENCES Veiculo(Id)
);

CREATE TABLE Reserva(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    HorarioEntrada TIMESTAMP NOT NULL,
    HorarioSaida TIMESTAMP NOT NULL,
    Valor DECIMAL(10,2),
    StatusReserva ENUM('ATIVA', 'CANCELADA', 'FINALIZADA', 'EXPIRADA') NOT NULL,
    PatioId INT,
    VeiculoId INT,
    
    CONSTRAINT fk_patio_r FOREIGN KEY (PatioId) REFERENCES Patio(Id),
    CONSTRAINT fk_veiculo_r FOREIGN KEY (VeiculoId) REFERENCES Veiculo(Id)
);

CREATE TABLE RegistroEstadia(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    HorarioEntradaReal TIMESTAMP NOT NULL,
    HorarioSaidaReal TIMESTAMP DEFAULT NULL,
    ReservaId INT,
    
    CONSTRAINT fk_reserva_r FOREIGN KEY (ReservaId) REFERENCES Reserva(Id)
);

CREATE TABLE Multa(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    Valor DECIMAL(10,2) NOT NULL,
    Motivo VARCHAR(500) NOT NULL,
    StatusMulta ENUM('NAO-PAGO', 'PAGO', 'RETIRADO'),
    EstadiaRelacionada INT,
    
    CONSTRAINT fk_estadia_m FOREIGN KEY (EstadiaRelacionada) REFERENCES RegistroEstadia(Id)
);

CREATE TABLE Reclamacao(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    Conteudo VARCHAR(2000) NOT NULL,
    StatusReclamacao ENUM('EM ANALISE', 'RESOLVIDA', 'RECUSADA') NOT NULL,
    EstadiaRelacionada INT,
    
    CONSTRAINT fk_estadia_r FOREIGN KEY (EstadiaRelacionada) REFERENCES RegistroEstadia(Id)
);

CREATE TABLE RelatorioMensal(
	Id INT PRIMARY KEY AUTO_INCREMENT,
    HorarioGerado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Ganhos DECIMAL(10,2) NOT NULL,
    QntdClientesCarro INT NOT NULL,
    QntdClientesMoto INT NOT NULL,
    QntClientesCaminhao INT NOT NULL,
    TempoMedioEstadia DECIMAL(10,2),
    ReclamacoesRegistradas INT NOT NULL,
    MultasAplicadas INT NOT NULL
);


-- Procedures 

DELIMITER //
CREATE PROCEDURE CriarRelatorioMensal()

BEGIN 
	DECLARE v_ganho DECIMAL(10,2);
    DECLARE v_qntdCarro INT;
    DECLARE v_qntdMoto INT;
    DECLARE v_qntdCaminhao INT;
    DECLARE v_tempoMedio DECIMAL(10,2);
    DECLARE v_reclamacoes INT;
    DECLARE v_multas INT;
    
	SELECT SUM(Valor) INTO v_ganho FROM Reserva 
    WHERE MONTH(HorarioEntrada) = MONTH(CURRENT_DATE()) 
    AND YEAR(HorarioEntrada) = YEAR(CURRENT_DATE());
    
    SELECT COUNT(*) INTO v_qntdCarro FROM Reserva 
    INNER JOIN Veiculo ON Reserva.VeiculoId = Veiculo.Id 
    WHERE Veiculo.TipoVeiculo = 'CARRO' AND 
    MONTH(Reserva.HorarioEntrada) = MONTH(CURRENT_DATE()) 
    AND YEAR(Reserva.HorarioEntrada) = YEAR(CURRENT_DATE());
    
    SELECT COUNT(*) INTO v_qntdMoto FROM Reserva 
    INNER JOIN Veiculo ON Reserva.VeiculoId = Veiculo.Id 
    WHERE Veiculo.TipoVeiculo = 'MOTO' AND 
    MONTH(Reserva.HorarioEntrada) = MONTH(CURRENT_DATE()) 
    AND YEAR(Reserva.HorarioEntrada) = YEAR(CURRENT_DATE());
    
    SELECT COUNT(*) INTO v_qntdCaminhao FROM Reserva 
    INNER JOIN Veiculo ON Reserva.VeiculoId = Veiculo.Id 
    WHERE Veiculo.TipoVeiculo = 'CAMINHAO' AND 
    MONTH(Reserva.HorarioEntrada) = MONTH(CURRENT_DATE()) 
    AND YEAR(Reserva.HorarioEntrada) = YEAR(CURRENT_DATE());
    
	SELECT AVG(TIMESTAMPDIFF(
		HOUR,
		HorarioEntradaReal,
		HorarioSaidaReal
        ))
    INTO v_tempoMedio
    FROM RegistroEstadia
    WHERE HorarioSaidaReal IS NOT NULL AND
    MONTH(HorarioEntradaReal) = MONTH(CURRENT_DATE()) 
    AND YEAR(HorarioEntradaReal) = YEAR(CURRENT_DATE());
    
    SELECT COUNT(*) INTO v_reclamacoes FROM Reclamacao R
    INNER JOIN RegistroEstadia E ON R.EstadiaRelacionada = E.Id
    WHERE MONTH(E.HorarioEntradaReal) = MONTH(CURRENT_DATE()) 
    AND YEAR(E.HorarioEntradaReal) = YEAR(CURRENT_DATE());
    
	SELECT COUNT(*) INTO v_multas FROM Multa M
    INNER JOIN RegistroEstadia E ON M.EstadiaRelacionada = E.Id
    WHERE MONTH(E.HorarioEntradaReal) = MONTH(CURRENT_DATE()) 
    AND YEAR(E.HorarioEntradaReal) = YEAR(CURRENT_DATE());
    
    -- Insert
	INSERT INTO RelatorioMensal(
        Ganhos,
        QntdClientesCarro,
        QntdClientesMoto,
        QntClientesCaminhao,
        TempoMedioEstadia,
        ReclamacoesRegistradas,
        MultasAplicadas
    )
    VALUES(
        COALESCE(v_ganho, 0),
        v_qntdCarro,
        v_qntdMoto,
        v_qntdCaminhao,
        COALESCE(v_tempoMedio, 0),
        v_reclamacoes,
        v_multas
    );
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE GetReservasCliente(
	IN p_clienteId INT)

BEGIN 
    
	SELECT * FROM Reserva R
    INNER JOIN Veiculo V ON R.VeiculoId = V.Id
    WHERE V.MotoristaPrincipal = p_clienteId;
END //

DELIMITER ;

-- Reclamação
DELIMITER //

CREATE PROCEDURE AdicionarReclamacao(
	IN p_Conteudo VARCHAR(2000),
    IN p_Estadia INT)
    
BEGIN

	INSERT INTO Reclamacao(Conteudo, StatusReclamacao, EstadiaRelacionada) VALUES(p_Conteudo, 1, p_Estadia);

END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE ListarReclamacoes(
    IN p_Estadia INT)
    
BEGIN

	SELECT * FROM Reclamacao WHERE EstadiaRelacionada = p_Estadia;

END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE UpdateReclamacaoStatus(
    IN p_ReclamacaoId INT,
    IN p_ReclamacaoStatus ENUM('EM ANALISE', 'RESOLVIDA', 'RECUSADA'))
    
BEGIN

	UPDATE Reclamacao
    SET StatusReclamacao = p_ReclamacaoStatus
    WHERE Id = p_ReclamacaoId;

END //

DELIMITER ;
-- Reserva
DELIMITER //

CREATE PROCEDURE adicionarReserva(
    IN p_HoraEntrada TIMESTAMP,
    IN p_HoraSaida TIMESTAMP,
    IN p_ValorReserva DECIMAL(10,2),
	IN p_VeiculoId INT,
    IN p_PatioId INT)
    
    /*	Id INT PRIMARY KEY AUTO_INCREMENT,
    HorarioEntrada TIMESTAMP NOT NULL,
    HorarioSaida TIMESTAMP NOT NULL,
    Valor DECIMAL(10,2),
    StatusReserva ENUM('ATIVA', 'CANCELADA', 'FINALIZADA', 'EXPIRADA') NOT NULL,
    PatioId INT,
    VeiculoId INT,*/
    
BEGIN
	INSERT INTO Reserva(HorarioEntrada, HorarioSaida, Valor, StatusReserva, ParioId, VeiculoId) 
    VALUES(p_HoraEntrada, p_HoraSaida, p_ValorReserva, 1, p_PatioId, p_VeiculoId);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE CancelarReserva(
	IN p_ReservaId INT)

BEGIN
	UPDATE Reserva
    SET StatusReserva = 2 
    WHERE Id = p_ReservaId;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE ListarReserva(
	IN p_ReservaId INT)

BEGIN
	SELECT * FROM Reserva WHERE Id = p_ReservaId;
END //

DELIMITER ;

-- Multa

DELIMITER //

CREATE PROCEDURE AdicionarMulta(
	IN p_Valor DECIMAL(10,2),
    IN p_Motivo VARCHAR(500),
	IN p_EstadiaId INT)
    
BEGIN 

    INSERT INTO Multa(Valor, Multa, StatusMulta, EstadiaRelacionada) VALUES (p_Valor, p_Motivo, 1, p_EstadiaId);
    
END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE UpdateMulta(
	IN p_MultaId INT,
    IN p_StatusMulta INT)
    
BEGIN 

    UPDATE Multa
    SET StatusMulta = p_StatusMulta WHERE Id = p_MultaId;
    
END //
DELIMITER ;

-- Veiculo

DELIMITER //

CREATE PROCEDURE AdicionarVeiculo(
	IN p_Placa VARCHAR(7),
    IN p_Modelo VARCHAR(50),
    IN p_Cor VARCHAR(15),
    IN p_MotoristaPrinc INT,
    IN p_TipoVeiculo ENUM('CARRO', 'MOTO', 'CAMINHAO'))
    
BEGIN
	
	INSERT INTO Veiculo(Placa, Modelo, Cor, MotoristaPrincipal, TipoVeiculo) VALUES (p_Placa, p_Modelo, p_Cor, p_MotoristaPrinc, p_TipoVeiculo);
        
END //
DELIMITER ;
    
DELIMITER //
    
CREATE PROCEDURE RemoverVeiculo (
	IN p_VeiculoId INT)
        
BEGIN
	
    DELETE FROM Veiculo WHERE Id = p_VeiculoId;
    
END //
DELIMITER;

DELIMITER //

CREATE PROCEDURE AdicionaMotorista(
	IN p_ClienteId INT,
    IN p_VeiculoId INT)

BEGIN 

	INSERT INTO VeiculoCliente(ClienteID, VeiculoId) VALUES (p_ClienteId, p_VeiculoId);

END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE UpdateVeiculo(
	IN p_VeiculoId INT,
    IN p_Modelo VARCHAR(50),
    IN p_Cor VARCHAR(15),
    IN p_MotoristaPrinc INT,
    IN p_TipoVeiculo ENUM('CARRO', 'MOTO', 'CAMINHAO'))

BEGIN

	UPDATE Veiculo
    SET Modelo = p_Modelo,
		Cor = p_Cor,
        MotoristaPrincipal = p_MotoristaPrinc,
        TipoVeiculo = p_TipoVeiculo 
	WHERE Id = p_VeiculoId;

END //
DELIMITER ;

-- USUARIO

DELIMITER // 

CREATE PROCEDURE AdicionarUsuario(
    IN p_CPF VARCHAR(11),
    IN p_Nome VARCHAR(100),
    IN P_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100))
    
BEGIN

	INSERT INTO Usuario(CPF, Nome, Sexo, DataNascimento, Email, Telefone, Senha) VALUES (p_CPF, p_Nome, p_Sexo, p_DataNascimento, p_Email, p_Telefone, p_Senha);
    
END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE UpdateUsuario(
	IN p_IdUsuario INT,
	IN p_Nome VARCHAR(100),
    IN P_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100))
    
BEGIN

	UPDATE Usuario
	SET Nome = p_Nome,
		Email = p_Email,
        Telefone = p_Telefone,
        Senha = p_Senha
	WHERE Id = p_IdUsuario;

END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE RemoveUsuario(
	IN p_IdUsuario INT)
    
BEGIN

	DELETE FROM Usuario WHERE Id = p_IdUsuario;

END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE AdicionaCliente(
	IN p_CPF VARCHAR(11),
    IN p_Nome VARCHAR(100),
    IN P_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100),
    IN p_Mensalista BOOLEAN)
    
BEGIN 

	DECLARE p_UsuarioId INT;
    CALL AdicionarUsuario(p_Nome, p_Cpf, p_Telefone, p_Sexo, p_DataNascimento, p_Email, p_Senha);
    SET v_UsuarioId = LAST_INSERT_ID();
    INSERT INTO Cliente (Id, Mensalista) VALUES (v_UsuarioId, p_Mensalista);
    
END //
DELIMITER ;

DELIMITER //

CREATE PROCEDURE UpdateCliente(
	IN p_IdCliente INT,
    IN p_Mensalista BOOLEAN)
    
BEGIN
	
    UPDATE Cliente
	SET Mensalista = p_Mensalista WHERE Id = p_IdCliente;

END //
DELIMITER ;
        