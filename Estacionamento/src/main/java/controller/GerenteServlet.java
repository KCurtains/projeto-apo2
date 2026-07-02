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
import dao.UsuarioDao;
import model.Gerente;

@WebServlet("/gerente")
public class GerenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private GerenteDao gerenteDao = new GerenteDao();
    private UsuarioDao usuarioDao = new UsuarioDao();

    public GerenteServlet() {
        super();
    }

    // 📥 Carregar dados (Perfil do Gerente)
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("gerenteLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Gerente não autenticado.\"}");
            return;
        }

        String acao = req.getParameter("acao");
        if ("buscarPerfil".equals(acao)) {
            Gerente gerente = (Gerente) session.getAttribute("gerenteLogado");
            res.getWriter().write("{\"nome\":\""+escapar(gerente.getNome())+"\",\"cpf\":\""+escapar(gerente.getCpf())+"\",\"email\":\""+escapar(gerente.getEmail())+"\",\"telefone\":\""+escapar(gerente.getTelefone())+"\"}");
            return;
        }

        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private int gerenteLogadoId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("gerenteLogado") == null) return -1;
        return ((Gerente) session.getAttribute("gerenteLogado")).getId();
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

        // CORRIGIDO: nada de credenciais fixas. Sem email/senha => 400.
        if (emailForm.isEmpty() || senhaForm.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail e senha são obrigatórios.\"}");
            return;
        }

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
        int id = gerenteLogadoId(req);
        if (id == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String campo = extrairCampoJson(jsonBody, "campo");
        String valor = extrairCampoJson(jsonBody, "valor");

        String coluna;
        if ("nome".equals(campo)) coluna = "Nome";
        else if ("numero".equals(campo)) coluna = "Telefone";
        else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Campo inválido.\"}");
            return;
        }

        boolean ok = usuarioDao.atualizarCampoSimples(id, coluna, valor);
        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Perfil do gerente atualizado!" : "Nada foi alterado.") + "\"}");
    }

    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = gerenteLogadoId(req);
        if (id == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String tipo = extrairCampoJson(jsonBody, "tipo");
        String valorAtual = extrairCampoJson(jsonBody, "valorAtual");
        String novoValor = extrairCampoJson(jsonBody, "novoValor");

        boolean ok;
        if ("email".equals(tipo)) {
            if (!usuarioDao.confirmaEmailAtual(id, valorAtual)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"O e-mail atual informado não confere.\"}");
                return;
            }
            ok = usuarioDao.atualizarEmail(id, novoValor);
        } else if ("senha".equals(tipo)) {
            if (!usuarioDao.confirmaSenhaAtual(id, valorAtual)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"A senha atual informada está incorreta.\"}");
                return;
            }
            ok = usuarioDao.atualizarSenha(id, novoValor);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Tipo inválido.\"}");
            return;
        }

        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Credenciais alteradas!" : "Nada foi alterado.") + "\"}");
    }

    private void naoAutenticado(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Gerente não autenticado.\"}");
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