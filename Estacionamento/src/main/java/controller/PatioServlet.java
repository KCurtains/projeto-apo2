package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.PatioDao;
import model.Patio;

@WebServlet("/patio")
public class PatioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatioDao patioDao = new PatioDao();

    public PatioServlet() {
        super();
    }

    // 📥 doGet: Focado em buscar informações (Consultar disponibilidade de vagas)
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        if ("verificarDisponibilidade".equals(acao)) {
            try {
                String idStr = req.getParameter("id");
                String tipoVeiculo = req.getParameter("tipoVeiculo");

                if (idStr == null || tipoVeiculo == null) {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID do pátio e tipo de veículo são obrigatórios.\"}");
                    return;
                }

                int id = Integer.parseInt(idStr);
                // Instancia o pátio usando o construtor existente da sua model
                Patio patio = new Patio(id, 0, 0, 0);

                // Executa a Procedure com parâmetro OUT
                int vagasDisponiveis = patioDao.verificarDisponibilidadeVaga(patio, tipoVeiculo);

                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write("{\"sucesso\": true, \"vagasDisponiveis\": " + vagasDisponiveis + "}");

            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Erro ao verificar disponibilidade.\"}");
            }
        }
    }

    // 📤 doPost: Focado em salvar, alterar ou deletar os pátios do sistema
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

    // ➕ 1. Adicionar Pátio
    private void executarAdicao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        int capCarro = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCarro"));
        int capMoto = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeMoto"));
        int capCaminhao = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCaminhao"));

        // Cria o pátio passando 0 no ID (o banco gera sozinho no auto-incremento da Procedure)
        Patio novoPatio = new Patio(0, capCarro, capMoto, capCaminhao);
        patioDao.adicionarPatio(novoPatio);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio adicionado com sucesso!\"}");
    }

    // 🔄 2. Atualizar Pátio
    private void executarAtualizacao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        int id = Integer.parseInt(extrairCampoJson(jsonBody, "id"));
        int capCarro = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCarro"));
        int capMoto = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeMoto"));
        int capCaminhao = Integer.parseInt(extrairCampoJson(jsonBody, "capacidadeCaminhao"));

        Patio patioAtualizar = new Patio(id, capCarro, capMoto, capCaminhao);
        patioDao.atualizarPatio(patioAtualizar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio atualizado com sucesso!\"}");
    }

    // ❌ 3. Remover Pátio
    private void executarRemocao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        int id = Integer.parseInt(extrairCampoJson(jsonBody, "id"));

        Patio patioRemover = new Patio(id, 0, 0, 0);
        patioDao.removerPatio(patioRemover);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pátio removido do sistema!\"}");
    }

    // 🛡️ Método utilitário para ler o fluxo JSON
    private String lerCorpoRequisicao(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }

    // 🔍 Método utilitário para extrair dados do JSON manualmente
    private String extrairCampoJson(String json, String campo) {
        try {
            String chave = "\"" + campo + "\"";
            if (!json.contains(chave)) return "0";
            int inicio = json.indexOf(chave) + chave.length();
            inicio = json.indexOf(":", inicio) + 1;
            
            // Remove espaços, aspas ou caracteres extras que possam quebrar o Integer.parseInt
            String valorBruto = json.substring(inicio, json.indexOf(json.contains(",") && json.indexOf(",", inicio) > inicio ? "," : "}", inicio)).trim();
            return valorBruto.replace("\"", "");
        } catch (Exception e) {
            return "0";
        }
    }
}