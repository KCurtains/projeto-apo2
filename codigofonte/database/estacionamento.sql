CREATE DATABASE estacionamento_db;
USE estacionamento_db;

SET GLOBAL event_scheduler = ON;
-- TABELAS

CREATE TABLE Patio(
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Endereco VARCHAR(200),
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
    HorarioEntrada DATETIME NOT NULL,
    HorarioSaida DATETIME NOT NULL,
    Valor DECIMAL(10,2),
    StatusReserva ENUM('ATIVA', 'CANCELADA', 'FINALIZADA', 'EXPIRADA') NOT NULL,
    PatioId INT,
    VeiculoId INT,
    VagaId INT,
    
    CONSTRAINT fk_patio_r FOREIGN KEY (PatioId) REFERENCES Patio(Id),
    CONSTRAINT fk_veiculo_r FOREIGN KEY (VeiculoId) REFERENCES Veiculo(Id),
    CONSTRAINT fk_vaga_r FOREIGN KEY (VagaId) REFERENCES Vaga(Id)
);

CREATE TABLE RegistroEstadia(
    Id INT PRIMARY KEY AUTO_INCREMENT,
    HorarioEntradaReal DATETIME NOT NULL,
    HorarioSaidaReal DATETIME DEFAULT NULL,
    ReservaId INT,
    
    CONSTRAINT fk_reserva_r FOREIGN KEY (ReservaId) REFERENCES Reserva(Id)
);

CREATE TABLE Multa(
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Valor DECIMAL(10,2) NOT NULL,
    Motivo VARCHAR(500) NOT NULL,
    StatusMulta ENUM('NAO_PAGO', 'PAGO', 'RETIRADO'),
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
    HorarioGerado DATETIME DEFAULT CURRENT_TIMESTAMP,
    Ganhos DECIMAL(10,2) NOT NULL,
    QntdClientesCarro INT NOT NULL,
    QntdClientesMoto INT NOT NULL,
    QntClientesCaminhao INT NOT NULL,
    TempoMedioEstadia DECIMAL(10,2),
    ReclamacoesRegistradas INT NOT NULL,
    MultasAplicadas INT NOT NULL
);

CREATE TABLE ValidacaoToken (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_expiracao DATETIME NOT NULL,
    utilizado BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_validacao_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES Cliente(id)
        ON DELETE CASCADE
);
ALTER TABLE cliente
ADD email_verificado BOOLEAN DEFAULT FALSE;

-- PROCEDURES - RELATÓRIOS E CONSULTAS GERAIS

DELIMITER //

-- Gera um relatório consolidado com dados do mês atual (Ganhos, Qnt. Veículos, Tempo Médio, etc.)
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
    
    SELECT AVG(TIMESTAMPDIFF(HOUR, HorarioEntradaReal, HorarioSaidaReal)) INTO v_tempoMedio
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
    
    INSERT INTO RelatorioMensal(
        Ganhos, QntdClientesCarro, QntdClientesMoto, QntClientesCaminhao, 
        TempoMedioEstadia, ReclamacoesRegistradas, MultasAplicadas
    )
    VALUES(
        COALESCE(v_ganho, 0), v_qntdCarro, v_qntdMoto, v_qntdCaminhao, 
        COALESCE(v_tempoMedio, 0), v_reclamacoes, v_multas
    );
END //

-- Retorna todas as reservas associadas aos veículos de um determinado cliente
CREATE PROCEDURE GetReservasCliente(
    IN p_clienteId INT
)
BEGIN 
    SELECT R.* FROM Reserva R
    INNER JOIN Veiculo V ON R.VeiculoId = V.Id
    WHERE V.MotoristaPrincipal = p_clienteId;
END //

-- PROCEDURES - RECLAMAÇÕES


-- Insere uma nova reclamação com status inicial 'EM ANALISE'
CREATE PROCEDURE AdicionarReclamacao(
    IN p_Conteudo VARCHAR(2000),
    IN p_Estadia INT
)
BEGIN
    INSERT INTO Reclamacao(Conteudo, StatusReclamacao, EstadiaRelacionada) 
    VALUES(p_Conteudo, 'EM ANALISE', p_Estadia);
END //

