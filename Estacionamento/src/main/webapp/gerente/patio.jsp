<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking - Gestão de Pátios</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="../CSS/global.css">
    <link rel="stylesheet" href="../CSS/gerente.css">
</head>
<body>

    <div class="app-container">
        
        <div class="header-title container-md">
            Pátios
        </div>

        <div class="content-area container-md">
            <div id="empty-state" class="text-center text-muted mt-5 d-none">
                <p>Nenhum pátio cadastrado.</p>
            </div>
            <div id="list-state"></div>
        </div>

        <div class="fab" onclick="abrirModalAddPatio()">+</div>

        <div class="bottom-nav">
            <a href="relatorio.jsp" class="nav-item"><i class="bi bi-list-task"></i></a>
            <a href="patio.jsp" class="nav-item active"><i class="bi bi-exclamation-circle"></i></a>
            <a href="reclamacao_gerente.jsp" class="nav-item"><i class="bi bi-car-front"></i></a>
            <a href="perfil_gerente.jsp" class="nav-item"><i class="bi bi-person-circle"></i></a>
        </div>

    </div>

    <div class="modal fade" id="modalAddPatio" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Adicionar Pátio</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body pb-0">
                    <form id="form-add-patio">
                        <label class="small text-muted mb-1">Endereço / Nome do Pátio</label>
                        <input type="text" class="form-control" id="addPatioNome" required>
                        
                        <label class="small text-muted mb-1">Vagas de Carro</label>
                        <input type="number" class="form-control" id="addPatioVagasCarro" required>
                        
                        <label class="small text-muted mb-1">Vagas de Moto</label>
                        <input type="number" class="form-control" id="addPatioVagasMoto" required>
                        
                        <label class="small text-muted mb-1">Vagas de Caminhão</label>
                        <input type="number" class="form-control" id="addPatioVagasCaminhao" required>
                    </form>
                </div>
                <div class="modal-footer pb-4 pt-2">
                    <button type="button" class="btn btn-orange" onclick="salvarNovoPatio()">Adicionar</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalEditPatio" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Editar Pátio</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body pb-0">
                    <form id="form-edit-patio">
                        <input type="hidden" id="editPatioId">
                        
                        <label class="small text-muted mb-1">Endereço / Nome do Pátio</label>
                        <input type="text" class="form-control" id="editPatioNome" readonly>
                        
                        <label class="small text-muted mb-1">Vagas de Carro</label>
                        <input type="number" class="form-control" id="editPatioVagasCarro" required>
                        
                        <label class="small text-muted mb-1">Vagas de Moto</label>
                        <input type="number" class="form-control" id="editPatioVagasMoto" required>
                        
                        <label class="small text-muted mb-1">Vagas de Caminhão</label>
                        <input type="number" class="form-control" id="editPatioVagasCaminhao" required>
                    </form>
                </div>
                <div class="modal-footer flex-column pb-4 gap-2 pt-2">
                    <button type="button" class="btn btn-orange" onclick="atualizarPatio()">Salvar Alterações</button>
                    <button type="button" class="btn btn-red" onclick="removerPatio()">Remover Pátio</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalInfoPatio" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Informações do Pátio</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body pb-4">
                    <div class="mb-2"><small class="text-muted d-block">Nome / Endereço</small><strong id="infoNome"></strong></div>
                    <div class="row">
                        <div class="col-4 mb-2"><small class="text-muted d-block">Carros</small><strong id="infoCarros"></strong></div>
                        <div class="col-4 mb-2"><small class="text-muted d-block">Motos</small><strong id="infoMotos"></strong></div>
                        <div class="col-4 mb-2"><small class="text-muted d-block">Caminhões</small><strong id="infoCaminhoes"></strong></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        $(document).ready(function() {
            carregarPatios();
        });

        function carregarPatios() {
            const listDiv = $('#list-state');
            
            $.ajax({
                url: '../patio?acao=listar',
                type: 'GET',
                dataType: 'json',
                success: function(patios) {
                    listDiv.empty();
                    
                    if(patios.length === 0) {
                        $('#empty-state').removeClass('d-none');
                        $('#list-state').addClass('d-none');
                    } else {
                        $('#empty-state').addClass('d-none');
                        $('#list-state').removeClass('d-none');

                        patios.forEach(patio => {
                            const patioData = encodeURIComponent(JSON.stringify(patio));

                            const cardHtml = `
                                <div class="item-card">
                                    <div class="item-card-text"><strong>`+ patio.nome +`</strong></div>
                                    <div>
                                        <button class="btn btn-icon-orange btn-sm rounded-3 me-1" onclick="abrirModalInfo('`+ patioData +`')">
                                            <i class="bi bi-info-circle"></i>
                                        </button>
                                        <button class="btn btn-icon-orange btn-sm rounded-3" onclick="abrirModalEdit('`+ patioData +`')">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>
                                    </div>
                                </div>
                            `;
                            listDiv.append(cardHtml);
                        });
                    }
                },
                error: function() {
                    alert("Erro: Não foi possível carregar os pátios do servidor.");
                }
            });
        }
        
        function abrirModalAddPatio() {
            $('#form-add-patio')[0].reset();
            new bootstrap.Modal(document.getElementById('modalAddPatio')).show();
        }

        // DISPARA AJAX DE ADIÇÃO (POST)
		function salvarNovoPatio() {
		    const dadosPatio = {
		        endereco: $('#addPatioNome').val(), // <-- Faltava esta linha
		        capacidadeCarro: $('#addPatioVagasCarro').val(),
		        capacidadeMoto: $('#addPatioVagasMoto').val(),
		        capacidadeCaminhao: $('#addPatioVagasCaminhao').val()
		    };

            $.ajax({
                url: '../patio?acao=adicionar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosPatio),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    $('#modalAddPatio').modal('hide');
                    carregarPatios();
                },
                error: function() {
                    alert("Erro ao adicionar patio");
                    $('#modalAddPatio').modal('hide');
                }
            });
        }

        function abrirModalEdit(patioDataEncoded) {
            const patio = JSON.parse(decodeURIComponent(patioDataEncoded));

            $('#editPatioId').val(patio.id);
            $('#editPatioNome').val(patio.nome);
            $('#editPatioVagasCarro').val(patio.vagasCarro);
            $('#editPatioVagasMoto').val(patio.vagasMoto);
            $('#editPatioVagasCaminhao').val(patio.vagasCaminhao);

            new bootstrap.Modal(document.getElementById('modalEditPatio')).show();
        }

        // DISPARA AJAX DE ATUALIZAÇÃO (POST)
        function atualizarPatio() {
	    	const dadosPatio = {
	        id: $('#editPatioId').val(),
	        endereco: $('#editPatioNome').val(), // <-- Faltava esta linha
	        capacidadeCarro: $('#editPatioVagasCarro').val(),
	        capacidadeMoto: $('#editPatioVagasMoto').val(),
	        capacidadeCaminhao: $('#editPatioVagasCaminhao').val()
    	};

            $.ajax({
                url: '../patio?acao=atualizar',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosPatio),
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    $('#modalEditPatio').modal('hide');
                    carregarPatios();
                },
                error: function() {
                    alert("Simulação: POST disparado para atualizar pátio.");
                    $('#modalEditPatio').modal('hide');
                }
            });
        }

        // DISPARA AJAX DE REMOÇÃO (POST)
        function removerPatio() {
            const idPatio = $('#editPatioId').val();
            if(confirm("ATENÇÃO: Deseja realmente remover este pátio?")) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/patio?acao=remover',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ id: idPatio }),
                    dataType: 'json',
                    success: function(response) {
                        alert(response.mensagem);
                        $('#modalEditPatio').modal('hide');
                        carregarPatios();
                    },
                    error: function() {
                        alert("Simulação: Pátio ID " + idPatio + " removido.");
                        $('#modalEditPatio').modal('hide');
                    }
                });
            }
        }

        function abrirModalInfo(patioDataEncoded) {
            const patio = JSON.parse(decodeURIComponent(patioDataEncoded));

            $('#infoNome').text(patio.nome);
            $('#infoCarros').text(patio.vagasCarro);
            $('#infoMotos').text(patio.vagasMoto);
            $('#infoCaminhoes').text(patio.vagasCaminhao);

            new bootstrap.Modal(document.getElementById('modalInfoPatio')).show();
        }
    </script>
</body>
</html>