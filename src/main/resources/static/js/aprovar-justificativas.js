/**
 * APROVAR JUSTIFICATIVAS - ADMIN
 * Gerenciamento de aprovação/rejeição de justificativas
 */

let todasJustificativas = [];
let justificativaAtual = null;

// ===== INICIALIZAÇÃO =====
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    carregarJustificativas();
    configurarEventos();
});

function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        toast.error('Acesso negado. Apenas administradores podem acessar esta página.');
        setTimeout(() => window.location.href = '/login.html', 2000);
        return;
    }
}

function configurarEventos() {
    // Character counter para motivo de rejeição
    const motivoTextarea = document.getElementById('motivoRejeicao');
    if (motivoTextarea) {
        motivoTextarea.addEventListener('input', (e) => {
            const charCount = e.target.value.length;
            document.getElementById('charCount').textContent = charCount;
        });
    }

    // Fechar modais ao clicar fora
    window.addEventListener('click', (e) => {
        const modalRejeitar = document.getElementById('modalRejeitar');
        const modalHistorico = document.getElementById('modalHistorico');

        if (e.target === modalRejeitar) {
            fecharModalRejeitar();
        }
        if (e.target === modalHistorico) {
            fecharModalHistorico();
        }
    });
}

// ===== CARREGAR JUSTIFICATIVAS =====
async function carregarJustificativas() {
    const token = localStorage.getItem('token');
    const listaContainer = document.getElementById('listaJustificativas');

    listaContainer.innerHTML = '<div class="loading"><div class="loading-spinner"></div><p>Carregando justificativas...</p></div>';

    try {
        // Buscar todas as justificativas (não apenas pendentes)
        const response = await fetch('/api/justificativas', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao carregar justificativas');

        todasJustificativas = await response.json();

        // Aplicar filtro inicial (apenas pendentes)
        aplicarFiltros();
        atualizarEstatisticas();

    } catch (error) {
        console.error('Erro ao carregar justificativas:', error);
        listaContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <p>Erro ao carregar justificativas. ${error.message}</p>
            </div>
        `;
        toast.error('Erro ao carregar justificativas');
    }
}

// ===== FILTROS =====
function aplicarFiltros() {
    const filtroStatus = document.getElementById('filtroStatus').value;
    const filtroTipo = document.getElementById('filtroTipo').value;
    const filtroBusca = document.getElementById('filtroBusca').value.toLowerCase();

    let justificativasFiltradas = todasJustificativas;

    // Filtrar por status
    if (filtroStatus) {
        justificativasFiltradas = justificativasFiltradas.filter(j => j.status === filtroStatus);
    }

    // Filtrar por tipo
    if (filtroTipo) {
        justificativasFiltradas = justificativasFiltradas.filter(j => j.tipo === filtroTipo);
    }

    // Filtrar por nome do funcionário
    if (filtroBusca) {
        justificativasFiltradas = justificativasFiltradas.filter(j =>
            j.funcionarioNome.toLowerCase().includes(filtroBusca)
        );
    }

    renderizarJustificativas(justificativasFiltradas);
}

function limparFiltros() {
    document.getElementById('filtroStatus').value = 'PENDENTE';
    document.getElementById('filtroTipo').value = '';
    document.getElementById('filtroBusca').value = '';
    aplicarFiltros();
}

// ===== RENDERIZAR JUSTIFICATIVAS =====
function renderizarJustificativas(justificativas) {
    const listaContainer = document.getElementById('listaJustificativas');

    if (justificativas.length === 0) {
        listaContainer.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <p>Nenhuma justificativa encontrada com os filtros aplicados.</p>
            </div>
        `;
        return;
    }

    listaContainer.innerHTML = justificativas.map(j => criarCardJustificativa(j)).join('');
}

function criarCardJustificativa(justificativa) {
    const statusClass = justificativa.status.toLowerCase();
    const tipoLabel = formatarTipo(justificativa.tipo);
    const dataFormatada = formatarData(justificativa.data);
    const iniciais = obterIniciais(justificativa.funcionarioNome);

    // Botões de ação apenas para pendentes
    const botoesAcao = justificativa.status === 'PENDENTE' ? `
        <button class="btn-success btn-small" onclick="aprovarJustificativa(${justificativa.id})">
            ✅ Aprovar
        </button>
        <button class="btn-danger btn-small" onclick="abrirModalRejeitar(${justificativa.id})">
            ❌ Rejeitar
        </button>
    ` : '';

    // Mostrar motivo de rejeição se rejeitada
    const motivoRejeicao = justificativa.status === 'REJEITADA' && justificativa.motivoRejeicao ? `
        <div class="justification-reason">
            <div class="reason-label">Motivo da Rejeição:</div>
            <div class="reason-text">${justificativa.motivoRejeicao}</div>
        </div>
    ` : '';

    // Campos adicionais para ajuste de ponto
    const camposAjuste = justificativa.tipo === 'AJUSTE_PONTO' && justificativa.horaOriginal ? `
        <div class="meta-item">
            <span>⏰ Hora Original:</span>
            <strong>${justificativa.horaOriginal}</strong>
        </div>
        <div class="meta-item">
            <span>🔄 Hora Solicitada:</span>
            <strong>${justificativa.horaSolicitada}</strong>
        </div>
    ` : '';

    return `
        <div class="justification-card">
            <div class="card-header">
                <div class="employee-info">
                    <div class="employee-avatar">${iniciais}</div>
                    <div>
                        <div class="employee-name">${justificativa.funcionarioNome}</div>
                        <div class="employee-email">ID: ${justificativa.funcionarioId}</div>
                    </div>
                </div>
                <span class="status-badge ${statusClass}">${justificativa.status}</span>
            </div>
            <div class="card-body">
                <div class="justification-meta">
                    <div class="meta-item">
                        <span>📋 Tipo:</span>
                        <strong>${tipoLabel}</strong>
                    </div>
                    <div class="meta-item">
                        <span>📅 Data:</span>
                        <strong>${dataFormatada}</strong>
                    </div>
                    ${camposAjuste}
                </div>
                <div class="justification-reason">
                    <div class="reason-label">Motivo:</div>
                    <div class="reason-text">${justificativa.motivo}</div>
                </div>
                ${motivoRejeicao}
            </div>
            <div class="card-footer">
                ${botoesAcao}
                <button class="btn-secondary btn-small" onclick="abrirModalHistorico(${justificativa.id})">
                    📜 Ver Histórico
                </button>
            </div>
        </div>
    `;
}

// ===== APROVAR JUSTIFICATIVA =====
async function aprovarJustificativa(id) {
    if (!confirm('Tem certeza que deseja aprovar esta justificativa?')) {
        return;
    }

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`/api/justificativas/${id}/processar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                aprovadorId: parseInt(localStorage.getItem('userId')),
                aprovada: true,
                observacao: null
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao aprovar justificativa');
        }

        toast.success('Justificativa aprovada com sucesso!');
        await carregarJustificativas();

    } catch (error) {
        console.error('Erro ao aprovar:', error);
        toast.error(error.message);
    }
}

// ===== REJEITAR JUSTIFICATIVA =====
function abrirModalRejeitar(id) {
    justificativaAtual = id;
    document.getElementById('motivoRejeicao').value = '';
    document.getElementById('charCount').textContent = '0';
    document.getElementById('modalRejeitar').classList.add('active');
}

function fecharModalRejeitar() {
    document.getElementById('modalRejeitar').classList.remove('active');
    justificativaAtual = null;
}

async function confirmarRejeicao() {
    const motivo = document.getElementById('motivoRejeicao').value.trim();

    if (!motivo) {
        toast.error('Informe o motivo da rejeição');
        return;
    }

    if (motivo.length < 10) {
        toast.error('O motivo deve ter pelo menos 10 caracteres');
        return;
    }

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`/api/justificativas/${justificativaAtual}/processar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                aprovadorId: parseInt(localStorage.getItem('userId')),
                aprovada: false,
                observacao: motivo
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensagem || 'Erro ao rejeitar justificativa');
        }

        toast.success('Justificativa rejeitada com sucesso!');
        fecharModalRejeitar();
        await carregarJustificativas();

    } catch (error) {
        console.error('Erro ao rejeitar:', error);
        toast.error(error.message);
    }
}

