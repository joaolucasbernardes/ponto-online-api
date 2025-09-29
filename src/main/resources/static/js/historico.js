document.addEventListener('DOMContentLoaded', () => {
    const containerHistorico = document.querySelector('#lista-historico');

    const funcionarioId = 1;

    // Função para buscar e renderizar o histórico
    function carregarHistorico() {
        fetch(`http://localhost:8080/registros-ponto/funcionario/${funcionarioId}`)
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Falha ao carregar o histórico.');
            })
            .then(registros => {
                containerHistorico.innerHTML = ''; // Limpa a mensagem "Carregando..."

                if (registros.length === 0) {
                    containerHistorico.innerHTML = '<p>Nenhum registro de ponto encontrado.</p>';
                    return;
                }

                // Agrupa os registros pela data
                const registrosPorData = agruparRegistrosPorData(registros);

                // Cria os elementos HTML para cada dia
                for (const data in registrosPorData) {
                    const diaCard = document.createElement('div');
                    diaCard.className = 'dia-historico';

                    const dataTitulo = document.createElement('h3');
                    dataTitulo.textContent = formatarDataParaExibicao(data);
                    diaCard.appendChild(dataTitulo);

                    registrosPorData[data].forEach(registro => {
                        const registroItem = document.createElement('div');
                        registroItem.className = 'historico-item';
                        const hora = registro.dataHora.split(' ')[1]; // Pega apenas a hora
                        registroItem.textContent = `Ponto registrado às: ${hora}`;
                        diaCard.appendChild(registroItem);
                    });

                    containerHistorico.appendChild(diaCard);
                }
            })
            .catch(error => {
                console.error('Erro ao buscar histórico:', error);
                containerHistorico.innerHTML = '<p>Não foi possível carregar o histórico. Tente novamente mais tarde.</p>';
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

    // Função auxiliar para formatar a data do título do card
    function formatarDataParaExibicao(dataString) {
        const [dia, mes, ano] = dataString.split('/');
        const dataObj = new Date(ano, mes - 1, dia);
        const opcoes = { weekday: 'long', day: 'numeric', month: 'long' };
        let dataFormatada = dataObj.toLocaleDateString('pt-BR', opcoes);
        return dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    }

    carregarHistorico();
});