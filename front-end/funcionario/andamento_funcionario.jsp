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
    
    <style>
        body {
            background-color: #f4f4f4;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
        }
        
        .app-container {
            width: 100%;
            background-color: #ffffff;
            position: relative;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            overflow-x: hidden;
        }

        .header-title {
            padding: 20px;
            font-weight: bold;
            font-size: 1.5rem;
        }

        .content-area {
            flex-grow: 1;
            padding: 0 20px 20px 20px;
            overflow-y: auto;
            padding-bottom: 90px; 
        }

        .estadia-card {
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .btn-icon-orange { background-color: #f98825; color: white; border: none; }
        .btn-icon-orange:hover { background-color: #e07a21; color: white; }

        .bottom-nav {
            background-color: #ffffff;
            border-top: 1px solid #ddd;
            display: flex;
            justify-content: space-around;
            padding: 10px 0;
            position: absolute;
            bottom: 0;
            width: 100%;
            z-index: 1000;
        }

        .nav-item {
            color: #333;
            font-size: 1.5rem;
            padding: 5px 15px;
            border-radius: 8px;
            cursor: pointer;
        }

        .nav-item.active {
            background-color: #f98825;
            color: white;
        }

        .modal-content { border-radius: 15px; border: none; }
        .modal-header { border-bottom: none; padding-bottom: 0; }
        .modal-footer { border-top: none; }
        .btn-orange { background-color: #f98825; color: white; font-weight: bold; border-radius: 20px; width: 100%; padding: 10px; border: none; }
        .btn-orange:hover { background-color: #e07a21; color: white; }
    </style>
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

            <div id="list-state">
                </div>

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
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="registrarSaida()">Registrar Saída</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        $(document).ready(function() {
            carregarEstadias();
        });

        function carregarEstadias() {
            const estadiasMock = [
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

            if(estadiasMock.length === 0) {
                $('#empty-state').removeClass('d-none');
                $('#list-state').addClass('d-none');
            } else {
                $('#empty-state').addClass('d-none');
                $('#list-state').removeClass('d-none');

                estadiasMock.forEach(estadia => {
                    const cardHtml = `
                        <div class="estadia-card d-flex justify-content-between align-items-center">
                            <div>
                                <small class="text-muted d-block">`+ estadia.veiculo.split(' (')[0] +`</small>
                                <strong>`+ estadia.data +`</strong>
                            </div>
                            <div>
                                <button class="btn btn-icon-orange btn-sm rounded-3" 
                                    onclick="abrirModalSaida(`+ estadia.id +`, '`+ estadia.veiculo +`', '`+ estadia.patio +`', '`+ estadia.horaEntrada +`', '`+ estadia.horaSaida +`', '`+ estadia.valor +`')">
                                    <i class="bi bi-info-circle"></i>
                                </button>
                            </div>
                        </div>
                    `;
                    listDiv.append(cardHtml);
                });
            }
        }

        function abrirModalSaida(id, veiculo, patio, entrada, saida, valor) {
            $('#infoEstadiaId').val(id);
            $('#infoVeiculo').text(veiculo);
            $('#infoPatio').text(patio);
            $('#infoEntrada').text(entrada);
            $('#infoSaida').text(saida);
            $('#infoValor').text(valor);

            var myModal = new bootstrap.Modal(document.getElementById('modalInfoEstadia'));
            myModal.show();
        }

        function registrarSaida() {
            const idEstadia = $('#infoEstadiaId').val();

            $.ajax({
                url: 'RegistrarSaidaServlet',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: idEstadia, acao: 'registrar_saida' }),
                dataType: 'json',
                success: function(response) {
                    alert("Saída registrada com sucesso!");
                    $('#modalInfoEstadia').modal('hide');
                    carregarEstadias(); 
                },
                error: function() {
                    alert("Simulação: Saída registrada para a estadia ID: " + idEstadia);
                    $('#modalInfoEstadia').modal('hide');
                }
            });
        }
    </script>
</body>
</html>