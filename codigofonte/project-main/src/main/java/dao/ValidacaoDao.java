package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import model.ValidacaoToken;
import util.dbConnection;

public class ValidacaoDao {

    public String gerarToken(int clienteId) {
        String token = UsuarioDao.gerarHash(UUID.randomUUID().toString() + System.nanoTime());
        LocalDateTime expira = LocalDateTime.now().plusHours(24); 

        String sql = "{CALL CriarValidacaoToken(?,?,?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, clienteId);
            stmt.setString(2, token);
            stmt.setTimestamp(3, Timestamp.valueOf(expira));

            stmt.execute();
            return token;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar token de validação: " + e.getMessage(), e);
        }
    }

    public ValidacaoToken buscarTokenValido(String token) {
        String sql = "{CALL BuscarValidacaoTokenValido(?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ValidacaoToken vt = new ValidacaoToken();
                    vt.setId(rs.getInt("id"));
                    vt.setClienteId(rs.getInt("cliente_id"));
                    vt.setToken(rs.getString("token"));
                    vt.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                    vt.setDataExpiracao(rs.getTimestamp("data_expiracao").toLocalDateTime());
                    vt.setUtilizado(rs.getBoolean("utilizado"));
                    return vt;
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar token: " + e.getMessage(), e);
        }
    }

    public void marcarTokenUtilizado(String token) {
        String sql = "{CALL MarcarTokenUtilizado(?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, token);
            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar token como utilizado: " + e.getMessage(), e);
        }
    }

    public void marcarEmailVerificado(int clienteId) {
        String sql = "{CALL MarcarEmailVerificado(?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, clienteId);
            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar e-mail como verificado: " + e.getMessage(), e);
        }
    }
}
