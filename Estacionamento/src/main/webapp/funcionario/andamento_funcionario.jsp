<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Estadias em Andamento</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/funcionario.css">
</head>
<body>

    <div class="app-container">

        <div class="header-title container-md">
            Estadias em Andamento
        </div>

        <div class="content-area container-md">
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Não há veículos no pátio no momento.</p>
            </div>
            <div id="list-state"></div>
        </div>

        <div class="bottom-nav">
            <a href="reservas_funcionario.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="andamento_funcionario.jsp" class="nav-item active"><i class="bi bi-exclamation-circle"></i></a>
            <a href="pesquisa_funcionario.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil_funcionario.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalInfoEstadia" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Estadia</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="infoEstadiaId">

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
                <div class="modal-footer flex-column pb-4 gap-2">
                    <button type="button" class="btn btn-orange w-100" onclick="registrarSaida()">Registrar Saída</button>
                    <button type="button" class="btn btn-red w-100" onclick="aplicarMultaManual()">Aplicar Multa por Infração</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        let estadiasCarregadas = [];

        $(document).ready(function() {
            carregarEstadias();
        });

        function carregarEstadias() {
            $.ajax({
                url: '../estadia?acao=listarAndamento',
                type: 'GET',
                dataType: 'json',
                success: function(estadias) {
                    estadiasCarregadas = estadias;
                    const listDiv = $('#list-state');
                    listDiv.empty();

                    if(estadias.length === 0) {
                        $('#empty-state').removeClass('d-none');
                        $('#list-state').addClass('d-none');
                    } else {
                        $('#empty-state').addClass('d-none');
                        $('#list-state').removeClass('d-none');

                        estadias.forEach(estadia => {
                            const cardHtml = `
                                <div class="item-card">
                                    <div class="item-card-text">
                                        <small class="text-muted d-block">`+ estadia.veiculo +`</small>
                                        <strong>`+ estadia.horaEntrada +`</strong>
                                    </div>
                                    <div>
                                        <button class="btn btn-icon-orange btn-sm rounded-3"
                                            onclick="abrirModalSaida(`+ estadia.id +`)">
                                            <i class="bi bi-info-circle"></i>
                                        </button>
                                    </div>
                                </div>
                            `;
                            listDiv.append(cardHtml);
                        });
                    }
                },
                error: function(xhr) {
                    console.error("Erro ao buscar estadias:", xhr.responseText);
                    $('#empty-state').removeClass('d-none');
                    $('#list-state').addClass('d-none');
                }
            });
        }

        function abrirModalSaida(id) {
            const estadia = estadiasCarregadas.find(e => e.id === id);
            if (!estadia) return;

            $('#infoEstadiaId').val(id);
            $('#infoVeiculo').text(estadia.veiculo);
            $('#infoPatio').text(estadia.patio);
            $('#infoEntrada').text(estadia.horaEntrada);
            $('#infoSaida').text(estadia.horaSaida);
            $('#infoValor').text(estadia.valor);

            var myModal = new bootstrap.Modal(document.getElementById('modalInfoEstadia'));
            myModal.show();
        }

        function registrarSaida() {
            const idEstadia = $('#infoEstadiaId').val();

            $.ajax({
            	url: '../estadia?acao=registrarSaida',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: idEstadia, acao: 'registrar_saida' }),
                dataType: 'json',
                success: function(response) {
                    alert("Saída registrada com sucesso!");
                    $('#modalInfoEstadia').modal('hide');
                    carregarEstadias();
                },
                error: function(xhr) {
                    alert("Não foi possível registrar a saída: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        function aplicarMultaManual() {
            const idEstadia = $('#infoEstadiaId').val();
            const motivoDigitado = prompt("Digite o motivo da multa (ex: Perda de ticket, Vaga dupla):");

            if (!motivoDigitado) return;

            const valorDigitado = prompt("Digite o valor da multa (ex: 50.00):");
            if (!valorDigitado) return;

            const dadosMulta = {
                valor: valorDigitado,
                motivo: motivoDigitado,
                estadiaId: idEstadia
            };

            $.ajax({
                url: '../multa?acao=adicionar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosMulta),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                },
                error: function(xhr) {
                    alert("Não foi possível aplicar a multa: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }
    </script>
</body>
</html>
