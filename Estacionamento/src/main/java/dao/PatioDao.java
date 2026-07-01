package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Patio;
import util.dbConnection;

public class PatioDao {
	
	public void adicionarPatio(Patio patio) {
		String sql = "{CALL AdicionarPatio(?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
				stmt.setString(1,  patio.getEndereco());
	            stmt.setInt(2, patio.getCapacidadeCarro());
	            stmt.setInt(3, patio.getCapacidadeMoto());
	            stmt.setInt(4, patio.getCapacidadeCaminhao());
				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void atualizarPatio(Patio patio) {
		String sql = "{CALL AtualizarPatio(?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());
            	stmt.setString(2, patio.getEndereco());
	            stmt.setInt(3, patio.getCapacidadeCarro());
	            stmt.setInt(4, patio.getCapacidadeMoto());
	            stmt.setInt(5, patio.getCapacidadeCaminhao());
				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void removerPatio(Patio patio) {
		String sql = "{CALL RemoverPatio(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
            	stmt.setInt(1, patio.getId());

				stmt.execute();
	            
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public List<Patio> listarPatios() {
	    List<Patio> lista = new ArrayList<>();
	    String sql = "SELECT * FROM Patio"; 
	    
	    try (Connection conn = dbConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	         
	        while (rs.next()) {
	            Patio p = new Patio(
	                rs.getInt("Id"),
	                rs.getString("Endereco"),
	                rs.getInt("CapacidadeCarro"),
	                rs.getInt("CapacidadeMoto"),
	                rs.getInt("CapacidadeCaminhao")
	            );
	            lista.add(p);
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException(e);
	    }
	    return lista;
	}

	
	public int verificarDisponibilidadeVaga(Patio patio, String tipoVeiculo) {
        String sql = "{CALL VerificarDisponibilidadeVaga(?, ?, ?)}";
        int vagasDisponiveis = 0;
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, patio.getId());
            stmt.setString(2, tipoVeiculo);
            stmt.registerOutParameter(3, Types.INTEGER); // Lendo o OUT 
            
            stmt.execute();
            vagasDisponiveis = stmt.getInt(3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vagasDisponiveis;
    }
}
