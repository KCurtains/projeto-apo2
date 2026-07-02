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

import dao.RegistroEstadiaDao;
import model.RegistroEstadia;
import model.Reserva;
import model.Usuario;

@WebServlet("/estadia") // Rota unificada seguindo o padrão do projeto
public class RegistroEstadiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RegistroEstadiaDao estadiaDao = new RegistroEstadiaDao();
    private static final DateTimeFormatter FMT_DATAHORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public RegistroEstadiaServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        HttpSession session = req.getSession(false);

        try {
            // 👷 Funcionário: estadias com veículos ainda dentro do pátio
            if ("listarAndamento".equals(acao)) {
                if (session == null || session.getAttribute("funcionarioLogado") == null) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Acesso restrito a funcionários.\"}");
                    return;
                }
                res.getWriter().write(montarJsonAndamento(estadiaDao.listarEmAndamento()));
                return;
            }

            // 🙋 Cliente: suas próprias estadias (usado para escolher a estadia de uma reclamação)
            if ("listarMinhas".equals(acao)) {
                if (session == null || session.getAttribute("usuarioLogado") == null) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Usuário não autenticado.\"}");
                    return;
                }
                int clienteId = ((Usuario) session.getAttribute("usuarioLogado")).getId();
                res.getWriter().write(montarJsonMinhas(estadiaDao.listarEstadiasPorCliente(clienteId)));
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

    private String montarJsonAndamento(List<RegistroEstadia> lista) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            RegistroEstadia e = lista.get(i);
            Reserva r = e.getReserva();
            String veiculo = r.getVeiculo().getModelo() + " (" + r.getVeiculo().getCor() + ") - " + r.getVeiculo().getPlaca();
            json.append("{")
                .append("\"id\": ").append(e.getId()).append(",")
                .append("\"veiculo\": \"").append(veiculo.replace("\"", "\\\"")).append("\",")
                .append("\"patio\": \"").append(escapar(r.getPatio().getEndereco())).append("\",")
                .append("\"horaEntrada\": \"").append(e.getHorarioEntradaReal().format(FMT_DATAHORA)).append("\",")
                .append("\"horaSaida\": \"-\",")
                .append("\"valor\": \"R$ ").append(String.format("%.2f", r.getValor())).append("\"")
                .append("}");
            if (i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String montarJsonMinhas(List<RegistroEstadia> lista) {
        DateTimeFormatter fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            RegistroEstadia e = lista.get(i);
            Reserva r = e.getReserva();
            String veiculo = r.getVeiculo().getModelo() + " (" + r.getVeiculo().getPlaca() + ")";
            json.append("{")
                .append("\"id\": ").append(e.getId()).append(",")
                .append("\"veiculo\": \"").append(veiculo.replace("\"", "\\\"")).append("\",")
                .append("\"data\": \"").append(e.getHorarioEntradaReal().format(fmtData)).append("\"")
                .append("}");
            if (i < lista.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String escapar(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
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
                case "validarEntrada":
                    executarValidarEntrada(req, res);
                    break;
                    
                case "registrarSaida":
                    executarRegistrarSaida(req, res);
                    break;
                    
                case "calcularHoras":
                    executarCalcularHoras(req, res);
                    break;
                    
                case "processarPagamento":
                    executarProcessarPagamento(req, res);
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

    // 📥 1. Validar Entrada do veículo usando o ID da Reserva
    private void executarValidarEntrada(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        String reservaIdStr = extrairCampoJson(jsonBody, "reservaId");

        if (reservaIdStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da reserva é obrigatório para entrada.\"}");
            return;
        }

        int reservaId = Integer.parseInt(reservaIdStr);

        // Instancia as dependências necessárias da model
        Reserva reserva = new Reserva();
        reserva.setId(reservaId); // Certifique-se de que sua model Reserva possui setId(int)

        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setReserva(reserva);

        // Executa a procedure {CALL ValidarEntrada(?)}
        estadiaDao.validarEntrada(estadia);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Entrada do veículo validada com sucesso!\"}");
    }

    // 📤 2. Registrar Saída do veículo usando o ID da Estadia
    private void executarRegistrarSaida(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        String estadiaIdStr = extrairCampoJson(jsonBody, "id"); // Captura o ID enviado pelo AJAX

        if (estadiaIdStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da estadia é obrigatório para saída.\"}");
            return;
        }

        int estadiaId = Integer.parseInt(estadiaIdStr);

        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setId(estadiaId);

        // Executa a procedure {CALL RegistrarSaida(?)}
        estadiaDao.registrarSaida(estadia);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Saída registrada com sucesso!\"}");
    }

    // ⏱️ 3. Consultar tempo de permanência acumulado
    private void executarCalcularHoras(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        String estadiaIdStr = extrairCampoJson(jsonBody, "id");

        if (estadiaIdStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da estadia é obrigatório.\"}");
            return;
        }

        int estadiaId = Integer.parseInt(estadiaIdStr);
        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setId(estadiaId);

        // Executa a procedure com parâmetro OUT
        double horas = estadiaDao.calcularHoras(estadia);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"totalHoras\": " + horas + "}");
    }

    // 💳 4. Concluir processamento financeiro
    private void executarProcessarPagamento(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);
        String reservaIdStr = extrairCampoJson(jsonBody, "reservaId");

        if (reservaIdStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da reserva é obrigatório para o pagamento.\"}");
            return;
        }

        int reservaId = Integer.parseInt(reservaIdStr);

        Reserva reserva = new Reserva();
        reserva.setId(reservaId);

        RegistroEstadia estadia = new RegistroEstadia();
        estadia.setReserva(reserva);

        // Executa a procedure {CALL ProcessarPagamento(?)}
        estadiaDao.processarPagamento(estadia);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Pagamento processado com sucesso!\"}");
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
            
            // Suporta formatos estruturados com dois pontos ':' comuns em envios JSON
            if (json.charAt(inicio) == ':') {
                inicio++;
            }
            
            inicio = json.indexOf("\"", inicio) + 1;
            
            // Fallback caso o número seja enviado direto sem aspas no JSON numérico
            if (inicio == 0) {
                int indexDoisPontos = json.indexOf(":", json.indexOf(chave)) + 1;
                int fimNum = json.indexOf(",", indexDoisPontos);
                if (fimNum == -1) fimNum = json.indexOf("}", indexDoisPontos);
                return json.substring(indexDoisPontos, fimNum).trim();
            }
            
            int fim = json.indexOf("\"", inicio);
            return json.substring(inicio, fim);
        } catch (Exception e) {
            return "";
        }
    }
}