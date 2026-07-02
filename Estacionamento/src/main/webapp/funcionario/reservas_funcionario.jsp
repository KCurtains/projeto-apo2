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
            Reservas de Hoje
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
        let reservasCarregadas = [];

        $(document).ready(function() {
            carregarReservasDoDia();
        });

        function carregarReservasDoDia() {
            $.ajax({
                url: '../reserva?acao=listarDoDia',
                type: 'GET',
                dataType: 'json',
                success: function(reservas) {
                    reservasCarregadas = reservas;
                    const listDiv = $('#list-state');
                    listDiv.empty();

                    if(reservas.length === 0) {
                        $('#empty-state').removeClass('d-none');
                        $('#list-state').addClass('d-none');
                    } else {
                        $('#empty-state').addClass('d-none');
                        $('#list-state').removeClass('d-none');

                        reservas.forEach(res => {
                            const cardHtml = `
                                <div class="reserva-card d-flex justify-content-between align-items-center">
                                    <div>
                                        <small class="text-muted d-block">`+ res.veiculo +`</small>
                                        <strong>`+ res.horaEntrada +`</strong>
                                    </div>
                                    <div>
                                        <button class="btn btn-icon-orange btn-sm rounded-3 me-1"
                                            onclick="abrirModalInfo(`+ res.id +`)">
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
                },
                error: function(xhr) {
                    console.error("Erro ao buscar as reservas do dia:", xhr.responseText);
                    $('#empty-state').removeClass('d-none');
                    $('#list-state').addClass('d-none');
                }
            });
        }

        function abrirModalInfo(id) {
            const res = reservasCarregadas.find(r => r.id === id);
            if (!res) return;

            $('#infoReservaId').val(id);
            $('#infoVeiculo').text(res.veiculo);
            $('#infoPatio').text(res.patio);
            $('#infoEntrada').text(res.horaEntrada);
            $('#infoSaida').text(res.horaSaida);
            $('#infoValor').text(res.valor);

            var myModal = new bootstrap.Modal(document.getElementById('modalInfoReservaFuncionario'));
            myModal.show();
        }

        function registrarEntrada() {
            const idReserva = $('#infoReservaId').val();

            $.ajax({
                url: '../estadia?acao=validarEntrada',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ reservaId: idReserva }),
                dataType: 'json',
                success: function(response) {
                    alert("Entrada registrada com sucesso!");
                    $('#modalInfoReservaFuncionario').modal('hide');
                    carregarReservasDoDia();
                },
                error: function(xhr) {
                    alert("Não foi possível registrar a entrada: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        function cancelarOuBloquearReserva(id) {
            if(confirm("Deseja realmente cancelar/bloquear esta reserva?")) {
                $.ajax({
                    url: '../reserva?acao=cancelar&id=' + id,
                    type: 'POST',
                    dataType: 'json',
                    success: function() {
                        alert("Reserva atualizada!");
                        carregarReservasDoDia();
                    },
                    error: function(xhr) {
                        alert("Não foi possível atualizar a reserva: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                    }
                });
            }
        }
    </script>
</body>
</html>
