package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ClienteDao;
import model.Cliente;
import model.Usuario;
import Enum.SexoEnum;

@WebServlet("/cliente")
public class ClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ClienteDao clienteDao = new ClienteDao();

    // recupera id
    private int idLogado(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) return -1;
        return ((Usuario) session.getAttribute("usuarioLogado")).getId();
    }

    // carrega dados
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Usuário não autenticado.\"}");
            return;
        }

        Usuario clienteLogado = (Usuario) session.getAttribute("usuarioLogado");
        res.getWriter().write("{"
            + "\"nome\":\"" + escapar(clienteLogado.getNome()) + "\","
            + "\"cpf\":\"" + escapar(clienteLogado.getCpf()) + "\","
            + "\"email\":\"" + escapar(clienteLogado.getEmail()) + "\","
            + "\"telefone\":\"" + escapar(clienteLogado.getTelefone()) + "\"}");
    }

    // usado para salvar e alterar
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
                case "cadastrar":         executarCadastro(req, res); break;
                case "atualizarSimples":  executarAtualizacaoSimples(req, res); break;
                case "atualizarComplexo": executarAtualizacaoComplexo(req, res); break;
                case "mudarMensalista":   executarMudancaMensalista(req, res); break;
                default:
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }


    private void executarCadastro(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String cpf = extrairCampoJson(jsonBody, "cpf");
        String nome = extrairCampoJson(jsonBody, "nome");
        String email = extrairCampoJson(jsonBody, "email");
        String telefone = extrairCampoJson(jsonBody, "telefone");
        String senha = extrairCampoJson(jsonBody, "senha");
        String sexoStr = extrairCampoJson(jsonBody, "sexo");
        String dataNascStr = extrairCampoJson(jsonBody, "dataNascimento");
        String mensalistaStr = extrairCampoJson(jsonBody, "mensalista");

        if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Campos obrigatórios ausentes.\"}");
            return;
        }

        SexoEnum sexo;
        if (sexoStr.isEmpty()) {
            sexo = SexoEnum.MASCULINO;
        } else {
            sexo = SexoEnum.valueOf(sexoStr.toUpperCase());
        }

        LocalDate dataNascimento;
        if (dataNascStr.isEmpty()) {
            dataNascimento = LocalDate.of(2000, 1, 1);
        } else {
            dataNascimento = LocalDate.parse(dataNascStr);
        }

        boolean mensalista = "true".equalsIgnoreCase(mensalistaStr);

        Cliente novoCliente = new Cliente(0, cpf, nome, sexo, dataNascimento, email, telefone, senha, mensalista, null);

        boolean sucesso = clienteDao.cadastrarCliente(novoCliente);
        if (sucesso) {
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Cliente cadastrado com sucesso!\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao salvar no banco (e-mail/CPF já cadastrado?).\"}");
        }
    }


    private void executarAtualizacaoSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = idLogado(req);
        if (id == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String nome = extrairCampoJson(jsonBody, "nome");
        String telefone = extrairCampoJson(jsonBody, "telefone");

        boolean ok = clienteDao.atualizarPerfilSimples(id, nome, telefone);
        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Perfil atualizado!" : "Nada foi alterado.") + "\"}");
    }


    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = idLogado(req);
        if (id == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String email = extrairCampoJson(jsonBody, "email");
        String novaSenha = extrairCampoJson(jsonBody, "senha");

        boolean ok = clienteDao.atualizarPerfilComplexo(id, email, novaSenha);
        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Credenciais alteradas!" : "Nada foi alterado.") + "\"}");
    }

    // altera status de mensalista (não implementado no momento)
    private void executarMudancaMensalista(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = idLogado(req);
        if (id == -1) { naoAutenticado(res); return; }

        boolean novoStatus = clienteDao.alternarMensalista(id);
        res.getWriter().write("{\"sucesso\": true, \"novoStatus\": " + novoStatus + "}");
    }

    private void naoAutenticado(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Usuário não autenticado.\"}");
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) sb.append(linha);
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
