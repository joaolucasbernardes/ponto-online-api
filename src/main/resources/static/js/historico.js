document.addEventListener('DOMContentLoaded', () => {
    const containerHistorico = document.querySelector('#lista-historico');
    const totalDiasEl = document.querySelector('#totalDias');
    const totalRegistrosEl = document.querySelector('#totalRegistros');
    const diasMesEl = document.querySelector('#diasMes');
    const filtroMes = document.querySelector('#filtroMes');
    const filtroBusca = document.querySelector('#filtroBusca');

    let todosRegistros = [];
    let registrosFiltrados = [];

    // Ícones para cada tipo de registro
    const icones = {
        0: '🏁', // Entrada
        1: '🍽️', // Saída Almoço
        2: '☕', // Retorno Almoço
        3: '🏠'  // Saída
    };

    const tipos = {
        0: 'Entrada',
        1: 'Saída Almoço',
        2: 'Retorno Almoço',
        3: 'Saída'
    };

    const classes = {
        0: 'entrada',
        1: 'saida-almoco',
        2: 'retorno-almoco',
        3: 'saida'
    };

    function carregarHistorico() {
        const token = localStorage.getItem('jwt_token');
        const funcionarioId = localStorage.getItem('funcionario_id');

        if (!token || !funcionarioId) {
            mostrarErro('Sessão expirada. <a href="/login.html" style="color: var(--accent-primary)">Faça o login</a> para ver o histórico.');
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
                todosRegistros = registros;
                registrosFiltrados = registros;

                if (registros.length === 0) {
                    mostrarVazio();
                    return;
                }

                atualizarEstatisticas(registros);
                renderizarHistorico(registros);
            })
            .catch(error => {
                console.error('Erro ao buscar histórico:', error);
                mostrarErro(`Não foi possível carregar o histórico. ${error.message}`);
                if (error.message.includes('Acesso negado')) {
                    setTimeout(() => window.location.href = '/login.html', 2000);
                }
            });
    }

    function atualizarEstatisticas(registros) {
        const registrosPorData = agruparRegistrosPorData(registros);
        const totalDias = Object.keys(registrosPorData).length;
        const totalRegistros = registros.length;

        // Contar dias do mês atual
        const mesAtual = new Date().getMonth();
        const anoAtual = new Date().getFullYear();
        const diasMes = Object.keys(registrosPorData).filter(data => {
            const [dia, mes, ano] = data.split('/');
            return parseInt(mes) - 1 === mesAtual && parseInt(ano) === anoAtual;
        }).length;

        totalDiasEl.textContent = totalDias;
        totalRegistrosEl.textContent = totalRegistros;
        diasMesEl.textContent = diasMes;
    }

    function renderizarHistorico(registros) {
        const registrosPorData = agruparRegistrosPorData(registros);
        const datas = Object.keys(registrosPorData).sort((a, b) => {
            const [diaA, mesA, anoA] = a.split('/');
            const [diaB, mesB, anoB] = b.split('/');
            const dataA = new Date(anoA, mesA - 1, diaA);
            const dataB = new Date(anoB, mesB - 1, diaB);
            return dataB - dataA; // Mais recente primeiro
        });

        containerHistorico.innerHTML = '';

        if (datas.length === 0) {
            mostrarVazio();
            return;
        }

        datas.forEach(data => {
            const registrosDia = registrosPorData[data];
            const card = criarCardDia(data, registrosDia);
            containerHistorico.appendChild(card);
        });
    }

    function criarCardDia(data, registros) {
        const card = document.createElement('div');
        card.className = 'dia-card';

        // Header do card
        const header = document.createElement('div');
        header.className = 'dia-header';

        const info = document.createElement('div');
        info.className = 'dia-info';

        // Ícone do dia
        const icone = document.createElement('div');
        icone.className = 'dia-icone';
        icone.textContent = '📅';

        // Texto do dia
        const texto = document.createElement('div');
        texto.className = 'dia-texto';

        const dataFormatada = document.createElement('div');
        dataFormatada.className = 'dia-data';
        dataFormatada.textContent = formatarDataParaExibicao(data);

        const resumo = document.createElement('div');
        resumo.className = 'dia-resumo';

        const badge = document.createElement('span');
        badge.className = 'resumo-badge';
        badge.textContent = `${registros.length} registro${registros.length > 1 ? 's' : ''}`;
        resumo.appendChild(badge);

        // Calcular horas trabalhadas
        if (registros.length >= 2) {
            const horasBadge = document.createElement('span');
            horasBadge.className = 'resumo-badge';
            horasBadge.textContent = calcularHorasDia(registros);
            resumo.appendChild(horasBadge);
        }

        texto.appendChild(dataFormatada);
        texto.appendChild(resumo);

        info.appendChild(icone);
        info.appendChild(texto);

        // Ícone de expandir
        const expandIcon = document.createElement('div');
        expandIcon.className = 'expand-icon';
        expandIcon.textContent = '▼';

        header.appendChild(info);
        header.appendChild(expandIcon);

        // Detalhes (timeline)
        const detalhes = document.createElement('div');
        detalhes.className = 'dia-detalhes';

        const timeline = document.createElement('div');
        timeline.className = 'timeline';

        registros.forEach((registro, index) => {
            const timelineItem = criarTimelineItem(registro, index);
            timeline.appendChild(timelineItem);
        });

        detalhes.appendChild(timeline);

        // Evento de clique para expandir/recolher
        header.addEventListener('click', () => {
            card.classList.toggle('expanded');
        });

        card.appendChild(header);
        card.appendChild(detalhes);

        return card;
    }

    function criarTimelineItem(registro, index) {
        const item = document.createElement('div');
        item.className = 'timeline-item';

        const marker = document.createElement('div');
        marker.className = `timeline-marker ${classes[index]}`;
        marker.textContent = icones[index];

        const content = document.createElement('div');
        content.className = 'timeline-content';

        const tipo = document.createElement('div');
        tipo.className = 'timeline-tipo';
        tipo.textContent = tipos[index];

        const hora = document.createElement('div');
        hora.className = 'timeline-hora';
        hora.textContent = registro.dataHora.split(' ')[1];

        content.appendChild(tipo);
        content.appendChild(hora);

        item.appendChild(marker);
        item.appendChild(content);

        return item;
    }

    function calcularHorasDia(registros) {
        if (registros.length < 2) return '';

        const horas = registros.map(r => {
            const [h, m, s] = r.dataHora.split(' ')[1].split(':');
            return parseInt(h) * 60 + parseInt(m);
        });

        let totalMinutos = 0;
        if (registros.length === 2) {
            totalMinutos = horas[1] - horas[0];
        } else if (registros.length === 4) {
            const manha = horas[1] - horas[0];
            const tarde = horas[3] - horas[2];
            totalMinutos = manha + tarde;
        }

        const h = Math.floor(totalMinutos / 60);
        const m = totalMinutos % 60;
        return `${h}h${m > 0 ? m + 'min' : ''}`;
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
        const opcoes = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
        let dataFormatada = dataObj.toLocaleDateString('pt-BR', opcoes);
        return dataFormatada.charAt(0).toUpperCase() + dataFormatada.slice(1);
    }

    function mostrarVazio() {
        containerHistorico.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <p>Nenhum registro de ponto encontrado.</p>
            </div>
        `;
    }

    function mostrarErro(mensagem) {
        containerHistorico.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <p>${mensagem}</p>
            </div>
        `;
    }

    // Filtros
    filtroMes.addEventListener('change', (e) => {
        const mesAno = e.target.value;
        if (!mesAno) {
            registrosFiltrados = todosRegistros;
        } else {
            const [ano, mes] = mesAno.split('-');
            registrosFiltrados = todosRegistros.filter(r => {
                const [dia, mesReg, anoReg] = r.dataHora.split(' ')[0].split('/');
                return anoReg === ano && mesReg === mes;
            });
        }
        renderizarHistorico(registrosFiltrados);
        atualizarEstatisticas(registrosFiltrados);
    });

    filtroBusca.addEventListener('input', (e) => {
        const busca = e.target.value.toLowerCase();
        if (!busca) {
            registrosFiltrados = todosRegistros;
        } else {
            registrosFiltrados = todosRegistros.filter(r => {
                const dataFormatada = formatarDataParaExibicao(r.dataHora.split(' ')[0]).toLowerCase();
                return dataFormatada.includes(busca) || r.dataHora.includes(busca);
            });
        }
        renderizarHistorico(registrosFiltrados);
        atualizarEstatisticas(registrosFiltrados);
    });

    // Inicializar
    carregarHistorico();
});