package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.RelatorioMensal;
import util.dbConnection;

public class RelatorioMensalDao {

    // Dispara a procedure que consolida os dados do mês atual e grava um novo RelatorioMensal.
    // Usada tanto pelo botão "Gerar agora" do gerente quanto pela automação (EVENT do MySQL).
    public void gerarRelatorioMesAtual() {
        String sql = "{CALL CriarRelatorioMensal()}";
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatório mensal: " + e.getMessage(), e);
        }
    }

    // Lista todos os relatórios já gerados (mais recentes primeiro).
    public List<RelatorioMensal> listarTodos() {
        List<RelatorioMensal> lista = new ArrayList<>();
        String sql = "SELECT * FROM RelatorioMensal ORDER BY HorarioGerado DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar relatórios: " + e.getMessage(), e);
        }
        return lista;
    }

    // Busca um relatório específico (para gerar o PDF).
    public RelatorioMensal buscarPorId(int id) {
        String sql = "SELECT * FROM RelatorioMensal WHERE Id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar relatório: " + e.getMessage(), e);
        }
    }

    private RelatorioMensal mapear(ResultSet rs) throws SQLException {
        return new RelatorioMensal(
            rs.getInt("Id"),
            rs.getTimestamp("HorarioGerado").toLocalDateTime(),
            rs.getFloat("Ganhos"),
            rs.getInt("QntdClientesCarro"),
            rs.getInt("QntdClientesMoto"),
            rs.getInt("QntClientesCaminhao"), // ATENÇÃO: no banco a coluna está sem o 'd' (typo do DDL)
            rs.getFloat("TempoMedioEstadia"),
            rs.getInt("ReclamacoesRegistradas"),
            rs.getInt("MultasAplicadas")
        );
    }
}
