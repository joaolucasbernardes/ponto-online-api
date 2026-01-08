document.addEventListener('DOMContentLoaded', () => {

    // Seleciona os elementos com os quais vamos interagir
    const loginForm = document.querySelector('.login-form');
    const identificadorInput = document.querySelector('#identificador');
    const senhaInput = document.querySelector('#senha');

    loginForm.addEventListener('submit', (evento) => {
        evento.preventDefault(); // Evita que o form carregue ao clicar em submit.

        // Captura os valores digitados nos campos de input
        const identificador = identificadorInput.value;
        const senha = senhaInput.value;

        const dadosLogin = {
            identificador: identificador,
            senha: senha
        };

        fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosLogin)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                // Limpa qualquer token antigo se o login falhar
                localStorage.removeItem('jwt_token');
                throw new Error('Usuário ou senha inválidos.');
            })
            .then(data => {
                console.log('Login bem-sucedido:', data);

                // Salva o token no localStorage do navegador
                localStorage.setItem('jwt_token', data.token);
                localStorage.setItem('funcionario_id', data.funcionarioId);
                localStorage.setItem('funcionario_nome', data.nome);

                // Salva também as chaves esperadas pelo admin.js
                localStorage.setItem('token', data.token);
                localStorage.setItem('role', data.role);
                localStorage.setItem('userName', data.nome);

                alert(data.mensagem);

                // Redireciona o usuário para a página principal
                window.location.href = '/principal.html';
            })
            .catch(error => {
                console.error('Erro na autenticação:', error);
                alert(error.message);
            });
    });
});