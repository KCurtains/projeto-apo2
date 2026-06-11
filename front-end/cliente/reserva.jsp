<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Reservas</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/cliente.css">
</head>
<body>

    <div class="app-container">
        
        <div class="header-title">
            Suas Reservas
        </div>

        <div class="content-area">
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Você não tem reservas. Clique no<br>botão + para realizar sua primeira<br>reserva.</p>
            </div>

            <div id="list-state">
                </div>
        </div>

        <div class="fab" onclick="abrirModalCriar()">+</div>

        <div class="bottom-nav">
            <a href="reserva.jsp" class="nav-item active"><i class="bi bi-list-task"></i></a>
            <a href="reclamacao.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="veiculo.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalCriarReserva" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Realize sua Reserva</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="form-nova-reserva">
                        <label class="small text-muted">Pátio</label>
                        <select class="form-select" id="resPátio" required>
                            <option value="" disabled selected>Selecione o pátio...</option>
                            </select>
                        <div class="text-end text-success small d-none" id="status-vagas" style="margin-top:-10px; margin-bottom: 10px;">Disponível</div>

                        <label class="small text-muted">Veículo</label>
                        <select class="form-select" id="resVeiculo" required>
                            <option value="" disabled selected>Selecione seu veículo...</option>
                            </select>

                        <div class="row">
                            <div class="col-6">
                                <label class="small text-muted">Entrada</label>
                                <input type="date" class="form-control" id="resDataEntrada" required>
                                <input type="time" class="form-control" id="resHoraEntrada" required>
                            </div>
                            <div class="col-6">
                                <label class="small text-muted">Saída</label>
                                <input type="date" class="form-control" id="resDataSaida" required>
                                <input type="time" class="form-control" id="resHoraSaida" required>
                            </div>
                        </div>

                        <div class="text-center mt-3">
                            <span class="fw-bold">Valor Total: </span>
                            <span class="valor-total-text" id="resValorTotal">R$ 0,00</span>
                        </div>
                        <p class="text-muted small text-center mt-2" style="font-size: 0.65rem;">
                            O valor calculado é referente a quantidade de horas estabelecida. O aumento do tempo na estadia acarretará em um aumento no valor pago após o final do período.
                        </p>
                    </form>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="salvarReserva()">Finalizar Reserva</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalInfoReserva" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Suas Reservas</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <small class="text-muted d-block">Veículo</small>
                        <strong id="infoVeiculo">Ford Ka - 2012 (Vermelho)</strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Pátio</small>
                        <strong id="infoPatio">Rua Cinco, 123 - São Paulo - SP</strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Horário de Entrada</small>
                        <strong id="infoEntrada">23/04/2026 às 12:30</strong>
                    </div>
                    <div class="mb-3">
                        <small class="text-muted d-block">Horário de Saída</small>
                        <strong id="infoSaida">23/04/2026 às 17:00</strong>
                    </div>
                    <div class="mb-4">
                        <small class="text-muted d-block">Valor Calculado</small>
                        <strong id="infoValor">R$50,00</strong>
                    </div>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-red" onclick="abrirModalCancelar()">Cancelar Reserva</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalConfirmarCancelamento" tabindex="-1" aria-hidden="true" style="z-index: 1060;">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content text-center p-4">
                <h6 class="fw-bold mb-4 mt-2">Deseja cancelar sua reserva?</h6>
                <div class="d-flex justify-content-center gap-3">
                    <button type="button" class="btn btn-red w-50" onclick="efetivarCancelamento()">SIM</button>
                    <button type="button" class="btn btn-orange w-50" data-bs-dismiss="modal">NÃO</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>

        let idReservaSelecionada = null;

        $(document).ready(function() {
            carregarReservas();
        });

        function carregarReservas() {
            // Para o seu projeto real, descomente o $.ajax
            /*
            $.ajax({
                url: 'ListarReservasServlet',
                type: 'GET',
                dataType: 'json',
                success: function(reservas) {
                    renderizarLista(reservas);
                },
                error: function(err) { console.error(err); }
            });
            */
            

            const reservasMock = [
                { id: 1, veiculo: "Ford Ka - 2012", data: "23/04/2026", patio: "Rua Cinco, 123", valor: "R$50,00" }
            ];
            

            renderizarLista(reservasMock); 
        }

        function renderizarLista(reservas) {
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
                                <strong>`+ res.data +`</strong>
                            </div>
                            <div>
                                <button class="btn btn-icon-orange btn-sm rounded-3 me-1" onclick="abrirModalInfo(`+ res.id +`)">
                                    <i class="bi bi-info-circle"></i>
                                </button>
                                <button class="btn btn-icon-red btn-sm rounded-3" onclick="abrirModalCancelar(`+ res.id +`)">
                                    <i class="bi bi-slash-circle"></i>
                                </button>
                            </div>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function abrirModalCriar() {
            var myModal = new bootstrap.Modal(document.getElementById('modalCriarReserva'));
            myModal.show();
        }

        function abrirModalInfo(id) {
            idReservaSelecionada = id;
            var myModal = new bootstrap.Modal(document.getElementById('modalInfoReserva'));
            myModal.show();
        }

        function abrirModalCancelar(id) {
            if(id) idReservaSelecionada = id;
            var myModal = new bootstrap.Modal(document.getElementById('modalConfirmarCancelamento'));
            myModal.show();
        }

        function salvarReserva() {
            const dadosReserva = {
                patio: $('#resPátio').val(),
                veiculo: $('#resVeiculo').val(),
                dataEntrada: $('#resDataEntrada').val() + " " + $('#resHoraEntrada').val(),
                dataSaida: $('#resDataSaida').val() + " " + $('#resHoraSaida').val()
            };

            $.ajax({
                url: 'CriarReservaServlet',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosReserva),
                dataType: 'json',
                success: function(response) {
                    alert("Reserva criada com sucesso!");
                    $('#modalCriarReserva').modal('hide');
                    carregarReservas(); 
                },
                error: function() {
                    alert("Simulação: POST disparado para 'CriarReservaServlet'.");
                    $('#modalCriarReserva').modal('hide');
                }
            });
        }

        function efetivarCancelamento() {
            $.ajax({
                url: 'CancelarReservaServlet?id=' + idReservaSelecionada,
                type: 'DELETE',
                dataType: 'json',
                success: function(response) {
                    alert("Reserva cancelada!");
                    $('#modalConfirmarCancelamento').modal('hide');
                    $('#modalInfoReserva').modal('hide'); 
                    carregarReservas();
                },
                error: function() {
                    alert("Simulação: DELETE disparado para a reserva " + idReservaSelecionada);
                    $('#modalConfirmarCancelamento').modal('hide');
                    $('#modalInfoReserva').modal('hide');
                }
            });
        }
    </script>
</body>
</html>