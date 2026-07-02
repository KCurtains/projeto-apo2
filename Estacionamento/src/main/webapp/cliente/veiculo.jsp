<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Veículos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/cliente.css">
</head>
<body>

    <div class="app-container">

        <div id="view-veiculos" class="h-100 d-flex flex-column w-100">
            <div class="header-title">Veículos</div>
            <div class="content-area" id="list-veiculos"></div>
            <div class="fab" onclick="abrirModalAddVeiculo()">+</div>
        </div>

        <div id="view-motoristas" class="h-100 flex-column w-100 d-none">
            <div class="header-title">
                <i class="bi bi-arrow-left btn-back-header" onclick="voltarParaVeiculos()"></i>
                Motoristas Autorizados
            </div>
            <div class="content-area container" id="list-motoristas"></div>
            <div class="fab" onclick="abrirModalAddMotorista()">+</div>
        </div>

        <div class="bottom-nav">
            <a href="reserva.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="reclamacao.jsp" class="nav-item"><i class="bi bi-exclamation-circle"></i></a>
            <a href="veiculo.jsp" class="nav-item active"><i class="bi bi-car-front"></i></a>
            <a href="perfil.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>


    </div>

    <div class="modal fade" id="modalAddVeiculo" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header"><h5 class="modal-title fw-bold">Adicionar Veículo</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><form id="form-add-veiculo">
        <label class="small text-muted">Modelo</label><input type="text" class="form-control" id="addVeiModelo" required>
        <label class="small text-muted">Placa</label><input type="text" class="form-control" id="addVeiPlaca" required>
        <label class="small text-muted">Cor</label><input type="text" class="form-control" id="addVeiCor" required>
        <label class="small text-muted">Tipo</label>
        <select class="form-select" id="addVeiTipo" required>
            <option value="" disabled selected>Selecione o tipo...</option>
            <option value="CARRO">Carro</option>
            <option value="MOTO">Moto</option>
            <option value="CAMINHAO">Caminhão</option>
        </select>
        </form></div><div class="modal-footer pb-4"><button type="button" class="btn btn-orange" onclick="salvarNovoVeiculo()">Adicionar Veículo</button></div></div></div>
    </div>

    <div class="modal fade" id="modalEditVeiculo" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header"><h5 class="modal-title fw-bold">Editar Veículo</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><form id="form-edit-veiculo">
        <input type="hidden" id="editVeiId">
        <label class="small text-muted">Modelo</label><input type="text" class="form-control" id="editVeiModelo" required>
        <label class="small text-muted">Placa</label><input type="text" class="form-control" id="editVeiPlaca" readonly>
        <label class="small text-muted">Cor</label><input type="text" class="form-control" id="editVeiCor" required>
        <label class="small text-muted">Tipo</label>
        <select class="form-select" id="editVeiTipo" required>
            <option value="CARRO">Carro</option>
            <option value="MOTO">Moto</option>
            <option value="CAMINHAO">Caminhão</option>
        </select>
        </form></div><div class="modal-footer flex-column pb-4 gap-2"><button type="button" class="btn btn-orange" onclick="atualizarVeiculo()">Salvar</button><button type="button" class="btn btn-red" onclick="removerVeiculo()">Remover Veículo</button></div></div></div>
    </div>

    <div class="modal fade" id="modalAddMotorista" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Autorizar Motorista</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="form-add-motorista">
                        <label class="small text-muted">CPF do motorista (já cadastrado no EasyParking)</label>
                        <input type="text" class="form-control" id="addMotCpf" required>
                        <p class="text-muted small mt-2" style="font-size: 0.75rem;">
                            O motorista precisa já ter uma conta de cliente cadastrada no sistema.
                        </p>
                    </form>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="salvarNovoMotorista()">Autorizar Motorista</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalConfirmarExclusaoMotorista" tabindex="-1" aria-hidden="true" style="z-index: 1060;">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content text-center p-4" style="border: 1px solid #ccc; box-shadow: 0 4px 15px rgba(0,0,0,0.2);">
                <h6 class="fw-bold mb-4 mt-2">Deseja remover este motorista?</h6>
                <div class="d-flex justify-content-center gap-3">
                    <button type="button" class="btn btn-red w-50" onclick="efetivarExclusaoMotorista()">SIM</button>
                    <button type="button" class="btn btn-orange w-50" data-bs-dismiss="modal">NÃO</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        let idVeiculoAtual = null;

        $(document).ready(function() {
            carregarVeiculos();
        });

        function carregarVeiculos() {
            $.ajax({
                url: '../veiculo',
                type: 'GET',
                dataType: 'json',
                success: function(veiculos) {
                    const listDiv = $('#list-veiculos');
                    listDiv.empty();

                    if (veiculos.length === 0) {
                        listDiv.append('<p class="text-muted text-center mt-4">Você ainda não tem veículos cadastrados.</p>');
                        return;
                    }

                    veiculos.forEach(vei => {
                        listDiv.append(`
                            <div class="item-card">
                                <div class="item-card-text">
                                    <i class="bi bi-car-front-fill fs-5"></i> `+ vei.modelo +` - `+ vei.placa +`
                                </div>
                                <div>
                                    <button class="btn btn-icon-orange btn-sm rounded-3 me-1" onclick="abrirViewMotoristas(`+ vei.id +`)">
                                        <i class="bi bi-people"></i>
                                    </button>
                                    <button class="btn btn-icon-orange btn-sm rounded-3" onclick='abrirModalEditVeiculo(`+ JSON.stringify(vei) +`)'>
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                </div>
                            </div>
                        `);
                    });
                },
                error: function(xhr) {
                    console.error("Erro ao buscar veículos:", xhr.responseText);
                    $('#list-veiculos').html('<p class="text-muted text-center mt-4">Não foi possível carregar seus veículos.</p>');
                }
            });
        }

        function abrirModalAddVeiculo() { $('#form-add-veiculo')[0].reset(); new bootstrap.Modal(document.getElementById('modalAddVeiculo')).show(); }

        function salvarNovoVeiculo() {
            const dadosVeiculo = {
                modelo: $('#addVeiModelo').val(),
                placa: $('#addVeiPlaca').val(),
                cor: $('#addVeiCor').val(),
                tipoVeiculo: $('#addVeiTipo').val()
            };

            if (!dadosVeiculo.modelo || !dadosVeiculo.placa || !dadosVeiculo.cor || !dadosVeiculo.tipoVeiculo) {
                alert("Preencha todos os campos.");
                return;
            }

            $.ajax({
                url: '../veiculo',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosVeiculo),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem || "Veículo adicionado!");
                    $('#modalAddVeiculo').modal('hide');
                    carregarVeiculos();
                },
                error: function(xhr) {
                    alert("Não foi possível adicionar o veículo: " + (xhr.responseJSON ? (xhr.responseJSON.mensagem || xhr.responseJSON.erro) : "erro no servidor."));
                }
            });
        }

        function abrirModalEditVeiculo(vei) {
            $('#editVeiId').val(vei.id);
            $('#editVeiModelo').val(vei.modelo);
            $('#editVeiPlaca').val(vei.placa);
            $('#editVeiCor').val(vei.cor);
            $('#editVeiTipo').val(vei.tipoVeiculo);
            new bootstrap.Modal(document.getElementById('modalEditVeiculo')).show();
        }

        function atualizarVeiculo() {
            const dadosVeiculo = {
                id: $('#editVeiId').val(),
                modelo: $('#editVeiModelo').val(),
                cor: $('#editVeiCor').val(),
                tipoVeiculo: $('#editVeiTipo').val()
            };

            $.ajax({
                url: '../veiculo?acao=atualizar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosVeiculo),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem || "Veículo atualizado!");
                    $('#modalEditVeiculo').modal('hide');
                    carregarVeiculos();
                },
                error: function(xhr) {
                    alert("Não foi possível atualizar: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        function removerVeiculo() {
            if (!confirm("Deseja realmente remover este veículo?")) return;

            const id = $('#editVeiId').val();
            $.ajax({
                url: '../veiculo?acao=remover',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: id }),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem || "Veículo removido!");
                    $('#modalEditVeiculo').modal('hide');
                    carregarVeiculos();
                },
                error: function(xhr) {
                    alert("Não foi possível remover: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        function abrirViewMotoristas(idVeiculo) {
            idVeiculoAtual = idVeiculo;
            $('#view-veiculos').addClass('d-none').removeClass('d-flex');
            $('#view-motoristas').removeClass('d-none').addClass('d-flex');
            carregarMotoristas(idVeiculo);
        }

        function voltarParaVeiculos() {
            $('#view-motoristas').addClass('d-none').removeClass('d-flex');
            $('#view-veiculos').removeClass('d-none').addClass('d-flex');
            idVeiculoAtual = null;
        }

        function carregarMotoristas(idVeiculo) {
            $.ajax({
                url: '../veiculo?acao=motoristas&veiculoId=' + idVeiculo,
                type: 'GET',
                dataType: 'json',
                success: function(motoristas) {
                    const listDiv = $('#list-motoristas');
                    listDiv.empty();

                    if (motoristas.length === 0) {
                        listDiv.append('<p class="text-muted text-center mt-4">Nenhum motorista autorizado ainda.</p>');
                        return;
                    }

                    motoristas.forEach(mot => {
                        listDiv.append(`
                            <div class="item-card">
                                <div class="item-card-text">
                                    <i class="bi bi-person-fill fs-4"></i>
                                    `+ mot.nome +`
                                </div>
                                <div>
                                    <button class="btn btn-icon-red btn-sm rounded-3" onclick="removerMotorista(`+ mot.id +`)">
                                        <i class="bi bi-slash-circle"></i>
                                    </button>
                                </div>
                            </div>
                        `);
                    });
                },
                error: function(xhr) {
                    console.error("Erro ao buscar motoristas:", xhr.responseText);
                    $('#list-motoristas').html('<p class="text-muted text-center mt-4">Não foi possível carregar os motoristas.</p>');
                }
            });
        }

        function abrirModalAddMotorista() {
            $('#form-add-motorista')[0].reset();
            var myModal = new bootstrap.Modal(document.getElementById('modalAddMotorista'));
            myModal.show();
        }

        function salvarNovoMotorista() {
            const cpf = $('#addMotCpf').val();
            if (!cpf) {
                alert("Informe o CPF do motorista.");
                return;
            }

            $.ajax({
                url: '../veiculo?acao=adicionarMotorista',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ veiculoId: idVeiculoAtual, cpf: cpf }),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    $('#modalAddMotorista').modal('hide');
                    carregarMotoristas(idVeiculoAtual);
                },
                error: function(xhr) {
                    alert("Não foi possível autorizar o motorista: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                }
            });
        }

        let idMotoristaParaExcluir = null;

        function removerMotorista(id) {
            idMotoristaParaExcluir = id;
            var myModal = new bootstrap.Modal(document.getElementById('modalConfirmarExclusaoMotorista'));
            myModal.show();
        }

        function efetivarExclusaoMotorista() {
            $.ajax({
                url: '../veiculo?acao=removerMotorista',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ veiculoId: idVeiculoAtual, clienteId: idMotoristaParaExcluir }),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem || "Motorista removido com sucesso!");
                    $('#modalConfirmarExclusaoMotorista').modal('hide');
                    carregarMotoristas(idVeiculoAtual);
                },
                error: function(xhr) {
                    alert("Não foi possível remover o motorista: " + (xhr.responseJSON ? xhr.responseJSON.mensagem : "erro no servidor."));
                    $('#modalConfirmarExclusaoMotorista').modal('hide');
                }
            });
        }
    </script>
</body>
</html>
