package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ClienteDao;
import dao.FuncionarioDao;
import dao.UsuarioDao;
import model.Cliente;
import model.Funcionario;

@WebServlet("/funcionario")
public class FuncionarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FuncionarioDao funcionarioDao = new FuncionarioDao();
    private ClienteDao clienteDao = new ClienteDao();
    private UsuarioDao usuarioDao = new UsuarioDao();

    public FuncionarioServlet() {
        super();
    }

    // 
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // verifica se tem um funcionário logado na sessão
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("funcionarioLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Funcionário não autenticado.\"}");
            return;
        }

        if ("pesquisarCliente".equals(acao)) {
            String termo = req.getParameter("termo");
            if (termo == null || termo.trim().isEmpty()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Informe um termo de busca.\"}");
                return;
            }
            List<Cliente> clientes = clienteDao.pesquisarPorNome(termo);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < clientes.size(); i++) {
                Cliente c = clientes.get(i);
                json.append("{")
                    .append("\"id\": ").append(c.getId()).append(",")
                    .append("\"nome\": \"").append(escapar(c.getNome())).append("\",")
                    .append("\"cpf\": \"").append(escapar(c.getCpf())).append("\",")
                    .append("\"email\": \"").append(escapar(c.getEmail())).append("\",")
                    .append("\"numero\": \"").append(escapar(c.getTelefone())).append("\"")
                    .append("}");
                if (i < clientes.size() - 1) json.append(",");
            }
            json.append("]");
            res.getWriter().write(json.toString());
            return;
        }

        if ("buscarPerfil".equals(acao)) {
            Funcionario func = (Funcionario) session.getAttribute("funcionarioLogado");
            res.getWriter().write("{\"nome\":\""+escapar(func.getNome())+"\",\"cpf\":\""+escapar(func.getCpf())+"\",\"email\":\""+escapar(func.getEmail())+"\",\"telefone\":\""+escapar(func.getTelefone())+"\"}");
            return;
        }

        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // salvarr e alterar dados
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
                    // perfil do funcionario
                    executarAtualizacaoSimples(req, res);
                    break;

                case "atualizarComplexo":

                    executarAtualizacaoComplexo(req, res);
                    break;

                case "atualizarClienteSimples":
                    // perfil de um cliente
                    executarAtualizacaoClienteSimples(req, res);
                    break;

                case "atualizarClienteComplexo":

                    executarAtualizacaoClienteComplexo(req, res);
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

    // confere se há um funcionário logado; devolve o Id ou -1.
    private int funcionarioLogadoId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("funcionarioLogado") == null) return -1;
        return ((Funcionario) session.getAttribute("funcionarioLogado")).getId();
    }

    // processa login de funcionario
    private void executarLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String emailForm = extrairCampoJson(jsonBody, "email");
        String senhaForm = extrairCampoJson(jsonBody, "senha");

        if (emailForm.isEmpty() || senhaForm.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail e senha são obrigatórios.\"}");
            return;
        }

        Funcionario funcionarioObjeto = funcionarioDao.validarLogin(emailForm, senhaForm);
        
        if (funcionarioObjeto != null) {
            req.getSession().setAttribute("funcionarioLogado", funcionarioObjeto);
            res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Login efetuado!\", \"redirecionar\": \"reservas_funcionario.jsp\"}");
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"E-mail ou senha incorretos.\"}");
        }
    }

    //
    private void executarAtualizacaoSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = funcionarioLogadoId(req);
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
        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Dados atualizados com sucesso!" : "Nada foi alterado.") + "\"}");
    }

    private void executarAtualizacaoComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = funcionarioLogadoId(req);
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

        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Credenciais alteradas com sucesso!" : "Nada foi alterado.") + "\"}");
    }

    private void executarAtualizacaoClienteSimples(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (funcionarioLogadoId(req) == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String idStr = extrairCampoJson(jsonBody, "id");
        String campo = extrairCampoJson(jsonBody, "campo");
        String valor = extrairCampoJson(jsonBody, "valor");

        if (idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Cliente não informado.\"}");
            return;
        }

        String coluna;
        if ("nome".equals(campo)) coluna = "Nome";
        else if ("numero".equals(campo)) coluna = "Telefone";
        else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Campo inválido.\"}");
            return;
        }

        boolean ok = usuarioDao.atualizarCampoSimples(Integer.parseInt(idStr), coluna, valor);
        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Dados do cliente atualizados!" : "Nada foi alterado.") + "\"}");
    }

    private void executarAtualizacaoClienteComplexo(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (funcionarioLogadoId(req) == -1) { naoAutenticado(res); return; }

        String jsonBody = lerCorpoRequisicao(req);
        String idStr = extrairCampoJson(jsonBody, "id");
        String tipo = extrairCampoJson(jsonBody, "tipo");
        String novoValor = extrairCampoJson(jsonBody, "novoValor");

        if (idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Cliente não informado.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        boolean ok;
        if ("email".equals(tipo)) {
            ok = usuarioDao.atualizarEmail(id, novoValor);
        } else if ("senha".equals(tipo)) {
            ok = usuarioDao.atualizarSenha(id, novoValor);
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Tipo inválido.\"}");
            return;
        }

        res.getWriter().write("{\"sucesso\": " + ok + ", \"mensagem\": \"" + (ok ? "Credenciais do cliente atualizadas!" : "Nada foi alterado.") + "\"}");
    }

    private void naoAutenticado(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Funcionário não autenticado.\"}");
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