-- Lista todas as reclamações vinculadas a uma estadia específica
CREATE PROCEDURE ListarReclamacoes(
    IN p_Estadia INT
)
BEGIN
    SELECT * FROM Reclamacao WHERE EstadiaRelacionada = p_Estadia;
END //

-- Atualiza o status de uma reclamação existente
CREATE PROCEDURE UpdateReclamacaoStatus(
    IN p_ReclamacaoId INT,
    IN p_ReclamacaoStatus ENUM('EM ANALISE', 'RESOLVIDA', 'RECUSADA')
)
BEGIN
    UPDATE Reclamacao
    SET StatusReclamacao = p_ReclamacaoStatus
    WHERE Id = p_ReclamacaoId;
END //

-- PROCEDURES - RESERVAS E ESTADIAS

-- Cria uma nova reserva com status 'ATIVA', vinculando veículo, pátio e vaga
CREATE PROCEDURE adicionarReserva(
    IN p_HoraEntrada TIMESTAMP,
    IN p_HoraSaida TIMESTAMP,
    IN p_ValorReserva DECIMAL(10,2),
    IN p_VeiculoId INT,
    IN p_PatioId INT,
    IN p_VagaId INT
)
BEGIN
    INSERT INTO Reserva(HorarioEntrada, HorarioSaida, Valor, StatusReserva, PatioId, VeiculoId, VagaId) 
    VALUES(p_HoraEntrada, p_HoraSaida, p_ValorReserva, 'ATIVA', p_PatioId, p_VeiculoId, p_VagaId);
END //

-- Altera o status de uma reserva para 'CANCELADA'
CREATE PROCEDURE CancelarReserva(
    IN p_ReservaId INT
)
BEGIN
    UPDATE Reserva
    SET StatusReserva = 'CANCELADA' 
    WHERE Id = p_ReservaId;
END //

-- Busca as informações detalhadas de uma reserva específica
CREATE PROCEDURE ListarReserva(
    IN p_ReservaId INT
)
BEGIN
    SELECT * FROM Reserva WHERE Id = p_ReservaId;
END //

-- Registra o momento exato em que o veículo entra no estacionamento
CREATE PROCEDURE ValidarEntrada(
    IN p_ReservaId INT
)
BEGIN
    INSERT INTO RegistroEstadia(HorarioEntradaReal, ReservaId)
    VALUES (CURRENT_TIMESTAMP, p_ReservaId);
END //

-- Registra o momento exato em que o veículo sai do estacionamento
CREATE PROCEDURE RegistrarSaida(
    IN p_EstadiaId INT
)
BEGIN
    UPDATE RegistroEstadia
    SET HorarioSaidaReal = CURRENT_TIMESTAMP
    WHERE Id = p_EstadiaId;
END //

-- Calcula o total de horas (fracionado) que o veículo permaneceu estacionado
CREATE PROCEDURE CalcularHoras(
    IN p_EstadiaId INT,
    OUT p_TotalHoras DECIMAL(10,2)
)
BEGIN
    SELECT TIMESTAMPDIFF(MINUTE, HorarioEntradaReal, HorarioSaidaReal) / 60.0
    INTO p_TotalHoras
    FROM RegistroEstadia
    WHERE Id = p_EstadiaId;
END //

-- Finaliza o ciclo da reserva, atuando como confirmação de que o serviço foi pago
CREATE PROCEDURE ProcessarPagamento(
    IN p_ReservaId INT
)
BEGIN
    UPDATE Reserva 
    SET StatusReserva = 'FINALIZADA' 
    WHERE Id = p_ReservaId;
END //

-- PROCEDURES - MULTAS

-- Aplica uma multa a uma estadia específica com status inicial 'NAO-PAGO'
CREATE PROCEDURE AdicionarMulta(
    IN p_Valor DECIMAL(10,2),
    IN p_Motivo VARCHAR(500),
    IN p_EstadiaId INT
)
BEGIN 
    INSERT INTO Multa(Valor, Motivo, StatusMulta, EstadiaRelacionada) 
    VALUES (p_Valor, p_Motivo, 'NAO-PAGO', p_EstadiaId);
END //

-- Atualiza o status de uma multa (ex: para 'PAGO' ou 'RETIRADO')
CREATE PROCEDURE UpdateMulta(
    IN p_MultaId INT,
    IN p_StatusMulta VARCHAR(20)
)
BEGIN 
    UPDATE Multa
    SET StatusMulta = p_StatusMulta 
    WHERE Id = p_MultaId;
