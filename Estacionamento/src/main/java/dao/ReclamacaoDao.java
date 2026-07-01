package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Enum.StatusReclamacaoEnum;
import model.Reclamacao;
import model.RegistroEstadia;
import util.dbConnection;

public class ReclamacaoDao {

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