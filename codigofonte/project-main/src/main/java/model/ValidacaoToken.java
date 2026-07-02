package model;

import java.time.LocalDateTime;

public class ValidacaoToken {

    public int Id;
    public int ClienteId;
    public String Token;
    public LocalDateTime DataCriacao;
    public LocalDateTime DataExpiracao;
    public boolean Utilizado;

    public ValidacaoToken() {
        super();
    }

    public ValidacaoToken(int id, int clienteId, String token, LocalDateTime dataCriacao,
            LocalDateTime dataExpiracao, boolean utilizado) {
        super();
        Id = id;
        ClienteId = clienteId;
        Token = token;
        DataCriacao = dataCriacao;
        DataExpiracao = dataExpiracao;
        Utilizado = utilizado;
    }

    public int getId() { return Id; }
    public void setId(int id) { Id = id; }

    public int getClienteId() { return ClienteId; }
    public void setClienteId(int clienteId) { ClienteId = clienteId; }

    public String getToken() { return Token; }
    public void setToken(String token) { Token = token; }

    public LocalDateTime getDataCriacao() { return DataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { DataCriacao = dataCriacao; }

    public LocalDateTime getDataExpiracao() { return DataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { DataExpiracao = dataExpiracao; }

    public boolean isUtilizado() { return Utilizado; }
    public void setUtilizado(boolean utilizado) { Utilizado = utilizado; }
}