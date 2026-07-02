package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UsuarioDao;
import model.Usuario;
import Enum.SexoEnum;

@WebServlet("/usuario") 
public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDao usuarioDao = new UsuarioDao();

    public UsuarioServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.getWriter().append("Served at: ").append(req.getContextPath());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        if (acao == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação não informada.\"}");
            return;
        }

        try {
            switch (acao) {
                case "cadastrar":
                    executarCadastro(req, res);
                    break;
                    
                case "atualizar":
                    executarAtualizacao(req, res);
                    break;
                    
                case "remover":
                    executarRemocao(req, res);
                    break;

                default:
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }

    //  Cadastrar Usuário Geral 
    private void executarCadastro(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String cpf = extrairCampoJson(jsonBody, "cpf");
        String nome = extrairCampoJson(jsonBody, "nome");
        String sexoStr = extrairCampoJson(jsonBody, "sexo");
        String dataNascStr = extrairCampoJson(jsonBody, "dataNascimento"); // Formato esperado: "yyyy-MM-dd"
        String email = extrairCampoJson(jsonBody, "email");
        String telefone = extrairCampoJson(jsonBody, "telefone");
        String senhaBruta = extrairCampoJson(jsonBody, "senha");

        if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || senhaBruta.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Campos obrigatórios ausentes.\"}");
            return;
        }

        SexoEnum sexo = SexoEnum.valueOf(sexoStr.toUpperCase());
        LocalDate dataNascimento = LocalDate.parse(dataNascStr);
        Usuario novoUsuario = new Usuario(0, cpf, nome, sexo, dataNascimento, email, telefone, senhaBruta);
        
        boolean cadastrou = usuarioDao.cadastrarUsuario(novoUsuario);

        if (cadastrou) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Usuário cadastrado com sucesso!\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao salvar o usuário no banco de dados.\"}");
        }
    }

    //  Atualiza Usuário Completo
    private void executarAtualizacao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        int id = Integer.parseInt(extrairCampoJson(jsonBody, "id"));
        String cpf = extrairCampoJson(jsonBody, "cpf");
        String nome = extrairCampoJson(jsonBody, "nome");
        String sexoStr = extrairCampoJson(jsonBody, "sexo");
        String dataNascStr = extrairCampoJson(jsonBody, "dataNascimento");
        String email = extrairCampoJson(jsonBody, "email");
        String telefone = extrairCampoJson(jsonBody, "telefone");
        String senhaBruta = extrairCampoJson(jsonBody, "senha");

        SexoEnum sexo = SexoEnum.valueOf(sexoStr.toUpperCase());
        LocalDate dataNascimento = LocalDate.parse(dataNascStr);
        Usuario usuarioAtualizar = new Usuario(id, cpf, nome, sexo, dataNascimento, email, telefone, senhaBruta);
        usuarioDao.atualizarUsuario(usuarioAtualizar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Dados do usuário atualizados!\"}");
    }

    // Remove Usuário do Sistema
    private void executarRemocao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        String idStr = extrairCampoJson(jsonBody, "id");

        if (idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID é obrigatório para exclusão.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        Usuario usuarioDeletar = new Usuario(id, null, null, null, null, null, null, null);
        usuarioDao.removerUsuario(usuarioDeletar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Usuário removido com sucesso!\"}");
    }

    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }

    private String extrairCampoJson(String json, String campo) {
        try {
            String chave = "\"" + campo + "\"";
            if (!json.contains(chave)) return "";
            int inicio = json.indexOf(chave) + chave.length();
            inicio = json.indexOf("\"", inicio) + 1;
            int fim = json.indexOf("\"", inicio);
            return json.substring(inicio, fim);
        } catch (Exception e) {
            return "";
        }
    }
}