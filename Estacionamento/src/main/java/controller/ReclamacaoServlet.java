package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.ReclamacaoDao;
import dao.ReclamacaoDao.ReclamacaoDetalhada;
import model.Reclamacao;
import model.RegistroEstadia;
import model.Usuario;
import Enum.StatusReclamacaoEnum;

@WebServlet("/reclamacao")
public class ReclamacaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReclamacaoDao reclamacaoDao = new ReclamacaoDao();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReclamacaoServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        HttpSession session = req.getSession(false);

        try {
            // 🧑‍💼 Gerente: todas as reclamações do sistema
            if ("listarTodas".equals(acao)) {
                if (session == null || session.getAttribute("gerenteLogado") == null) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Acesso restrito ao gerente.\"}");
                    return;
                }
                res.getWriter().write(montarJsonDetalhado(reclamacaoDao.listarTodas()));
                return;
            }

            // 🙋 Cliente: só as reclamações que ele mesmo abriu
            if ("listarMinhas".equals(acao) || acao == null) {
                if (session == null || session.getAttribute("usuarioLogado") == null) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Usuário não autenticado.\"}");
                    return;
                }
                int clienteId = ((Usuario) session.getAttribute("usuarioLogado")).getId();
                res.getWriter().write(montarJsonDetalhado(reclamacaoDao.listarPorCliente(clienteId)));
                return;
            }

            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Ação desconhecida.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro interno no servidor.\"}");
        }
    }

    // Converte o status interno do enum para o texto exibido nas telas.
    private String statusParaTexto(StatusReclamacaoEnum status) {
        switch (status) {
            case EM_ANALISE: return "Em análise";
            case RESOLVIDA:  return "Resolvida";
            case RECUSADA:   return "Recusada";
            default:         return status.name();
        }
    }

    private String montarJsonDetalhado(List<ReclamacaoDetalhada> lista) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            ReclamacaoDetalhada d = lista.get(i);
            json.append("{")
                .append("\"id\": ").append(d.reclamacao.getId()).append(",")
                .append("\"veiculo\": \"").append(d.veiculo.replace("\"", "\\\"")).append("\",")
                .append("\"data\": \"").append(d.dataEstadia.format(FMT)).append("\",")
                .append("\"texto\": \"").append(d.reclamacao.getConteudo().replace("\"", "\\\"")).append("\",")
                .append("\"status\": \"").append(statusParaTexto(d.reclamacao.getStatusReclamacao())).append("\"")
                .append("}");
            if (i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
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
                case "adicionar":
                    executarAdicao(req, res);
                    break;
                    
                case "atualizarStatus":
                    executarAtualizacaoStatus(req, res);
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

    // 📩 1. Cliente adicionando uma reclamação
    private void executarAdicao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String texto = extrairCampoJson(jsonBody, "texto");
        String idEstadiaStr = extrairCampoJson(jsonBody, "idEstadia");

        if (texto.isEmpty() || idEstadiaStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Campos obrigatórios ausentes.\"}");
            return;
        }

        int idEstadia = Integer.parseInt(idEstadiaStr);

        // Instancia a dependência necessária para a model
        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setId(idEstadia);


        StatusReclamacaoEnum statusInicial = StatusReclamacaoEnum.EM_ANALISE; 
        try {
            statusInicial = StatusReclamacaoEnum.valueOf("EM_ANALISE");
        } catch(Exception e) {
            // Caso seu enum use outro padrão texturizado, ele pega o primeiro disponível
            statusInicial = StatusReclamacaoEnum.values()[0]; 
        }

        Reclamacao novaReclamacao = new Reclamacao(0, texto, statusInicial, estadia);
        
        reclamacaoDao.adicionarReclamacao(novaReclamacao);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Reclamação enviada com sucesso!\"}");
    }

    // 🔄 2. Gerente atualizando o status da reclamação
    private void executarAtualizacaoStatus(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String idStr = extrairCampoJson(jsonBody, "id");
        String novoStatusStr = extrairCampoJson(jsonBody, "status");

        if (idStr.isEmpty() || novoStatusStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID e status são obrigatórios.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        
        // Trata strings como "Em análise" ou "Não resolvido" para o padrão UPPER_CASE de Enums (EM_ANALISE / NAO_RESOLVIDO)
        String formatoEnum = novoStatusStr.toUpperCase()
                                         .replace(" ", "_")
                                         .replace("Á", "A")
                                         .replace("Ã", "A");
        
        StatusReclamacaoEnum status = StatusReclamacaoEnum.valueOf(formatoEnum);

        Reclamacao reclamacaoAtualizar = new Reclamacao(id, null, status, null);
        
        // Executa a procedure {CALL UpdateReclamacaoStatus(?,?)}
        reclamacaoDao.updateStatusReclamacao(reclamacaoAtualizar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Status atualizado com sucesso!\"}");
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