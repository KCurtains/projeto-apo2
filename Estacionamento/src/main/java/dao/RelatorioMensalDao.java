package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import model.RelatorioMensal;

import util.dbConnection;

public class RelatorioMensalDao {
	

	public RelatorioMensal criarRelatorioMensal() {
		
		String sql = "{CALL CriarRelatorioMensal()}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql);
	        ResultSet rs = stmt.executeQuery()){
			
			if (rs.next()) {
				
				RelatorioMensal relatorio = new RelatorioMensal(0, null, null, 0, 0, 0, null, 0, 0);
				
				relatorio.setId(rs.getInt("Id"));
				relatorio.setHorarioGerado(rs.getObject("HorarioGerado", LocalDateTime.class));
				relatorio.setGanhos(rs.getFloat("Ganhos"));
				relatorio.setQntdClientesCarro(rs.getInt("QntdClientesCarro"));
				relatorio.setQntdClientesMoto(rs.getInt("QntdClientesMoto"));
				relatorio.setQntdClientesCaminhao(rs.getInt("QntClientesCaminhao"));
				relatorio.setTempoMedioEstadia(rs.getFloat("TempoMedioEstadia"));
				relatorio.setReclamacoesRegistradas(rs.getInt("ReclamacoesRegistradas"));
				relatorio.setMultasAplicadas(rs.getInt("MultasAplicadas"));
				
				return relatorio;
			}
			
			return null;
		}catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar veículo: " + e.getMessage(), e);
        }
	}
}
