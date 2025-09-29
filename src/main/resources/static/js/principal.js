document.addEventListener('DOMContentLoaded', () => {
    console.log('Página principal carregada e script principal.js executado.');

    const elementoSaudacao = document.querySelector('#saudacao');
    const elementoDataAtual = document.querySelector('#data-atual');
    const btnBaterPonto = document.querySelector('#btnBaterPonto');
    const spansDeHora = {
        entrada: document.querySelector('#hora-entrada'),
        saidaAlmoco: document.querySelector('#hora-saida-almoco'),
        retornoAlmoco: document.querySelector('#hora-retorno-almoco'),
        saida: document.querySelector('#hora-saida')
    };

    function configurarCabecalho() {
        const nomeUsuario = localStorage.getItem('funcionario_nome');
        if (nomeUsuario) {
            elementoSaudacao.textContent = `Olá, ${nomeUsuario}!`;
        } else {
            elementoSaudacao.textContent = 'Olá!';
            alert('Não foi possível identificar o usuário. Faça o login novamente.');
            window.location.href = '/login.html';
        }

        const hoje = new Date();
        const opcoesDeFormatacao = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        let dataFormatada = hoje.toLocaleDateString('pt-BR', opcoesDeFormatacao);
        elementoDataAtual.textContent = dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    }

    function atualizarQuadroDeHoras(registros = []) {
        spansDeHora.entrada.textContent = '--:--';
        spansDeHora.saidaAlmoco.textContent = '--:--';
        spansDeHora.retornoAlmoco.textContent = '--:--';
        spansDeHora.saida.textContent = '--:--';

        if (registros.length > 0) spansDeHora.entrada.textContent = registros[0].dataHora.split(' ')[1];
        if (registros.length > 1) spansDeHora.saidaAlmoco.textContent = registros[1].dataHora.split(' ')[1];
        if (registros.length > 2) spansDeHora.retornoAlmoco.textContent = registros[2].dataHora.split(' ')[1];
        if (registros.length > 3) spansDeHora.saida.textContent = registros[3].dataHora.split(' ')[1];
    }

    function verificarLimiteEAtualizarBotao(totalDeRegistros) {
        if (totalDeRegistros >= 4) {
            btnBaterPonto.disabled = true;
            btnBaterPonto.textContent = 'Limite Atingido';
            btnBaterPonto.style.backgroundColor = '#6c757d';
            btnBaterPonto.style.cursor = 'not-allowed';
        } else {
            btnBaterPonto.disabled = false;
            btnBaterPonto.textContent = 'Bater Ponto';
            btnBaterPonto.style.backgroundColor = '#536dfe';
            btnBaterPonto.style.cursor = 'pointer';
        }
    }

    async function carregarRegistrosDoDia() {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            alert('Sessão expirada. Por favor, faça o login novamente.');
            window.location.href = '/login.html';
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/registros-ponto/funcionario/${funcionarioId}/hoje`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error('Falha ao carregar registros do dia.');

            const registros = await response.json();
            console.log('Registros de hoje carregados:', registros);
            
            atualizarQuadroDeHoras(registros);
            verificarLimiteEAtualizarBotao(registros.length);

        } catch (error) {
            console.error('Erro ao carregar registros:', error);
            alert(error.message);
        }
    }
    
    btnBaterPonto.addEventListener('click', async () => {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            alert('Sessão expirada. Faça login.');
            window.location.href = '/login.html';
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/registros-ponto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ funcionarioId })
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro ao registrar o ponto.');
            }

            await carregarRegistrosDoDia();

        } catch (error) {
            console.error('Erro no registro de ponto:', error);
            alert(error.message); 
        }
    });

    configurarCabecalho();
    carregarRegistrosDoDia();
});