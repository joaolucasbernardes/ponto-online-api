document.addEventListener('DOMContentLoaded', () => {
    const containerHistorico = document.querySelector('#lista-historico');
    const funcionarioId = 1;

    function carregarHistorico() {
        // Pega o token salvo no localStorage
        const token = localStorage.getItem('jwt_token');

        if (!token) {
            containerHistorico.innerHTML = '<p>Sessão expirada. <a href="/login.html">Faça o login</a> para ver o histórico.</p>';
            return;
        }

        fetch(`http://localhost:8080/registros-ponto/funcionario/${funcionarioId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.status === 403) {
                    throw new Error('Acesso negado. Sua sessão pode ter expirado.');
                }
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Falha ao carregar o histórico.');
            })
            .then(registros => {
                containerHistorico.innerHTML = ''; 

                if (registros.length === 0) {
                    containerHistorico.innerHTML = '<p>Nenhum registro de ponto encontrado.</p>';
                    return;
                }

                const registrosPorData = agruparRegistrosPorData(registros);

                for (const data in registrosPorData) {
                    const diaCard = document.createElement('div');
                    diaCard.className = 'dia-historico';

                    const dataTitulo = document.createElement('h3');
                    dataTitulo.textContent = formatarDataParaExibicao(data);
                    diaCard.appendChild(dataTitulo);

                    registrosPorData[data].forEach(registro => {
                        const registroItem = document.createElement('div');
                        registroItem.className = 'historico-item';
                        const hora = registro.dataHora.split(' ')[1];
                        registroItem.textContent = `Ponto registrado às: ${hora}`;
                        diaCard.appendChild(registroItem);
                    });

                    containerHistorico.appendChild(diaCard);
                }
            })
            .catch(error => {
                console.error('Erro ao buscar histórico:', error);
                containerHistorico.innerHTML = `<p>Não foi possível carregar o histórico. ${error.message}</p>`;
                if (error.message.includes('Acesso negado')) {
                     setTimeout(() => window.location.href = '/login.html', 2000);
                }
            });
    }

    function agruparRegistrosPorData(registros) {
        return registros.reduce((acc, registro) => {
            const data = registro.dataHora.split(' ')[0];
            if (!acc[data]) {
                acc[data] = [];
            }
            acc[data].push(registro);
            return acc;
        }, {});
    }
    
    function formatarDataParaExibicao(dataString) {
        const [dia, mes, ano] = dataString.split('/');
        const dataObj = new Date(ano, mes - 1, dia);
        const opcoes = { weekday: 'long', day: 'numeric', month: 'long' };
        let dataFormatada = dataObj.toLocaleDateString('pt-BR', opcoes);
        return dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    }

    carregarHistorico();
});