END //

-- PROCEDURES - VEÍCULOS

-- Cadastra um novo veículo associado a um cliente (motorista principal)
CREATE PROCEDURE AdicionarVeiculo(
    IN p_Placa VARCHAR(7),
    IN p_Modelo VARCHAR(50),
    IN p_Cor VARCHAR(15),
    IN p_MotoristaPrinc INT,
    IN p_TipoVeiculo ENUM('CARRO', 'MOTO', 'CAMINHAO')
)
BEGIN
    INSERT INTO Veiculo(Placa, Modelo, Cor, MotoristaPrincipal, TipoVeiculo) 
    VALUES (p_Placa, p_Modelo, p_Cor, p_MotoristaPrinc, p_TipoVeiculo);
END //

-- Exclui o registro de um veículo do banco de dados
CREATE PROCEDURE RemoverVeiculo (
    IN p_VeiculoId INT
)
BEGIN
    DELETE FROM Veiculo WHERE Id = p_VeiculoId;
END //

-- Associa um motorista adicional a um veículo (tabela VeiculoCliente)
CREATE PROCEDURE AdicionaMotorista(
    IN p_ClienteId INT,
    IN p_VeiculoId INT
)
BEGIN 
    INSERT INTO VeiculoCliente(ClienteID, VeiculoId) 
    VALUES (p_ClienteId, p_VeiculoId);
END //

-- Atualiza os dados de um veículo cadastrado
CREATE PROCEDURE UpdateVeiculo(
    IN p_VeiculoId INT,
    IN p_Modelo VARCHAR(50),
    IN p_Cor VARCHAR(15),
    IN p_MotoristaPrinc INT,
    IN p_TipoVeiculo ENUM('CARRO', 'MOTO', 'CAMINHAO')
)
BEGIN
    UPDATE Veiculo
    SET Modelo = p_Modelo,
        Cor = p_Cor,
        MotoristaPrincipal = p_MotoristaPrinc,
        TipoVeiculo = p_TipoVeiculo 
    WHERE Id = p_VeiculoId;
END //

-- PROCEDURES - USUÁRIOS E CLIENTES

-- Insere os dados base de uma pessoa na tabela Usuario
CREATE PROCEDURE AdicionarUsuario(
    IN p_CPF VARCHAR(11),
    IN p_Nome VARCHAR(100),
    IN p_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100)
)
BEGIN
    INSERT INTO Usuario(CPF, Nome, Sexo, DataNascimento, Email, Telefone, Senha) 
    VALUES (p_CPF, p_Nome, p_Sexo, p_DataNascimento, p_Email, p_Telefone, p_Senha);
END //

-- Atualiza as informações pessoais de um Usuário
CREATE PROCEDURE UpdateUsuario(
    IN p_IdUsuario INT,
    IN p_Nome VARCHAR(100),
    IN p_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100)
)
BEGIN
    UPDATE Usuario
    SET Nome = p_Nome,
        Sexo = p_Sexo,
        DataNascimento = p_DataNascimento,
        Email = p_Email,
        Telefone = p_Telefone,
        Senha = p_Senha
    WHERE Id = p_IdUsuario;
END //

-- Remove um usuário do sistema
CREATE PROCEDURE RemoveUsuario(
    IN p_IdUsuario INT
)
BEGIN
    DELETE FROM Usuario WHERE Id = p_IdUsuario;
END //

-- Cria um usuário e imediatamente o cadastra como Cliente (herdando o ID)
CREATE PROCEDURE AdicionaCliente(
    IN p_CPF VARCHAR(11),
    IN p_Nome VARCHAR(100),
    IN p_Sexo ENUM('MASCULINO', 'FEMININO'),
    IN p_DataNascimento DATE,
    IN p_Email VARCHAR(100),
    IN p_Telefone VARCHAR(14),
    IN p_Senha VARCHAR(100),
    IN p_Mensalista BOOLEAN
)
BEGIN 
    DECLARE v_UsuarioId INT;
    
    CALL AdicionarUsuario(p_CPF, p_Nome, p_Sexo, p_DataNascimento, p_Email, p_Telefone, p_Senha);
    SET v_UsuarioId = LAST_INSERT_ID();
    
    INSERT INTO Cliente (Id, Mensalista) 
    VALUES (v_UsuarioId, p_Mensalista);