// ===== HISTÓRICO DE ALTERAÇÕES =====
async function abrirModalHistorico(id) {
    const modal = document.getElementById('modalHistorico');
    const conteudo = document.getElementById('historicoConteudo');

    modal.classList.add('active');
    conteudo.innerHTML = '<div class="loading"><div class="loading-spinner"></div><p>Carregando histórico...</p></div>';

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`/api/historico-alteracoes/justificativa/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao carregar histórico');

        const historico = await response.json();

        if (historico.length === 0) {
            conteudo.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <p>Nenhuma alteração registrada ainda.</p>
                </div>
            `;
            return;
        }

        conteudo.innerHTML = historico.map(h => `
            <div class="historico-item">
                <div class="historico-header">
                    <div class="historico-tipo">${formatarTipoAlteracao(h.tipoAlteracao)}</div>
                    <div class="historico-data">${formatarDataHora(h.dataHora)}</div>
                </div>
                <div class="historico-descricao">${h.descricao}</div>
                <div class="historico-usuario">Por: ${h.usuarioNome || 'Sistema'}</div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Erro ao carregar histórico:', error);
        conteudo.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <p>Erro ao carregar histórico.</p>
            </div>
        `;
    }
}

function fecharModalHistorico() {
    document.getElementById('modalHistorico').classList.remove('active');
}

