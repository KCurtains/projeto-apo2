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

    <div class="app-container container">
        
        <div id="view-veiculos" class="h-100 d-flex flex-column w-100 container">
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
        <div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header"><h5 class="modal-title fw-bold">Adicionar Veículo</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><form id="form-add-veiculo"><label class="small text-muted">Modelo</label><input type="text" class="form-control" id="addVeiModelo" required><label class="small text-muted">Ano</label><input type="text" class="form-control" id="addVeiAno" required><label class="small text-muted">Placa</label><input type="text" class="form-control" id="addVeiPlaca" required><label class="small text-muted">Cor</label><input type="text" class="form-control" id="addVeiCor" required></form></div><div class="modal-footer pb-4"><button type="button" class="btn btn-orange" onclick="salvarNovoVeiculo()">Adicionar Veículo</button></div></div></div>
    </div>

    <div class="modal fade" id="modalEditVeiculo" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered"><div class="modal-content"><div class="modal-header"><h5 class="modal-title fw-bold">Editar Veículo</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div><div class="modal-body"><form id="form-edit-veiculo"><input type="hidden" id="editVeiId"><label class="small text-muted">Modelo</label><input type="text" class="form-control" id="editVeiModelo" required><label class="small text-muted">Ano</label><input type="text" class="form-control" id="editVeiAno" required><label class="small text-muted">Placa</label><input type="text" class="form-control" id="editVeiPlaca" readonly><label class="small text-muted">Cor</label><input type="text" class="form-control" id="editVeiCor" required></form></div><div class="modal-footer flex-column pb-4 gap-2"><button type="button" class="btn btn-orange" onclick="atualizarVeiculo()">Salvar</button><button type="button" class="btn btn-red" onclick="removerVeiculo()">Remover Veículo</button></div></div></div>
    </div>

    <div class="modal fade" id="modalAddMotorista" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Adicionar Motorista</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="form-add-motorista">
                        <label class="small text-muted">Nome Completo</label>
                        <input type="text" class="form-control" id="addMotNome" required>
                        
                        <label class="small text-muted">CPF</label>
                        <input type="text" class="form-control" id="addMotCpf" required>
                        
                        <label class="small text-muted">Email</label>
                        <input type="email" class="form-control" id="addMotEmail" required>
                        
                        <label class="small text-muted">Número</label>
                        <input type="text" class="form-control" id="addMotNumero" required>
                    </form>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="salvarNovoMotorista()">Adicionar Motorista</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalEditMotorista" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Editar Motorista</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="form-edit-motorista">
                        <input type="hidden" id="editMotId">

                        <label class="small text-muted">Nome Completo</label>
                        <input type="text" class="form-control" id="editMotNome" required>
                        
                        <label class="small text-muted">CPF</label>
                        <input type="text" class="form-control" id="editMotCpf" readonly>
                        
                        <label class="small text-muted">Email</label>
                        <input type="email" class="form-control" id="editMotEmail" required>
                        
                        <label class="small text-muted">Número</label>
                        <input type="text" class="form-control" id="editMotNumero" required>
                    </form>
                </div>
                <div class="modal-footer pb-4">
                    <button type="button" class="btn btn-orange" onclick="atualizarMotorista()">Salvar Alterações</button>
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
            const veiculosMock = [{ id: 1, modelo: "Ford Ka", ano: "2012", placa: "AVW4G37", cor: "Vermelho" }];
            const listDiv = $('#list-veiculos');
            listDiv.empty();

            veiculosMock.forEach(vei => {
                listDiv.append(`
                    <div class="item-card">
                        <div class="item-card-text">
                            <i class="bi bi-car-front-fill fs-5"></i> `+ vei.modelo +` - `+ vei.placa +`
                        </div>
                        <div>
                            <button class="btn btn-icon-orange btn-sm rounded-3 me-1" onclick="abrirViewMotoristas(`+ vei.id +`)">
                                <i class="bi bi-people"></i>
                            </button>
                            <button class="btn btn-icon-orange btn-sm rounded-3" onclick="abrirModalEditVeiculo(`+ vei.id +`, '`+ vei.modelo +`', '`+ vei.ano +`', '`+ vei.placa +`', '`+ vei.cor +`')">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                        </div>
                    </div>
                `);
            });
        }

        function abrirModalAddVeiculo() { $('#form-add-veiculo')[0].reset(); new bootstrap.Modal(document.getElementById('modalAddVeiculo')).show(); }
        function salvarNovoVeiculo() { alert("Veículo adicionado!"); $('#modalAddVeiculo').modal('hide'); carregarVeiculos(); }
        function abrirModalEditVeiculo(id, modelo, ano, placa, cor) {
            $('#editVeiId').val(id); $('#editVeiModelo').val(modelo); $('#editVeiAno').val(ano); $('#editVeiPlaca').val(placa); $('#editVeiCor').val(cor);
            new bootstrap.Modal(document.getElementById('modalEditVeiculo')).show();
        }
        function atualizarVeiculo() { alert("Veículo atualizado!"); $('#modalEditVeiculo').modal('hide'); carregarVeiculos(); }
        function removerVeiculo() { alert("Veículo removido!"); $('#modalEditVeiculo').modal('hide'); carregarVeiculos(); }

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
            const motoristasMock = [
                { id: 1, nome: "Keven Santos", cpf: "123.456.789-00", email: "keven@email.com", numero: "(11) 98765-4321" }
            ];

            const listDiv = $('#list-motoristas');
            listDiv.empty();

            motoristasMock.forEach(mot => {
                listDiv.append(`
                    <div class="item-card">
                        <div class="item-card-text">
                            <i class="bi bi-person-fill fs-4"></i> 
                            `+ mot.nome +`
                        </div>
                        <div>
                            <button class="btn btn-icon-orange btn-sm rounded-3 me-1" onclick="abrirModalEditMotorista(`+ mot.id +`, '`+ mot.nome +`', '`+ mot.cpf +`', '`+ mot.email +`', '`+ mot.numero +`')">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                            <button class="btn btn-icon-red btn-sm rounded-3" onclick="removerMotorista(`+ mot.id +`)">
                                <i class="bi bi-slash-circle"></i>
                            </button>
                        </div>
                    </div>
                `);
            });
        }

        function abrirModalAddMotorista() {
            $('#form-add-motorista')[0].reset();
            var myModal = new bootstrap.Modal(document.getElementById('modalAddMotorista'));
            myModal.show();
        }

        function salvarNovoMotorista() {
            alert("Motorista adicionado ao veículo!");
            $('#modalAddMotorista').modal('hide');
            carregarMotoristas(idVeiculoAtual);
        }

        function abrirModalEditMotorista(id, nome, cpf, email, numero) {
            $('#editMotId').val(id);
            $('#editMotNome').val(nome);
            $('#editMotCpf').val(cpf);
            $('#editMotEmail').val(email);
            $('#editMotNumero').val(numero);
            
            var myModal = new bootstrap.Modal(document.getElementById('modalEditMotorista'));
            myModal.show();
        }

        function atualizarMotorista() {
            alert("Dados do motorista atualizados!");
            $('#modalEditMotorista').modal('hide');
            carregarMotoristas(idVeiculoAtual);
        }

        let idMotoristaParaExcluir = null;

        function removerMotorista(id) {
            idMotoristaParaExcluir = id;
            var myModal = new bootstrap.Modal(document.getElementById('modalConfirmarExclusaoMotorista'));
            myModal.show();
        }

        function efetivarExclusaoMotorista() {
            alert("Motorista " + idMotoristaParaExcluir + " removido com sucesso!");
            
            $('#modalConfirmarExclusaoMotorista').modal('hide');
            
            carregarMotoristas(idVeiculoAtual);
        }
    </script>
</body>
</html>