END //

-- Altera o status de "Mensalista" de um cliente
CREATE PROCEDURE UpdateCliente(
    IN p_IdCliente INT,
    IN p_Mensalista BOOLEAN
)
BEGIN
    UPDATE Cliente
    SET Mensalista = p_Mensalista 
    WHERE Id = p_IdCliente;
END //

-- PROCEDURES - PÁTIOS E VAGAS

-- Adiciona um novo pátio definindo sua capacidade para cada tipo de veículo
CREATE PROCEDURE AdicionarPatio(
    IN p_Endereco VARCHAR(200),
    IN p_CapCarro INT,
    IN p_CapMoto INT,
    IN p_CapCaminhao INT
)
BEGIN
    INSERT INTO Patio(Endereco, CapacidadeCarro, CapacidadeMoto, CapacidadeCaminhao)
    VALUES (p_Endereco, p_CapCarro, p_CapMoto, p_CapCaminhao);
END //

-- Altera as capacidades de um pátio existente
CREATE PROCEDURE AtualizarPatio(
    IN p_Id INT,
    IN p_Endereco VARCHAR(200),
    IN p_CapCarro INT,
    IN p_CapMoto INT,
    IN p_CapCaminhao INT
)
BEGIN
    UPDATE Patio
    SET Endereco = p_Endereco,
        CapacidadeCarro = p_CapCarro,
        CapacidadeMoto = p_CapMoto,
        CapacidadeCaminhao = p_CapCaminhao
    WHERE Id = p_Id;
END //

-- Exclui um pátio do sistema
CREATE PROCEDURE RemoverPatio(
    IN p_Id INT
)
BEGIN
    DELETE FROM Patio WHERE Id = p_Id;
END //

CREATE PROCEDURE SincronizarVagasPatio(IN p_PatioId INT)
BEGIN
    DECLARE capCarro, capMoto, capCaminhao INT DEFAULT 0;
    DECLARE atualCarro, atualMoto, atualCaminhao INT DEFAULT 0;

    SELECT CapacidadeCarro, CapacidadeMoto, CapacidadeCaminhao
      INTO capCarro, capMoto, capCaminhao
      FROM Patio WHERE Id = p_PatioId;

    SELECT COUNT(*) INTO atualCarro FROM Vaga WHERE PatioId = p_PatioId AND Tipo = 'CARRO';
    SELECT COUNT(*) INTO atualMoto FROM Vaga WHERE PatioId = p_PatioId AND Tipo = 'MOTO';
    SELECT COUNT(*) INTO atualCaminhao FROM Vaga WHERE PatioId = p_PatioId AND Tipo = 'CAMINHAO';

    WHILE atualCarro < capCarro DO
        INSERT INTO Vaga(Tipo, StatusVaga, PatioId) VALUES ('CARRO', 'DISPONIVEL', p_PatioId);
        SET atualCarro = atualCarro + 1;
    END WHILE;

    WHILE atualMoto < capMoto DO
        INSERT INTO Vaga(Tipo, StatusVaga, PatioId) VALUES ('MOTO', 'DISPONIVEL', p_PatioId);
        SET atualMoto = atualMoto + 1;
    END WHILE;

    WHILE atualCaminhao < capCaminhao DO
        INSERT INTO Vaga(Tipo, StatusVaga, PatioId) VALUES ('CAMINHAO', 'DISPONIVEL', p_PatioId);
        SET atualCaminhao = atualCaminhao + 1;
    END WHILE;
END //

-- Roda a sincronização acima para TODOS os pátios já cadastrados.
CREATE PROCEDURE SincronizarTodosPatios()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE pid INT;
    DECLARE cur CURSOR FOR SELECT Id FROM Patio;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO pid;
        IF done THEN
            LEAVE read_loop;
        END IF;
        CALL SincronizarVagasPatio(pid);
    END LOOP;
    CLOSE cur;
END //

-- Verifica e retorna a quantidade de vagas que estão 'DISPONIVEL' em um pátio para um tipo de veículo específico
CREATE PROCEDURE VerificarDisponibilidadeVaga(
    IN p_PatioId INT,
    IN p_Tipo VARCHAR(20),
    OUT p_QuantidadeDisponivel INT
)
BEGIN
    SELECT COUNT(*) INTO p_QuantidadeDisponivel
    FROM Vaga
    WHERE PatioId = p_PatioId 
      AND Tipo = p_Tipo 
      AND StatusVaga = 'DISPONIVEL';
