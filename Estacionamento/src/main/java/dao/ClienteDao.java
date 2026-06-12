package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import model.Cliente;
import util.dbConnection;

public class ClienteDao {
	
	public boolean cadastrarCliente(Cliente cliente) {
			
		String sql = "{CALL AdicionarUsuario(?,?,?,?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setString(1, cliente.getCpf());
			stmt.setString(2, cliente.getNome());
			stmt.setString(3, cliente.getSexo().name());
			stmt.setObject(4, cliente.getDataNascimento());
			stmt.setString(5, cliente.getEmail());
			stmt.setString(6, cliente.getTelefone());
			stmt.setString(7, cliente.getSenha());
			stmt.setBoolean(8, cliente.getMensalista());
			
			
			stmt.execute();
			return true;
			
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean mudarStatusMensalista(Cliente cliente) {
		var statusMensalista = !cliente.getMensalista();
		
		String sql = "{CALL UpdateCliente(?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
			
				stmt.setInt(1, cliente.getId());
				stmt.setBoolean(2, statusMensalista);
				int linhasAlteradas = stmt.executeUpdate();
				cliente.setMensalista(statusMensalista);
				return linhasAlteradas > 0;
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	}
	
	public void listarReservasCliente(Cliente cliente) {
		String sql = "{CALL UpdateCliente(?)}";
		
		try (Connection conn = dbConnection.getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)) {
			
				stmt.setInt(1, cliente.getId());
				
				stmt.execute();
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
		
	}

}
