document.addEventListener('DOMContentLoaded', () => {
    const containerHistorico = document.querySelector('#lista-historico');

    function carregarHistorico() {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            containerHistorico.innerHTML = '<p>Sessão expirada. <a href="/login.html">Faça o login</a> para ver o histórico.</p>';
            return;
        }

        fetch(`http://localhost:8080/registros-ponto/funcionario/${funcionarioId}`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
        })
        .then(response => {
            if (response.status === 403) throw new Error('Acesso negado. Sua sessão pode ter expirado.');
            if (response.ok) return response.json();
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
                const accordionItem = document.createElement('div');
                accordionItem.className = 'accordion-item';

                const accordionHeader = document.createElement('div');
                accordionHeader.className = 'accordion-header';
                
                const dataTitulo = document.createElement('h3');
                dataTitulo.textContent = formatarDataParaExibicao(data);
                accordionHeader.appendChild(dataTitulo);
                
                const accordionContent = document.createElement('div');
                accordionContent.className = 'accordion-content';

                const accordionBody = document.createElement('div');
                accordionBody.className = 'accordion-body';
                
                registrosPorData[data].forEach(registro => {
                    const registroItem = document.createElement('div');
                    registroItem.className = 'historico-item';
                    const hora = registro.dataHora.split(' ')[1];
                    registroItem.textContent = `Ponto registrado às: ${hora}`;
                    accordionBody.appendChild(registroItem);
                });
                
                accordionContent.appendChild(accordionBody);
                accordionItem.appendChild(accordionHeader);
                accordionItem.appendChild(accordionContent);
                containerHistorico.appendChild(accordionItem);
            }
            
            adicionarListenersAoAcordeao();
        })
        .catch(error => {
            console.error('Erro ao buscar histórico:', error);
            containerHistorico.innerHTML = `<p>Não foi possível carregar o histórico. ${error.message}</p>`;
            if (error.message.includes('Acesso negado')) {
                 setTimeout(() => window.location.href = '/login.html', 2000);
            }
        });
    }

    function adicionarListenersAoAcordeao() {
        const headers = document.querySelectorAll('.accordion-header');
        headers.forEach(header => {
            header.addEventListener('click', () => {
                const item = header.parentElement;
                item.classList.toggle('active');
            });
        });
    }

    function agruparRegistrosPorData(registros) {
        return registros.reduce((acc, registro) => {
            const data = registro.dataHora.split(' ')[0];
            if (!acc[data]) acc[data] = [];
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