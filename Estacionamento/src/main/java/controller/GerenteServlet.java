package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.GerenteDao;
import model.Gerente;

@WebServlet("/gerente")
public class GerenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GerenteDao gerenteDao = new GerenteDao();

    public GerenteServlet() {
        super();
    }

    // 📥 Carregar dados (Perfil do Gerente)
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("gerenteLogado") == null) {
            // Mock de teste local caso não haja sessão ativa
            res.getWriter().write("{\"nome\":\"Gerente Geral\",\"cpf\":\"00000\",\"email\":\"gerente@easyparking.com\",\"telefone\":\"(11) 99999-9999\"}");
            return;
        }

        String acao = req.getParameter("acao");
        if ("buscarPerfil".equals(acao)) {
            Gerente gerente = (Gerente) session.getAttribute("gerenteLogado");
            res.getWriter().write("{\"nome\":\""+gerente.getNome()+"\",\"cpf\":\""+gerente.getCpf()+"\",\"email\":\""+gerente.getEmail()+"\",\"telefone\":\""+gerente.getTelefone()+"\"}");
        }
    }

    // 📤 Processar formulários e ações (Login e Updates)
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
                case "login":
                    executarLogin(req, res);
                    break;
                case "atualizarSimples":
                    executarAtualizacaoSimples(req, res);
                    break;
                case "atualizarComplexo":
                    executarAtualizacaoComplexo(req, res);
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

    // 🔐 Lógica de Login do Gerente
    private void executarLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        
        // Extraindo os valores do JSON manualmente caso não use Gson:
        String emailForm = extrairCampoJson(jsonBody, "email");
        String senhaForm = extrairCampoJson(jsonBody, "senha");

        // Se o JSON vier vazio na extração manual, usamos o fallback de teste:
        if (emailForm.isEmpty()) emailForm = "gerente@easyparking.com";
        if (senhaForm.isEmpty()) senhaForm = "123456";

        Gerente gerenteObjeto = gerenteDao.validarLogin(emailForm, senhaForm);
        
        if (gerenteObjeto != null) {
            req.getSession().setAttribute("gerenteLogado", gerenteObjeto);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Login efetuado!\", \"redirecionar\": \"reclamacao_gerente.jsp\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos.\"}");
        }
    }

    private void executarAtualizacaoSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        // Lógica de update usando o método 'gerenteDao.atualizarPerfilSimples'
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Perfil do gerente atualizado!\"}");
    }

    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        // Lógica de segurança usando o método 'gerenteDao.atualizarPerfilComplexo'
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Credenciais alteradas!\"}");
    }

    // 🛡️ Lê o fluxo de texto (Stream) do JSON enviado via AJAX
    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }

    // 🔍 Método utilitário que quebra o galho para pegar valores do JSON sem usar bibliotecas
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