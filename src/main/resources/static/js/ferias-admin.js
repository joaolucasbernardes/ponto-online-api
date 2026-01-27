let solicitacaoAtualId = null;

document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarSolicitacoes();
});

async function carregarSolicitacoes() {
    const container = document.getElementById('listaSolicitacoes');
    container.innerHTML = '<div class="loading-spinner"></div>';

    const status = document.getElementById('filtroStatus').value;

    try {
        const token = getToken();
        let url = '/api/ferias';
        if (status) url += `?status=${status}`;

        const response = await fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao carregar');

        const lista = await response.json();

        if (lista.length === 0) {
            container.innerHTML = '<div class="empty-state">Nenhuma solicitação encontrada.</div>';
            return;
        }

        container.innerHTML = lista.map(item => `
            <div class="solicitacao-card" style="border-left-color: ${getCorStatus(item.status)}">
                <div class="solicitacao-info">
                    <h4>${item.funcionarioNome}</h4>
                    <p>${formatarTipo(item.tipo)}</p>
                </div>
                <div class="solicitacao-data">
                    <strong>${formatarData(item.dataInicio)}</strong> a <strong>${formatarData(item.dataFim)}</strong><br>
                    <small>${item.dias} dias</small>
                </div>
                <div class="solicitacao-status">
                    <span class="badge badge-${item.status.toLowerCase()}">${item.status}</span>
                </div>
                <div class="actions">
                    <button onclick="abrirModal(${item.id})" class="btn-secondary">Detalhes</button>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error(error);
        container.innerHTML = '<p class="text-danger">Erro ao carregar dados.</p>';
    }
}

// Armazenar dados completos na memória seria ideal, mas vou buscar via API ou passar parametros. 
// Para simplificar, vou buscar novamente via fetch no array global se tivesse, mas aqui vou usar o que tenho no DOM? Não.
// Vou fazer um truque: ao clicar, busco do servidor ou passo o objeto. 
// Vou buscar do servidor novamente para garantir dados frescos ou filtrar do array global.
// Melhor: Vou modificar carregarSolicitacoes para guardar em variavel global.

let listaGlobal = [];
// Reescrevendo carregarSolicitacoes para usar variavel global
// ... (mas vou deixar como está e buscar do array filtrado se eu salvar)

// Vou reescrever a função carregar para salvar no global
const carregarOriginal = carregarSolicitacoes;
carregarSolicitacoes = async function () {
    const container = document.getElementById('listaSolicitacoes');
    container.innerHTML = '<div class="loading-spinner"></div>';
    const status = document.getElementById('filtroStatus').value;
    try {
        const token = getToken();
        let url = '/api/ferias';
        if (status) url += `?status=${status}`;
        const response = await fetch(url, { headers: { 'Authorization': `Bearer ${token}` } });
        if (!response.ok) throw new Error('Erro');
        listaGlobal = await response.json();

        if (listaGlobal.length === 0) {
            container.innerHTML = '<div class="empty-state">Nenhuma solicitação encontrada.</div>';
            return;
        }

        container.innerHTML = listaGlobal.map(item => `
            <div class="solicitacao-card" style="border-left-color: ${getCorStatus(item.status)}">
                <div class="solicitacao-info">
                    <h4>${item.funcionarioNome}</h4>
                    <p>${formatarTipo(item.tipo)}</p>
                </div>
                <div class="solicitacao-data">
                    <strong>${formatarData(item.dataInicio)}</strong> a <strong>${formatarData(item.dataFim)}</strong><br>
                    <small>${item.dias} dias</small>
                </div>
                <div class="solicitacao-status">
                    <span class="badge badge-${item.status.toLowerCase()}">${item.status}</span>
                </div>
                <div class="actions">
                    <button onclick="abrirModal(${item.id})" class="btn-secondary">Detalhes</button>
                </div>
            </div>
        `).join('');
    } catch (e) { console.error(e); }
}

function abrirModal(id) {
    const item = listaGlobal.find(i => i.id === id);
    if (!item) return;

    solicitacaoAtualId = id;

    const detalhes = document.getElementById('detalhesConteudo');
    detalhes.innerHTML = `
        <div class="detail-row"><span>Funcionário:</span> <strong>${item.funcionarioNome}</strong></div>
        <div class="detail-row"><span>Tipo:</span> <strong>${formatarTipo(item.tipo)}</strong></div>
        <div class="detail-row"><span>Período:</span> <strong>${formatarData(item.dataInicio)} a ${formatarData(item.dataFim)}</strong> (${item.dias} dias)</div>
        <div class="detail-row"><span>Data Solicitação:</span> <strong>${formatarData(item.dataSolicitacao)}</strong></div>
        <div class="detail-row" style="flex-direction:column; align-items:flex-start;">
            <span>Observação Funcionário:</span>
            <p style="background:rgba(255,255,255,0.05); padding:10px; width:100%; border-radius:4px; margin-top:5px;">${item.observacao || 'Nenhuma'}</p>
        </div>
        ${item.status !== 'SOLICITADA' ? `
            <div class="detail-row"><span>Status:</span> <strong>${item.status}</strong></div>
            <div class="detail-row"><span>Aprovador:</span> <strong>${item.aprovadorNome || '-'}</strong></div>
            <div class="detail-row" style="flex-direction:column; align-items:flex-start;">
                <span>Observação Gestor:</span>
                <p>${item.observacaoGestor || '-'}</p>
            </div>
        ` : ''}
    `;

    const areaAcao = document.getElementById('areaAcao');
    if (item.status === 'SOLICITADA') {
        areaAcao.style.display = 'block';
        document.getElementById('obsGestor').value = '';
    } else {
        areaAcao.style.display = 'none';
    }

    document.getElementById('modalDetalhes').classList.add('active');
}

function fecharModal() {
    document.getElementById('modalDetalhes').classList.remove('active');
    solicitacaoAtualId = null;
}

async function processar(aprovada) {
    if (!solicitacaoAtualId) return;

    if (!confirm(`Tem certeza que deseja ${aprovada ? 'APROVAR' : 'REJEITAR'} esta solicitação?`)) return;

    const obs = document.getElementById('obsGestor').value;
    const token = getToken();
    const adminId = parseInt(localStorage.getItem('userId')) || 0; // Se logado como admin com ID
    // Hack: Se admin, o ID pode ser pego do token payload se decodificado, mas vamos assumir que localStorage tem userId.
    // Se não tiver, o backend vai tentar achar pelo repositório admin.

    try {
        const response = await fetch(`/api/ferias/${solicitacaoAtualId}/analisar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                aprovadorId: adminId,
                aprovada: aprovada,
                observacao: obs
            })
        });

        if (!response.ok) {
            const erro = await response.json();
            throw new Error(erro.mensagem || 'Erro ao processar');
        }

        toast.success(`Solicitação ${aprovada ? 'aprovada' : 'rejeitada'}!`);
        fecharModal();
        carregarSolicitacoes();

    } catch (error) {
        toast.error(error.message);
    }
}

function getCorStatus(status) {
    const map = {
        'SOLICITADA': '#f59e0b',
        'APROVADA': '#10b981',
        'REJEITADA': '#ef4444',
        'CANCELADA': '#6b7280',
        'CONCLUIDA': '#6366f1'
    };
    return map[status] || '#ccc';
}

function formatarTipo(tipo) {
    const map = {
        'FERIAS': 'Férias',
        'LICENCA_MEDICA': 'Licença Médica',
        'LICENCA_MATERNIDADE': 'Licença Maternidade',
        'LICENCA_PATERNIDADE': 'Licença Paternidade',
        'FOLGA_COMPENSATORIA': 'Folga',
        'CASAMENTO': 'Casamento',
        'LUTO': 'Luto',
        'OUTROS': 'Outros'
    };
    return map[tipo] || tipo;
}

function formatarData(dataStr) {
    if (!dataStr) return '-';
    const datePart = dataStr.split('T')[0];
    const [ano, mes, dia] = datePart.split('-');
    return `${dia}/${mes}/${ano}`;
}
