package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Enum.StatusReclamacaoEnum;
import model.Reclamacao;
import model.RegistroEstadia;
import model.Reserva;
import model.Veiculo;
import util.dbConnection;

public class ReclamacaoDao {

	// Representa uma reclamação já enriquecida com dados do veículo/data da estadia,
	// só para exibição nas telas de listagem (gerente e cliente).
	public static class ReclamacaoDetalhada {
		public Reclamacao reclamacao;
		public String veiculo;
		public java.time.LocalDateTime dataEstadia;
	}

	// Lista TODAS as reclamações do sistema, com veículo/data, para a tela do gerente.
	public List<ReclamacaoDetalhada> listarTodas() {
		List<ReclamacaoDetalhada> lista = new ArrayList<>();
		String sql = "SELECT rec.Id, rec.Conteudo, rec.StatusReclamacao, "
				   + "       re.HorarioEntradaReal, v.Modelo, v.Placa, v.Cor "
				   + "FROM Reclamacao rec "
				   + "INNER JOIN RegistroEstadia re ON rec.EstadiaRelacionada = re.Id "
				   + "INNER JOIN Reserva r ON re.ReservaId = r.Id "
				   + "INNER JOIN Veiculo v ON r.VeiculoId = v.Id "
				   + "ORDER BY re.HorarioEntradaReal DESC";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				lista.add(montarDetalhada(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar todas as reclamações", e);
		}
		return lista;
	}

	// Lista as reclamações feitas pelo cliente logado (em todos os veículos dele).
	public List<ReclamacaoDetalhada> listarPorCliente(int clienteId) {
		List<ReclamacaoDetalhada> lista = new ArrayList<>();
		String sql = "SELECT rec.Id, rec.Conteudo, rec.StatusReclamacao, "
				   + "       re.HorarioEntradaReal, v.Modelo, v.Placa, v.Cor "
				   + "FROM Reclamacao rec "
				   + "INNER JOIN RegistroEstadia re ON rec.EstadiaRelacionada = re.Id "
				   + "INNER JOIN Reserva r ON re.ReservaId = r.Id "
				   + "INNER JOIN Veiculo v ON r.VeiculoId = v.Id "
				   + "WHERE v.MotoristaPrincipal = ? "
				   + "ORDER BY re.HorarioEntradaReal DESC";

		try (Connection conn = dbConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, clienteId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					lista.add(montarDetalhada(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar reclamações do cliente", e);
		}
		return lista;
	}

	private ReclamacaoDetalhada montarDetalhada(ResultSet rs) throws SQLException {
		String statusBd = rs.getString("StatusReclamacao").replace(" ", "_").toUpperCase();
		StatusReclamacaoEnum status = StatusReclamacaoEnum.valueOf(statusBd);

		Reclamacao rec = new Reclamacao(rs.getInt("Id"), rs.getString("Conteudo"), status, null);

		ReclamacaoDetalhada det = new ReclamacaoDetalhada();
		det.reclamacao = rec;
		det.dataEstadia = rs.getTimestamp("HorarioEntradaReal").toLocalDateTime();
		det.veiculo = rs.getString("Modelo") + " (" + rs.getString("Cor") + ") - " + rs.getString("Placa");
		return det;
	}

    public void adicionarReclamacao(Reclamacao reclamacao) {
        String sql = "{CALL AdicionarReclamacao(?,?)}";
        try(Connection conn = dbConnection.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)){
            
            stmt.setString(1, reclamacao.getConteudo());
            stmt.setInt(2, reclamacao.getEstadiaRelacionada().getId());
            stmt.execute();
            
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    // 🔄 Método corrigido para retornar a lista de reclamações
    public List<Reclamacao> listarReclamacoes(int idEstadia) {
        List<Reclamacao> lista = new ArrayList<>();
        String sql = "{CALL ListarReclamacoes(?)}";
        
        try(Connection conn = dbConnection.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)){
            
            stmt.setInt(1, idEstadia);
            ResultSet rs = stmt.executeQuery(); // Captura o resultado da consulta
            
            while (rs.next()) {
                // Formata o status vindo do BD ("EM ANALISE" -> "EM_ANALISE")
                String statusBd = rs.getString("StatusReclamacao").replace(" ", "_").toUpperCase();
                StatusReclamacaoEnum status = StatusReclamacaoEnum.valueOf(statusBd);
                
                // Instancia a estadia apenas com o ID para compor o objeto
                RegistroEstadia estadia = new RegistroEstadia();
                estadia.setId(rs.getInt("EstadiaRelacionada"));
                
                Reclamacao rec = new Reclamacao(
                    rs.getInt("Id"),
                    rs.getString("Conteudo"),
                    status,
                    estadia
                );
                
                lista.add(rec);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        
        return lista;
    }
    
    public void updateStatusReclamacao(Reclamacao reclamacao) {
        String sql = "{CALL UpdateReclamacaoStatus(?,?)}";
        try(Connection conn = dbConnection.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)){
            
            stmt.setInt(1, reclamacao.getId());
            stmt.setString(2, reclamacao.getStatusReclamacao().name().replace("_", " ")); // Volta ao padrão do BD
            stmt.execute();
            
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}