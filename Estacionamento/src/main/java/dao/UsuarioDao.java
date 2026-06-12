package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import Enum.SexoEnum;
import model.Usuario;
import util.dbConnection;

public class UsuarioDao {

	
	public boolean cadastrarUsuario(Usuario usuario) {
		
		String sql = "{CALL AdicionarUsuario(?,?,?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
			CallableStatement stmt = conn.prepareCall(sql)){
			
			stmt.setString(1, usuario.getCpf());
			stmt.setString(2, usuario.getNome());
			stmt.setString(3, usuario.getSexo().name());
			stmt.setObject(4, usuario.getDataNascimento());
			stmt.setString(5, usuario.getEmail());
			stmt.setString(6, usuario.getTelefone());
			stmt.setString(7, usuario.getSenha());
			
			
			stmt.execute();
			return true;
			
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Usuario autenticarUsuario(String email, String senha) {
		
		String sql = "{CALL BuscaUsuarioEmail(?)}";
		
		try (Connection conn = dbConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)){
				
				stmt.setString(1, email);

				
				try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							if (gerarHash(senha).equals(rs.getString("Senha"))) {
							    Usuario usuario = new Usuario(0, null, null, null, null, null, null, null); 
							    
							    usuario.setId(rs.getInt("Id"));
							    usuario.setCpf(rs.getString("CPF"));
							    usuario.setNome(rs.getString("Nome"));
							    
							    String sexoStr = rs.getString("Sexo");
							    if (sexoStr != null) {
							        usuario.setSexo(SexoEnum.valueOf(sexoStr));
							    }
							    
							    usuario.setDataNascimento(rs.getObject("DataNascimento", LocalDate.class));
							    
							    usuario.setEmail(rs.getString("Email"));
							    usuario.setTelefone(rs.getString("Telefone"));
							    
							    return usuario; 
							}else {
								return null;
						}
					}
				}
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
		return null;

	}
	
	public static String gerarHash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    texto.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash SHA-256", e);
        }
    }
	
	public void atualizarUsuario(Usuario usuario) {
		String sql = "{CALL UpdateUsuario(?,?,?,?,?,?)}";
		
		try (Connection conn = dbConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)){
				
				stmt.setString(1, usuario.getCpf());
				stmt.setString(2, usuario.getNome());
				stmt.setString(3, usuario.getSexo().name());
				stmt.setObject(4, usuario.getDataNascimento());
				stmt.setString(5, usuario.getEmail());
				stmt.setString(6, usuario.getTelefone());
				stmt.setString(7, usuario.getSenha());
				
				
				stmt.execute();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void removerUsuario(Usuario usuario) {
		String sql = "{CALL RemoverUsuario(?)}";
		
		try (Connection conn = dbConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)){
				
				stmt.setInt(1, usuario.getId());

				stmt.execute();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
	}
	
}
