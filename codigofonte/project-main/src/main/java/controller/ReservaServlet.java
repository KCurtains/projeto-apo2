package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservaDao;
import dao.VeiculoDao;
import model.Patio;
import model.Reserva;
import model.Vaga;
import model.Usuario;
import model.Veiculo;
import Enum.TipoVeiculoEnum;
import javax.servlet.http.HttpSession;

@WebServlet("/reserva") 
public class ReservaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservaDao reservaDao = new ReservaDao();
    private VeiculoDao veiculoDao = new VeiculoDao();

    public ReservaServlet() {
        super();
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        HttpSession session = req.getSession(false);

        // reservas marcadas para hoje
        if ("listarDoDia".equals(acao)) {
            if (session == null || session.getAttribute("funcionarioLogado") == null) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("{\"erro\": \"Acesso restrito a funcionários.\"}");
                return;
            }
            try {
                List<Reserva> reservas = reservaDao.listarReservasDoDia();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < reservas.size(); i++) {
                    Reserva r = reservas.get(i);
                    String veiculo = r.getVeiculo().getModelo() + " (" + r.getVeiculo().getCor() + ") - " + r.getVeiculo().getPlaca();
                    json.append("{")
                        .append("\"id\": ").append(r.getId()).append(",")
                        .append("\"veiculo\": \"").append(veiculo.replace("\"", "\\\"")).append("\",")
                        .append("\"patio\": \"").append(r.getPatio().getEndereco().replace("\"", "\\\"")).append("\",")
                        .append("\"horaEntrada\": \"").append(r.getHorarioEntrada().format(fmt)).append("\",")
                        .append("\"horaSaida\": \"").append(r.getHorarioSaida().format(fmt)).append("\",")
                        .append("\"valor\": \"R$ ").append(String.format("%.2f", r.getValor())).append("\"")
                        .append("}");
                    if (i < reservas.size() - 1) json.append(",");
                }
                json.append("]");
                res.getWriter().write(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("[]");
            }
            return;
        }

        // reservas do cliente
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"erro\": \"Usuário não autenticado.\"}");
            return;
        }

        try {
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            int clienteLogadoId = usuarioLogado.getId();
            List<Reserva> reservas = reservaDao.listarReservasPorCliente(clienteLogadoId);

            StringBuilder json = new StringBuilder("[");
            // Formatar a data para o padrão do frontend: "23/04/2026"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                json.append("{")
                    .append("\"id\": ").append(r.getId()).append(",")
                    // Mais pra frente trazer os nomes por meio de um JOIN
                    .append("\"veiculo\": \"Veículo ID ").append(r.getVeiculo().getId()).append("\",")
                    .append("\"patio\": \"Pátio ID ").append(r.getPatio().getId()).append("\",")
                    .append("\"data\": \"").append(r.getHorarioEntrada().format(formatter)).append("\",")
                    .append("\"valor\": \"R$ ").append(String.format("%.2f", r.getValor())).append("\"")
                    .append("}");

                if (i < reservas.size() - 1) json.append(",");
            }
            json.append("]");

            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("[]");
        }
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
                case "criar":
                    executarCriacao(req, res);
                    break;
                    
                case "cancelar":
                    executarCancelamento(req, res);
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

    // cria agendamento
    private void executarCriacao(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String jsonBody = lerCorpoRequisicao(req);

        String patioIdStr = extrairCampoJson(jsonBody, "patio");
        String veiculoIdStr = extrairCampoJson(jsonBody, "veiculo");
        String dataEntradaStr = extrairCampoJson(jsonBody, "dataEntrada"); // Ex: "2026-04-23 12:30"
        String dataSaidaStr = extrairCampoJson(jsonBody, "dataSaida");

        if (patioIdStr.isEmpty() || veiculoIdStr.isEmpty() || dataEntradaStr.isEmpty() || dataSaidaStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Todos os campos são obrigatórios.\"}");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime entrada = LocalDateTime.parse(dataEntradaStr, formatter);
        LocalDateTime saida = LocalDateTime.parse(dataSaidaStr, formatter);

        int patioId = Integer.parseInt(patioIdStr);
        int veiculoId = Integer.parseInt(veiculoIdStr);


        Veiculo veiculoReal = veiculoDao.buscarPorId(veiculoId);
        if (veiculoReal == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Veículo não encontrado.\"}");
            return;
        }

        Integer vagaId = veiculoDao.buscarVagaDisponivel(patioId, veiculoReal.getTipoVeiculo().name());
        if (vagaId == null) {
            res.setStatus(HttpServletResponse.SC_CONFLICT);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"Não há vagas disponíveis nesse pátio para o tipo de veículo selecionado.\"}");
            return;
        }

        Patio patio = new Patio(patioId, "", 0, 0, 0);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(veiculoId);

        Vaga vaga = new Vaga();
        vaga.setId(vagaId);

        float valor = calcularValor(veiculoReal.getTipoVeiculo(), entrada, saida);

        Reserva novaReserva = new Reserva(0, entrada, saida, valor, null, patio, veiculo, vaga);


        reservaDao.criarReserva(novaReserva);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Reserva realizada com sucesso! Valor: R$ "
                + String.format("%.2f", valor) + "\"}");
    }


    //   CARRO:    R$ 10/hora  | diária R$ 50
    //   MOTO:     R$  5/hora  | diária R$ 30
    //   CAMINHAO: R$ 20/hora  | diária R$ 90
    private float calcularValor(TipoVeiculoEnum tipo, LocalDateTime entrada, LocalDateTime saida) {
        float valorHora;
        float valorDiaria;
        switch (tipo) {
            case MOTO:
                valorHora = 5f;
                valorDiaria = 30f;
                break;
            case CAMINHAO:
                valorHora = 20f;
                valorDiaria = 90f;
                break;
            case CARRO:
            default:
                valorHora = 10f;
                valorDiaria = 50f;
                break;
        }

        long minutos = Duration.between(entrada, saida).toMinutes();
        if (minutos < 0) minutos = 0;
        double horas = minutos / 60.0;

        if (horas >= 12) {
            long dias = (long) Math.ceil(horas / 24.0);
            if (dias < 1) dias = 1;
            return dias * valorDiaria;
        }

        long horasCobradas = (long) Math.ceil(horas);
        if (horasCobradas < 1) horasCobradas = 1;
        return horasCobradas * valorHora;
    }


    private void executarCancelamento(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String idStr = req.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da reserva não informado.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        Reserva reservaCancelar = new Reserva();
        reservaCancelar.setId(id);


        reservaDao.cancelarReserva(reservaCancelar);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Reserva cancelada com sucesso!\"}");
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