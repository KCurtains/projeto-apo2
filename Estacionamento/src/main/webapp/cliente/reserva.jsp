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

        <div class="header-title container">
            Suas Reservas
        </div>

        <div class="content-area container">
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Você não tem reservas. Clique no<br>botão + para realizar sua primeira<br>reserva.</p>
            </div>
            <div id="list-state"></div>
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
                        <select class="form-select mb-2" id="resPátio" required>
                            <option value="" disabled selected>Selecione o pátio...</option>
                        </select>
                        <div class="text-end text-success small d-none mb-2" id="status-vagas" style="font-weight: bold;">Disponível</div>

                        <label class="small text-muted">Veículo</label>
                        <select class="form-select mb-2" id="resVeiculo" required>
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
                    <div class="mb-3"><small class="text-muted d-block">Veículo</small><strong id="infoVeiculo"></strong></div>
                    <div class="mb-3"><small class="text-muted d-block">Pátio</small><strong id="infoPatio"></strong></div>
                    <div class="mb-3"><small class="text-muted d-block">Horário de Entrada</small><strong id="infoEntrada"></strong></div>
                    <div class="mb-3"><small class="text-muted d-block">Horário de Saída</small><strong id="infoSaida"></strong></div>
                    <div class="mb-4"><small class="text-muted d-block">Valor Calculado</small><strong id="infoValor"></strong></div>
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
        let reservasCarregadas = [];
        let veiculosCarregados = [];

        // Tarifas usadas só para a ESTIMATIVA mostrada na tela — o valor final e
        // oficial é sempre calculado e conferido pelo servidor.
        const TARIFAS = {
            CARRO:    { hora: 10, diaria: 50 },
            MOTO:     { hora: 5,  diaria: 30 },
            CAMINHAO: { hora: 20, diaria: 90 }
        };

        $(document).ready(function() {
            carregarReservas();

            // 🔍 Reconsulta a disponibilidade de vagas sempre que o pátio OU o
            // veículo mudam, usando o TIPO do veículo selecionado (antes estava
            // fixo em "CARRO", então mostrava vaga errada para moto/caminhão).
            $('#resPátio, #resVeiculo').on('change', function() {
                verificarDisponibilidade();
                atualizarValorEstimado();
            });
            $('#resDataEntrada, #resHoraEntrada, #resDataSaida, #resHoraSaida').on('change', function() {
                atualizarValorEstimado();
            });
        });

        function verificarDisponibilidade() {
            const idPatio = $('#resPátio').val();
            const idVeiculo = $('#resVeiculo').val();
            if (!idPatio || !idVeiculo) return;

            const veiculo = veiculosCarregados.find(v => String(v.id) === String(idVeiculo));
            const tipo = veiculo ? veiculo.tipoVeiculo : 'CARRO';

            $.ajax({
                url: '../patio?acao=verificarDisponibilidade&id=' + idPatio + '&tipoVeiculo=' + tipo,
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    if(response.vagasDisponiveis > 0) {
                        $('#status-vagas').text("Disponível (" + response.vagasDisponiveis + " vagas)")
                                         .removeClass('d-none text-danger').addClass('text-success');
                    } else {
                        $('#status-vagas').text("Pátio lotado para esse tipo de veículo")
                                         .removeClass('d-none text-success').addClass('text-danger');
                    }
                },
                error: function() {
                    $('#status-vagas').addClass('d-none');
                }
            });
        }

        function atualizarValorEstimado() {
            const idVeiculo = $('#resVeiculo').val();
            const veiculo = veiculosCarregados.find(v => String(v.id) === String(idVeiculo));
            const dataEntrada = $('#resDataEntrada').val(), horaEntrada = $('#resHoraEntrada').val();
            const dataSaida = $('#resDataSaida').val(), horaSaida = $('#resHoraSaida').val();

            if (!veiculo || !dataEntrada || !horaEntrada || !dataSaida || !horaSaida) return;

            const entrada = new Date(dataEntrada + 'T' + horaEntrada);
            const saida = new Date(dataSaida + 'T' + horaSaida);
            const horas = (saida - entrada) / (1000 * 60 * 60);
            if (isNaN(horas) || horas <= 0) return;

            const tarifa = TARIFAS[veiculo.tipoVeiculo] || TARIFAS.CARRO;
            let valor;
            if (horas >= 12) {
                const dias = Math.ceil(horas / 24);
                valor = dias * tarifa.diaria;
            } else {
                valor = Math.ceil(horas) * tarifa.hora;
            }

            $('#resValorTotal').text('R$ ' + valor.toFixed(2).replace('.', ','));
        }

        function carregarReservas() {
            $.ajax({
                url: '../reserva', // Bate no seu doGet
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    reservasCarregadas = response;
                    renderizarLista(response); // Passa os dados reais para a sua função de renderizar
                },
                error: function(xhr) {
                    console.error("Erro ao buscar as reservas:", xhr.responseText);
                    $('#empty-state').removeClass('d-none');
                    $('#list-state').addClass('d-none');
                }
            });
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
                        <div class="item-card">
                            <div class="item-card-text">
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

        function popularSelects() {
            $.ajax({
                url: '../patio?acao=listar',
                type: 'GET',
                dataType: 'json',
                success: function(patios) {
                    const select = $('#resPátio');
                    select.find('option:not(:first)').remove();
                    patios.forEach(p => {
                        select.append('<option value="' + p.id + '">' + p.nome + '</option>');
                    });
                }
            });

            $.ajax({
                url: '../veiculo',
                type: 'GET',
                dataType: 'json',
                success: function(veiculos) {
                    veiculosCarregados = veiculos;
                    const select = $('#resVeiculo');
                    select.find('option:not(:first)').remove();
                    veiculos.forEach(v => {
                        select.append('<option value="' + v.id + '">' + v.modelo + ' (' + v.placa + ')</option>');
                    });
                }
            });
        }

        function salvarReserva() {

            const dadosReserva = {
                patio: $('#resPátio').val(),
                veiculo: $('#resVeiculo').val(),

                dataEntrada: $('#resDataEntrada').val() + " " + $('#resHoraEntrada').val(),
                dataSaida: $('#resDataSaida').val() + " " + $('#resHoraSaida').val()
            };

            if (!dadosReserva.patio || !dadosReserva.veiculo || !$('#resDataEntrada').val() || !$('#resDataSaida').val()) {
                alert("Por favor, preencha todos os campos para fazer a reserva.");
                return;
            }

            $.ajax({
                url: '../reserva?acao=criar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosReserva),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    $('#modalCriarReserva').modal('hide');
                    carregarReservas();
                },
                error: function(xhr) {
                    alert("Não foi possível criar a reserva: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        function efetivarCancelamento() {
            if (!idReservaSelecionada) {
                alert("Nenhuma reserva foi selecionada para exclusão.");
                return;
            }

            $.ajax({
                url: '../reserva?acao=cancelar&id=' + idReservaSelecionada,
                type: 'POST',
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    $('#modalConfirmarCancelamento').modal('hide');
                    $('#modalInfoReserva').modal('hide');
                    carregarReservas();
                },
                error: function(xhr) {
                    alert("Não foi possível cancelar: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                    $('#modalConfirmarCancelamento').modal('hide');
                }
            });
        }

        function abrirModalCriar() {
            $('#form-nova-reserva')[0].reset();
            $('#status-vagas').addClass('d-none');
            $('#resValorTotal').text('R$ 0,00');
            popularSelects();
            new bootstrap.Modal(document.getElementById('modalCriarReserva')).show();
        }

        function abrirModalInfo(id) {
            idReservaSelecionada = id;
            const res = reservasCarregadas.find(r => r.id === id);
            if (res) {
                $('#infoVeiculo').text(res.veiculo);
                $('#infoPatio').text(res.patio);
                $('#infoEntrada').text(res.data);
                $('#infoSaida').text('-');
                $('#infoValor').text(res.valor);
            }
            new bootstrap.Modal(document.getElementById('modalInfoReserva')).show();
        }
        function abrirModalCancelar(id) { if(id) idReservaSelecionada = id; new bootstrap.Modal(document.getElementById('modalConfirmarCancelamento')).show(); }
    </script>
</body>
</html>
