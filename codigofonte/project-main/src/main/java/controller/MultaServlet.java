package controller;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MultaDao;
import model.Multa;
import model.RegistroEstadia;
import Enum.StatusMultaEnum;

@WebServlet("/multa")
public class MultaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private MultaDao multaDao = new MultaDao();   

    public MultaServlet() {
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

    // aplica multa
    private void executarAdicao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        // extrai json
        String valorStr = extrairCampoJson(jsonBody, "valor");
        String motivo = extrairCampoJson(jsonBody, "motivo");
        String estadiaIdStr = extrairCampoJson(jsonBody, "estadiaId");

        if (valorStr.isEmpty() || motivo.isEmpty() || estadiaIdStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Todos os campos são obrigatórios.\"}");
            return;
        }

        Float valor = Float.parseFloat(valorStr);
        int estadiaId = Integer.parseInt(estadiaIdStr);

        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setId(estadiaId); 

        
        Multa novaMulta = new Multa(0, valor, motivo, StatusMultaEnum.NAO_PAGO, estadia);

        multaDao.adicionarMulta(novaMulta);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Multa aplicada com sucesso!\"}");
    }

    // altera status multa
    private void executarAtualizacaoStatus(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String idStr = extrairCampoJson(jsonBody, "id");
        String novoStatusStr = extrairCampoJson(jsonBody, "status");

        if (idStr.isEmpty() || novoStatusStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID e Status são obrigatórios.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        StatusMultaEnum novoStatus = StatusMultaEnum.valueOf(novoStatusStr.toUpperCase());

        Multa multaAtualizar = new Multa(id, null, null, novoStatus, null);

        multaDao.updateMulta(multaAtualizar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Status da multa atualizado!\"}");
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
