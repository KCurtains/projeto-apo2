<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Gestão de Reservas</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/funcionario.css">

</head>
<body>

    <div class="app-container">
        
        <div class="header-title container-md" id="titulo-data">
            Reservas - 23/04/2026
        </div>

        <div class="content-area container-md">
            
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Não há reservas marcadas para hoje.</p>
            </div>

            <div id="list-state">
                </div>

        </div>

        <div class="bottom-nav">
            <a href="reservas_funcionario.jsp" class="nav-item active"><i class="bi bi-list-task"></i></a>
            <a href="andamento_funcionario.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="pesquisa_funcionario.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil_funcionario.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalInfoReservaFuncionario" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Reserva</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="infoReservaId">
                    
                    <div class="mb-3">
                        <small class="text-muted d-block">Veículo</small>
                        <strong id="infoVeiculo"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Pátio</small>
                        <strong id="infoPatio"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Horário de Entrada</small>
                        <strong id="infoEntrada"></strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Horário de Saída</small>
                        <strong id="infoSaida"></strong>
                    </div>
                    <div class="mb-4">
                        <small class="text-muted d-block">Valor Calculado</small>
                        <strong id="infoValor"></strong>
                    </div>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="registrarEntrada()">Registrar Entrada</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        $(document).ready(function() {
            carregarReservasDoDia();
        });

        function carregarReservasDoDia() {
            const reservasMock = [
                { 
                    id: 1, 
                    veiculo: "Ford Ka - 2012 (Vermelho)", 
                    data: "23/04/2026", 
                    patio: "Rua Cinco, 123 - São Paulo - SP",
                    horaEntrada: "23/04/2026 às 12:30",
                    horaSaida: "23/04/2026 às 17:00",
                    valor: "R$50,00" 
                }
            ];
            
            const listDiv = $('#list-state');
            listDiv.empty();

            if(reservasMock.length === 0) {
                $('#empty-state').removeClass('d-none');
                $('#list-state').addClass('d-none');
            } else {
                $('#empty-state').addClass('d-none');
                $('#list-state').removeClass('d-none');

                reservasMock.forEach(res => {
                    const cardHtml = `
                        <div class="reserva-card d-flex justify-content-between align-items-center">
                            <div>
                                <small class="text-muted d-block">`+ res.veiculo.split(' (')[0] +`</small>
                                <strong>`+ res.data +`</strong>
                            </div>
                            <div>
                                <button class="btn btn-icon-orange btn-sm rounded-3 me-1" 
                                    onclick="abrirModalInfo(`+ res.id +`, '`+ res.veiculo +`', '`+ res.patio +`', '`+ res.horaEntrada +`', '`+ res.horaSaida +`', '`+ res.valor +`')">
                                    <i class="bi bi-info-circle"></i>
                                </button>
                                <button class="btn btn-icon-red btn-sm rounded-3" onclick="cancelarOuBloquearReserva(`+ res.id +`)">
                                    <i class="bi bi-slash-circle"></i>
                                </button>
                            </div>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function abrirModalInfo(id, veiculo, patio, entrada, saida, valor) {
            $('#infoReservaId').val(id);
            $('#infoVeiculo').text(veiculo);
            $('#infoPatio').text(patio);
            $('#infoEntrada').text(entrada);
            $('#infoSaida').text(saida);
            $('#infoValor').text(valor);

            var myModal = new bootstrap.Modal(document.getElementById('modalInfoReservaFuncionario'));
            myModal.show();
        }

        function registrarEntrada() {
            const idReserva = $('#infoReservaId').val();

            $.ajax({
                url: 'RegistrarEntradaServlet', 
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: idReserva, acao: 'registrar_entrada' }),
                dataType: 'json',
                success: function(response) {
                    alert("Entrada registrada com sucesso!");
                    $('#modalInfoReservaFuncionario').modal('hide');
                    carregarReservasDoDia();
                },
                error: function() {
                    alert("Simulação: Entrada registrada para a reserva ID: " + idReserva);
                    $('#modalInfoReservaFuncionario').modal('hide');
                }
            });
        }

        function cancelarOuBloquearReserva(id) {
            if(confirm("Deseja realmente cancelar/bloquear esta reserva?")) {
                $.ajax({
                    url: 'GerenciarReservaServlet',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ id: id, acao: 'cancelar' }),
                    success: function() {
                        alert("Reserva atualizada!");
                    },
                    error: function() {
                        alert("Simulação: Ação executada na reserva " + id);
                    }
                });
            }
        }
    </script>
</body>
</html>