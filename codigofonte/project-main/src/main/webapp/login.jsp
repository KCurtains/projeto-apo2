<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EasyParking</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="CSS/global.css">
    <link rel="stylesheet" href="CSS/login.css">

</head>
<body>

    <div class="app-container">
        
        <div id="view-login">
            <div class="logo-area">
                <img src="imagens/logo.jpeg" alt="EasyParking Logo" style="max-width: 100%; border-radius:10px;">
            </div>
            
            <div class="auth-card text-center">
                <h6 class="mb-4 font-weight-bold">Bem vindo ao EasyParking</h6>
                
                <form id="form-login">
                    <input type="email" class="form-control" id="loginEmail" placeholder="email" required>
                    <input type="password" class="form-control" id="loginSenha" placeholder="senha" required>
                    
                    <button type="button" class="btn btn-orange mt-2" onclick="realizarLogin()">Entrar no EasyParking</button>
                </form>
                
                <div class="mt-3 text-start">
                    <div class="link-text">Primeira vez? <span onclick="changeView('view-cadastro-1')">Faça cadastro</span></div>
                    <div class="link-text">Esqueceu sua senha? <span onclick="changeView('view-recuperar-1')">Redefina sua senha</span></div>
                </div>
            </div>
        </div>

        <div id="view-cadastro-1" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-login')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Realize seu cadastro</h6>
                </div>
                
                <p class="text-muted small mb-3">Informações Pessoais:</p>
                
                <form id="form-cad-1">
                    <input type="text" class="form-control" id="cadNome" placeholder="nome completo">
                    <input type="text" class="form-control" id="cadCpf" placeholder="cpf">
                    <input type="email" class="form-control" id="cadEmail" placeholder="email">
                    <input type="text" class="form-control" id="cadNumero" placeholder="número">
                </form>

                <div class="dots-container">
                    <span class="dot active"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="enviarCodigoCadastro()">Continuar</button>
            </div>
        </div>

        <div id="view-cadastro-codigo" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-cadastro-1')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Realize seu cadastro</h6>
                </div>
                
                <p class="text-muted small text-center mb-4">Enviamos um código de verificação para o email informado. Insira ele para continuar seu cadastro.</p>
                
                <input type="text" class="form-control text-center" id="cadCodigo" placeholder="código de verificação">
                <small class="text-danger d-block text-center mb-3" style="font-size: 0.75rem; visibility: hidden;">Código Inválido</small>

                <div class="dots-container">
                    <span class="dot"></span>
                    <span class="dot active"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="verificarCodigoCadastro()">Continuar</button>
            </div>
        </div>

        <div id="view-cadastro-2" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-cadastro-codigo')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Realize seu cadastro</h6>
                </div>
                
                <p class="text-muted small mb-3">Informações do Veículo:</p>
                
                <form id="form-cad-2">
                    <input type="text" class="form-control" id="cadModelo" placeholder="modelo">
                    <input type="text" class="form-control" id="cadAno" placeholder="ano">
                    <input type="text" class="form-control" id="cadCor" placeholder="cor">
                    <input type="text" class="form-control" id="cadPlaca" placeholder="placa">
                </form>

                <div class="dots-container">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot active"></span>
                    <span class="dot"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="changeView('view-cadastro-3')">Continuar</button>
            </div>
        </div>

        <div id="view-cadastro-3" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-cadastro-2')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Realize seu cadastro</h6>
                </div>
                
                <p class="text-muted small mb-3">Crie sua senha:</p>
                
                <form id="form-cad-3">
                    <input type="password" class="form-control" id="cadSenha" placeholder="Digite sua senha">
                    <input type="password" class="form-control" id="cadConfirmaSenha" placeholder="Confirme sua senha">
                </form>

                <div class="dots-container">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot active"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="finalizarCadastro()">Finalizar cadastro</button>
            </div>
        </div>

        <div id="view-recuperar-1" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-login')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Redefinir senha</h6>
                </div>
                
                <p class="text-muted small mb-3">Digite seu email:</p>
                
                <form id="form-rec-1">
                    <input type="email" class="form-control" id="recEmail" placeholder="email">
                </form>

                <div class="dots-container">
                    <span class="dot active"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="enviarCodigoRecuperacao()">Continuar</button>
            </div>
        </div>

        <div id="view-recuperar-2" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-recuperar-1')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Redefinir senha</h6>
                </div>
                
                <p class="text-muted small text-center mb-4">Enviamos um código de verificação no seu email.</p>
                
                <form id="form-rec-2">
                    <input type="text" class="form-control text-center" id="recCodigo" placeholder="código">
                </form>

                <div class="dots-container">
                    <span class="dot"></span>
                    <span class="dot active"></span>
                    <span class="dot"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="verificarCodigoRecuperacao()">Continuar</button>
            </div>
        </div>

        <div id="view-recuperar-3" class="d-none">
            <div class="auth-card">
                <div class="d-flex align-items-center mb-4">
                    <i class="back-btn me-2 fw-bold" onclick="changeView('view-recuperar-2')">&larr;</i>
                    <h6 class="mb-0 fw-bold">Redefinir senha</h6>
                </div>
                
                <form id="form-rec-3">
                    <input type="password" class="form-control" id="recNovaSenha" placeholder="nova senha">
                    <input type="password" class="form-control" id="recRepetirSenha" placeholder="Repetir nova senha">
                </form>

                <div class="dots-container">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot active"></span>
                </div>

                <button type="button" class="btn btn-orange" onclick="salvarNovaSenha()">Redefinir Senha</button>
            </div>
        </div>

    </div>

    <div class="footer-text w-100">
        Desenvolvido por Alunos do IFSP - 2026
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <script>

        function changeView(viewId) {
            $('#view-login, #view-cadastro-1, #view-cadastro-codigo, #view-cadastro-2, #view-cadastro-3, #view-recuperar-1, #view-recuperar-2, #view-recuperar-3').addClass('d-none');
            $('#' + viewId).removeClass('d-none');
        }

        function enviarCodigoCadastro() {
            const nome = $('#cadNome').val();
            const cpf = $('#cadCpf').val();
            const email = $('#cadEmail').val();
            if (!nome || !cpf || !email) {
                alert("Preencha nome, CPF e e-mail para continuar.");
                return;
            }
            $.ajax({
                url: 'verificacao-cadastro',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ acao: 'enviar', email: email }),
                dataType: 'json',
                success: function(response) {
                    changeView('view-cadastro-codigo');
                },
                error: function(err) {
                    const msg = (err.responseJSON && err.responseJSON.mensagem) ? err.responseJSON.mensagem : "Erro ao enviar o código.";
                    alert(msg);
                }
            });
        }

        function verificarCodigoCadastro() {
            const codigo = $('#cadCodigo').val();
            if (!codigo) {
                alert("Digite o código recebido por e-mail.");
                return;
            }
            $.ajax({
                url: 'verificacao-cadastro',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ acao: 'verificar', codigo: codigo }),
                dataType: 'json',
                success: function(response) {
                    changeView('view-cadastro-2');
                },
                error: function(err) {
                    alert("Código inválido. Confira o e-mail e tente novamente.");
                }
            });
        }

        function realizarLogin() {
            const dadosLogin = {
                email: $('#loginEmail').val(),
                senha: $('#loginSenha').val()
            };

            if (!dadosLogin.email || !dadosLogin.senha) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            $.ajax({
                url: 'login',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(dadosLogin), 
                dataType: 'json',
                success: function(response) {
                    if (response.sucesso) {
                        window.location.href = response.redirecionar;
                    } else {
                        alert(response.mensagem);
                    }
                },
                error: function(err) {

                    if (err.responseJSON && err.responseJSON.mensagem) {
                        alert(err.responseJSON.mensagem);
                    } else {
                        alert("Erro ao tentar realizar o login. Verifique os dados.");
                    }
                }
            });
        }

        function finalizarCadastro() {

            const novoUsuario = {
                nome: $('#cadNome').val(),
                cpf: $('#cadCpf').val(),
                email: $('#cadEmail').val(),
                telefone: $('#cadNumero').val(),
                senha: $('#cadSenha').val(),

                sexo: "MASCULINO", 
                dataNascimento: "2000-01-01",
                mensalista: false   
            };

            if (!novoUsuario.nome || !novoUsuario.cpf || !novoUsuario.email || !novoUsuario.senha) {
                alert("Por favor, preencha os campos obrigatórios do cadastro.");
                return;
            }

            if (novoUsuario.senha !== $('#cadConfirmaSenha').val()) {
                alert("As senhas informadas não coincidem!");
                return;
            }

            $.ajax({
                url: 'cliente?acao=cadastrar', 
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(novoUsuario), 
                dataType: 'json',
                success: function(response) {
                    alert(response.mensagem);
                    changeView('view-login'); 
                },
                error: function(err) {
                    if (err.responseJSON && err.responseJSON.mensagem) {
                        alert(err.responseJSON.mensagem);
                    } else {
                        alert("Erro ao realizar o cadastro no servidor.");
                    }
                }
            });
        }

        function enviarCodigoRecuperacao() {
            const email = $('#recEmail').val();
            
            if(!email) {
                alert("Por favor, digite seu email.");
                return;
            }

            $.ajax({
                url: 'RecuperarSenhaServlet',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ acao: 'enviar_codigo', email: email }),
                dataType: 'json',
                success: function(response) {
                    changeView('view-recuperar-2');
                },
                error: function(err) {
                    const msg = (err.responseJSON && err.responseJSON.mensagem) ? err.responseJSON.mensagem : "Erro ao enviar o código.";
                    alert(msg);
                }
            });
        }

        function verificarCodigoRecuperacao() {
            const codigo = $('#recCodigo').val();
            
            if(!codigo) {
                alert("Por favor, digite o código.");
                return;
            }

            $.ajax({
                url: 'RecuperarSenhaServlet',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ acao: 'verificar_codigo', codigo: codigo }),
                dataType: 'json',
                success: function(response) {
                    changeView('view-recuperar-3');
                },
                error: function(err) {
                    alert("Código inválido. Tente novamente.");
                }
            });
        }

        function salvarNovaSenha() {
            const senha1 = $('#recNovaSenha').val();
            const senha2 = $('#recRepetirSenha').val();
            
            if(!senha1 || senha1 !== senha2) {
                alert("As senhas não coincidem ou estão vazias!");
                return;
            }

            $.ajax({
                url: 'RecuperarSenhaServlet',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ acao: 'nova_senha', senha: senha1 }),
                dataType: 'json',
                success: function(response) {
                    alert("Senha redefinida com sucesso!");
                    changeView('view-login');
                },
                error: function(err) {
                    const msg = (err.responseJSON && err.responseJSON.mensagem) ? err.responseJSON.mensagem : "Erro ao redefinir a senha.";
                    alert(msg);
                }
            });
        }
    </script>
</body>
</html>