END //

-- Reserva fisicamente uma vaga alterando seu status, impedindo uso rotativo
CREATE PROCEDURE DefinirVagasMensalistas(
    IN p_VagaId INT
)
BEGIN
    UPDATE Vaga 
    SET StatusVaga = 'RESERVADA' 
    WHERE Id = p_VagaId;
END //

-- Converte um cliente normal em mensalista e gera uma reserva ativa com duração de 30 dias
CREATE PROCEDURE AlugarVagaMensal(
    IN p_ClienteId INT,
    IN p_VeiculoId INT,
    IN p_PatioId INT,
    IN p_VagaId INT,
    IN p_Valor DECIMAL(10,2)
)
BEGIN
    UPDATE Cliente SET Mensalista = TRUE WHERE Id = p_ClienteId;
    
    INSERT INTO Reserva(HorarioEntrada, HorarioSaida, Valor, StatusReserva, PatioId, VeiculoId, VagaId)
    VALUES (CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 MONTH), p_Valor, 'ATIVA', p_PatioId, p_VeiculoId, p_VagaId);
END //

CREATE PROCEDURE CriarValidacaoToken(
    IN p_ClienteId INT,
    IN p_Token VARCHAR(255),
    IN p_DataExpiracao DATETIME
)
BEGIN
    INSERT INTO ValidacaoToken(cliente_id, token, data_expiracao)
    VALUES (p_ClienteId, p_Token, p_DataExpiracao);
END //

-- Busca um token que ainda seja válido (não utilizado e não expirado)
CREATE PROCEDURE BuscarValidacaoTokenValido(
    IN p_Token VARCHAR(255)
)
BEGIN
    SELECT * FROM ValidacaoToken
    WHERE token = p_Token
      AND utilizado = FALSE
      AND data_expiracao > NOW();
END //

-- Marca um token como já utilizado
CREATE PROCEDURE MarcarTokenUtilizado(
    IN p_Token VARCHAR(255)
)
BEGIN
    UPDATE ValidacaoToken SET utilizado = TRUE WHERE token = p_Token;
END //

-- Marca o e-mail do cliente como verificado
CREATE PROCEDURE MarcarEmailVerificado(
    IN p_ClienteId INT
)
BEGIN
    UPDATE Cliente SET email_verificado = TRUE WHERE Id = p_ClienteId;
END //

CREATE EVENT ev_relatorio_mensal
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 23 HOUR + INTERVAL 55 MINUTE)
DO
BEGIN
    IF DAY(CURRENT_DATE + INTERVAL 1 DAY) = 1 THEN
        CALL CriarRelatorioMensal();
    END IF;
END //

DELIMITER ;

CALL SincronizarTodosPatios();

INSERT INTO Usuario (CPF, Nome, Sexo, DataNascimento, Email, Telefone, Senha)
VALUES ('11122233344', 'Funcionario Teste', 'MASCULINO', '1995-05-10',
        'funcionario.teste@easyparking.com', '(11) 90000-0001', SHA2('123456', 256));
SET @func_id = LAST_INSERT_ID();
INSERT INTO Funcionario (Id) VALUES (@func_id);

-- Gerente de teste (todo Gerente também precisa ser Funcionário, conforme seu schema)
INSERT INTO Usuario (CPF, Nome, Sexo, DataNascimento, Email, Telefone, Senha)
VALUES ('55566677788', 'Gerente Teste', 'FEMININO', '1990-02-20',
        'gerente.teste@easyparking.com', '(11) 90000-0002', SHA2('123456', 256));
SET @ger_id = LAST_INSERT_ID();
INSERT INTO Funcionario (Id) VALUES (@ger_id);
INSERT INTO GERENTE (Id) VALUES (@ger_id);

INSERT INTO Usuario (CPF, Nome, Sexo, DataNascimento, Email, Telefone, Senha)
VALUES ('55562275588', 'Cliente Teste', 'MASCULINO', '1985-03-22',
        'cliente.teste@easyparking.com', '(11) 90111-0002', SHA2('123456', 256));

