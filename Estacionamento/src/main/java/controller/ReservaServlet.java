package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservaDao;
import model.Patio;
import model.Reserva;
import model.Vaga;
import model.Veiculo;

@WebServlet("/reserva") // Rota unificada seguindo o padrão
public class ReservaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservaDao reservaDao = new ReservaDao();

    public ReservaServlet() {
        super();
    }

    // O doGet pode ser usado para listar as reservas do cliente logado
 // Substitua o seu doGet atual no ReservaServlet
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        try {
            // Simulando o ID do cliente logado como "1" até você ter o esquema de Sessões pronto
            int clienteLogadoId = 1; 
            List<Reserva> reservas = reservaDao.listarReservasPorCliente(clienteLogadoId);

            StringBuilder json = new StringBuilder("[");
            // Formatar a data para o padrão do frontend: "23/04/2026"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                json.append("{")
                    .append("\"id\": ").append(r.getId()).append(",")
                    // Adaptando para mostrar os IDs enquanto não trazemos o nome via JOIN
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

    // 📅 1. Criar Agendamento de Reserva
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

        // Conversão de datas vindas do formato do HTML
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime entrada = LocalDateTime.parse(dataEntradaStr, formatter);
        LocalDateTime saida = LocalDateTime.parse(dataSaidaStr, formatter);

        // Instancia os objetos dependentes mapeando os IDs
        Patio patio = new Patio(Integer.parseInt(patioIdStr), dataSaidaStr, 0, 0, 0);
        Veiculo veiculo = new Veiculo(); 
        veiculo.setId(Integer.parseInt(veiculoIdStr)); // Certifique-se de que sua model Veiculo possui setId(int)
        
        Vaga vaga = new Vaga(); 
        vaga.setId(1); // Mock inicial (Sua procedure trata a busca da vaga livre usando o ID do pátio)

        // Cria o objeto Reserva com valor base fictício (Sua procedure calcula ou recebe do front)
        Reserva novaReserva = new Reserva(0, entrada, saida, 50.0f, null, patio, veiculo, vaga);

        // Executa a procedure {CALL adicionarReserva(?,?,?,?,?,?)}
        reservaDao.criarReserva(novaReserva);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write("{\"sucesso\": true, \"mensagem\": \"Reserva realizada com sucesso!\"}");
    }

    // ❌ 2. Cancelar Reserva Existente
    private void executarCancelamento(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Captura o ID vindo diretamente dos parâmetros da URL (?acao=cancelar&id=1)
        String idStr = req.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"sucesso\": false, \"mensagem\": \"ID da reserva não informado.\"}");
            return;
        }

        int id = Integer.parseInt(idStr);
        Reserva reservaCancelar = new Reserva();
        reservaCancelar.setId(id);

        // Executa a procedure {CALL CancelarReserva(?)}
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