// ===== DASHBOARD ADMIN - JAVASCRIPT =====

let graficoPresenca = null;
let funcionariosData = [];
let inconsistenciasData = [];

// Verificar autenticação
function verificarAutenticacao() {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    const userName = localStorage.getItem('userName');

    if (!token || role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
}

// Logout
function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Carregar estatísticas
async function carregarEstatisticas() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/admin/dashboard/estatisticas', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar estatísticas');

        const data = await response.json();

        document.getElementById('totalFuncionarios').textContent = data.totalFuncionarios;
        document.getElementById('registrosHoje').textContent = data.registrosHoje;
        document.getElementById('horasHoje').textContent = data.horasTrabalhadasHoje;
        document.getElementById('inconsistencias').textContent = data.inconsistenciasHoje;

    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    }
}

// Carregar gráfico de presença
async function carregarGraficoPresenca() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/admin/dashboard/grafico-presenca?dias=7', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar gráfico');

        const data = await response.json();

        renderizarGrafico(data);

    } catch (error) {
        console.error('Erro ao carregar gráfico:', error);
    }
}

// Renderizar gráfico com Chart.js
function renderizarGrafico(data) {
    const ctx = document.getElementById('graficoPresenca').getContext('2d');

    if (graficoPresenca) {
        graficoPresenca.destroy();
    }

    graficoPresenca = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: [
                {
                    label: 'Presentes',
                    data: data.presentes,
                    backgroundColor: 'rgba(72, 187, 120, 0.8)',
                    borderColor: 'rgba(72, 187, 120, 1)',
                    borderWidth: 2,
                    borderRadius: 8
                },
                {
                    label: 'Ausentes',
                    data: data.ausentes,
                    backgroundColor: 'rgba(245, 101, 101, 0.8)',
                    borderColor: 'rgba(245, 101, 101, 1)',
                    borderWidth: 2,
                    borderRadius: 8
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: {
                        color: 'rgba(255, 255, 255, 0.9)',
                        font: {
                            size: 14,
                            weight: '500'
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    ticks: {
                        color: 'rgba(255, 255, 255, 0.8)'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    ticks: {
                        color: 'rgba(255, 255, 255, 0.8)',
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// Carregar funcionários
async function carregarFuncionarios() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/admin/dashboard/funcionarios-ativos', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar funcionários');

        funcionariosData = await response.json();
        renderizarFuncionarios(funcionariosData);

    } catch (error) {
        console.error('Erro ao carregar funcionários:', error);
        document.getElementById('funcionariosTableBody').innerHTML = `
            <tr><td colspan="5" class="empty-state">Erro ao carregar funcionários</td></tr>
        `;
    }
}

// Renderizar tabela de funcionários
function renderizarFuncionarios(funcionarios) {
    const tbody = document.getElementById('funcionariosTableBody');

    if (funcionarios.length === 0) {
        tbody.innerHTML = `
            <tr><td colspan="5" class="empty-state">Nenhum funcionário encontrado</td></tr>
        `;
        return;
    }

    tbody.innerHTML = funcionarios.map(func => `
        <tr>
            <td>${func.nome}</td>
            <td>${func.email}</td>
            <td>${func.empresa}</td>
            <td>${func.ultimoRegistro || 'Nunca'}</td>
            <td>
                <span class="status-badge ${func.statusHoje.toLowerCase().replace('_', '-')}">
                    ${formatarStatus(func.statusHoje)}
                </span>
            </td>
        </tr>
    `).join('');
}

// Formatar status
function formatarStatus(status) {
    const statusMap = {
        'COMPLETO': 'Completo',
        'INCOMPLETO': 'Incompleto',
        'SEM_REGISTRO': 'Sem Registro'
    };
    return statusMap[status] || status;
}

// Buscar funcionários
function buscarFuncionarios(termo) {
    const filtrados = funcionariosData.filter(func =>
        func.nome.toLowerCase().includes(termo.toLowerCase()) ||
        func.email.toLowerCase().includes(termo.toLowerCase()) ||
        func.empresa.toLowerCase().includes(termo.toLowerCase())
    );
    renderizarFuncionarios(filtrados);
}

// Carregar inconsistências
async function carregarInconsistencias() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('/api/admin/dashboard/inconsistencias', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('Erro ao carregar inconsistências');

        inconsistenciasData = await response.json();
        renderizarInconsistencias(inconsistenciasData);

    } catch (error) {
        console.error('Erro ao carregar inconsistências:', error);
        document.getElementById('inconsistenciasList').innerHTML = `
            <div class="empty-state">Erro ao carregar inconsistências</div>
        `;
    }
}

// Renderizar inconsistências
function renderizarInconsistencias(inconsistencias) {
    const container = document.getElementById('inconsistenciasList');

    if (inconsistencias.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">✅</div>
                <p>Nenhuma inconsistência detectada!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = inconsistencias.map(inc => `
        <div class="inconsistencia-card ${inc.tipo.toLowerCase().replace('_', '-')}">
            <div class="inconsistencia-header">
                <span class="inconsistencia-nome">${inc.funcionarioNome}</span>
                <span class="inconsistencia-data">${inc.data}</span>
            </div>
            <div class="inconsistencia-tipo">${formatarTipo(inc.tipo)}</div>
            <p class="inconsistencia-descricao">${inc.descricao}</p>
        </div>
    `).join('');
}

// Formatar tipo de inconsistência
function formatarTipo(tipo) {
    const tipoMap = {
        'REGISTRO_INCOMPLETO': 'Registro Incompleto',
        'JORNADA_EXCESSIVA': 'Jornada Excessiva',
        'SEM_INTERVALO': 'Sem Intervalo'
    };
    return tipoMap[tipo] || tipo;
}

// Filtrar inconsistências
function filtrarInconsistencias(tipo) {
    if (tipo === 'TODOS') {
        renderizarInconsistencias(inconsistenciasData);
    } else {
        const filtradas = inconsistenciasData.filter(inc => inc.tipo === tipo);
        renderizarInconsistencias(filtradas);
    }
}

// Atualizar dashboard
function atualizarDashboard() {
    carregarEstatisticas();
    carregarGraficoPresenca();
    carregarFuncionarios();
    carregarInconsistencias();
}

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    atualizarDashboard();

    // Busca de funcionários
    document.getElementById('searchFuncionarios').addEventListener('input', (e) => {
        buscarFuncionarios(e.target.value);
    });

    // Filtro de inconsistências
    document.getElementById('filtroInconsistencias').addEventListener('change', (e) => {
        filtrarInconsistencias(e.target.value);
    });

    // Atualização automática a cada 30 segundos
    setInterval(atualizarDashboard, 30000);
});