// ===== ESTATÍSTICAS =====
function atualizarEstatisticas() {
    const hoje = new Date().toISOString().split('T')[0];

    const pendentes = todasJustificativas.filter(j => j.status === 'PENDENTE').length;
    const aprovadasHoje = todasJustificativas.filter(j =>
        j.status === 'APROVADA' && j.dataProcessamento && j.dataProcessamento.startsWith(hoje)
    ).length;
    const rejeitadasHoje = todasJustificativas.filter(j =>
        j.status === 'REJEITADA' && j.dataProcessamento && j.dataProcessamento.startsWith(hoje)
    ).length;
    const total = todasJustificativas.length;

    document.getElementById('statPendentes').textContent = pendentes;
    document.getElementById('statAprovadas').textContent = aprovadasHoje;
    document.getElementById('statRejeitadas').textContent = rejeitadasHoje;
    document.getElementById('statTotal').textContent = total;
}

// ===== FUNÇÕES AUXILIARES =====
function formatarTipo(tipo) {
    const tipos = {
        'AUSENCIA': '🏠 Ausência',
        'ATRASO': '⏰ Atraso',
        'AJUSTE_PONTO': '⚙️ Ajuste de Ponto'
    };
    return tipos[tipo] || tipo;
}

function formatarTipoAlteracao(tipo) {
    const tipos = {
        'CRIACAO': '➕ Criação',
        'APROVACAO': '✅ Aprovação',
        'REJEICAO': '❌ Rejeição',
        'AJUSTE_PONTO': '⚙️ Ajuste de Ponto'
    };
    return tipos[tipo] || tipo;
}

function formatarData(dataString) {
    if (!dataString) return '-';
    // Se já estiver no formato DD/MM/YYYY, retorna como está
    if (dataString.includes('/')) return dataString;
    // Caso contrário, converte de YYYY-MM-DD para DD/MM/YYYY
    const [ano, mes, dia] = dataString.split('-');
    return `${dia}/${mes}/${ano}`;
}

function formatarDataHora(dataHoraString) {
    const data = new Date(dataHoraString);
    return data.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function obterIniciais(nome) {
    const partes = nome.trim().split(' ');
    if (partes.length >= 2) {
        return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
    }
    return nome.substring(0, 2).toUpperCase();
}

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}
