// ===== Gerenciar Locais Permitidos =====

let map;
let marker;
let circle;
let locais = [];
let editandoId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacao();
    inicializarMapa();
    carregarLocais();
    inicializarEventos();
});

function verificarAutenticacao() {
    const token = localStorage.getItem('jwt_token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        window.location.href = '/login.html';
        return;
    }
}

// ===== Mapa =====
function inicializarMapa() {
    // Inicializar mapa centrado em São Paulo
    map = L.map('map').setView([-23.550520, -46.633308], 13);

    // Adicionar camada de tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
    }).addTo(map);

    // Click no mapa para selecionar coordenadas
    map.on('click', function (e) {
        const lat = e.latlng.lat;
        const lon = e.latlng.lng;

        document.getElementById('latitude').value = lat.toFixed(6);
        document.getElementById('longitude').value = lon.toFixed(6);

        atualizarMarcadorMapa(lat, lon);
    });
}

function atualizarMarcadorMapa(lat, lon) {
    const raio = parseInt(document.getElementById('raioMetros').value) || 100;

    // Remover marcador e círculo anteriores
    if (marker) {
        map.removeLayer(marker);
    }
    if (circle) {
        map.removeLayer(circle);
    }

    // Adicionar novo marcador
    marker = L.marker([lat, lon]).addTo(map);

    // Adicionar círculo do raio
    circle = L.circle([lat, lon], {
        radius: raio,
        color: '#6366f1',
        fillColor: '#6366f1',
        fillOpacity: 0.2
    }).addTo(map);

    // Centralizar mapa
    map.setView([lat, lon], 15);
}

// ===== Eventos =====
function inicializarEventos() {
    document.getElementById('btnNovoLocal').addEventListener('click', novoLocal);
    document.getElementById('btnCancelar').addEventListener('click', cancelarEdicao);
    document.getElementById('formLocal').addEventListener('submit', salvarLocal);
    document.getElementById('btnSair').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/login.html';
    });

    // Atualizar círculo quando raio mudar
    document.getElementById('raioMetros').addEventListener('input', (e) => {
        const lat = parseFloat(document.getElementById('latitude').value);
        const lon = parseFloat(document.getElementById('longitude').value);

        if (lat && lon) {
            atualizarMarcadorMapa(lat, lon);
        }
    });
}

// ===== CRUD Locais =====
async function carregarLocais() {
    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch('/api/locais-permitidos', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar locais');
        }

        locais = await response.json();
        renderizarListaLocais();

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao carregar locais permitidos');
        document.getElementById('listaLocais').innerHTML =
            '<p style="text-align: center; color: var(--danger); padding: 2rem;">Erro ao carregar locais</p>';
    }
}

function renderizarListaLocais() {
    const container = document.getElementById('listaLocais');

    if (locais.length === 0) {
        container.innerHTML = `
            <p style="text-align: center; color: var(--text-muted); padding: 2rem;">
                Nenhum local cadastrado. Clique em "+ Novo Local" para adicionar.
            </p>
        `;
        return;
    }

    container.innerHTML = locais.map(local => `
        <div class="local-card">
            <div style="display: flex; justify-content: space-between; align-items: start;">
                <h4>${local.nome}</h4>
                <span class="status-badge ${local.ativo ? 'ativo' : 'inativo'}">
                    ${local.ativo ? 'Ativo' : 'Inativo'}
                </span>
            </div>
            <p class="local-info">📍 ${local.endereco}</p>
            <p class="local-info">🌐 ${local.latitude.toFixed(6)}, ${local.longitude.toFixed(6)}</p>
            <p class="local-info">📏 Raio: ${local.raioMetros}m</p>
            ${local.descricao ? `<p class="local-info" style="margin-top: 0.5rem;">${local.descricao}</p>` : ''}
            <div class="local-actions">
                <button class="btn-ponto btn-small" onclick="editarLocal(${local.id})">Editar</button>
                <button class="btn-secondary btn-small" onclick="visualizarNoMapa(${local.id})">Ver no Mapa</button>
                <button class="btn-secondary btn-small" onclick="alterarStatus(${local.id}, ${!local.ativo})">
                    ${local.ativo ? 'Desativar' : 'Ativar'}
                </button>
                <button class="btn-secondary btn-small" style="background: var(--danger); border-color: var(--danger);" 
                        onclick="excluirLocal(${local.id}, '${local.nome}')">Excluir</button>
            </div>
        </div>
    `).join('');
}

function novoLocal() {
    editandoId = null;
    document.getElementById('formTitulo').textContent = 'Novo Local';
    document.getElementById('formLocal').reset();
    document.getElementById('localId').value = '';
    document.getElementById('raioMetros').value = '100';
    document.getElementById('ativo').checked = true;

    // Limpar mapa
    if (marker) map.removeLayer(marker);
    if (circle) map.removeLayer(circle);
    map.setView([-23.550520, -46.633308], 13);
}

async function editarLocal(id) {
    const local = locais.find(l => l.id === id);
    if (!local) return;

    editandoId = id;
    document.getElementById('formTitulo').textContent = 'Editar Local';
    document.getElementById('localId').value = id;
    document.getElementById('nome').value = local.nome;
    document.getElementById('endereco').value = local.endereco;
    document.getElementById('latitude').value = local.latitude;
    document.getElementById('longitude').value = local.longitude;
    document.getElementById('raioMetros').value = local.raioMetros;
    document.getElementById('descricao').value = local.descricao || '';
    document.getElementById('ativo').checked = local.ativo;

    // Atualizar mapa
    atualizarMarcadorMapa(local.latitude, local.longitude);

    // Scroll para o formulário
    document.querySelector('.local-form').scrollIntoView({ behavior: 'smooth' });
}

function visualizarNoMapa(id) {
    const local = locais.find(l => l.id === id);
    if (!local) return;

    atualizarMarcadorMapa(local.latitude, local.longitude);

    // Scroll para o mapa
    document.getElementById('map').scrollIntoView({ behavior: 'smooth' });
}

async function salvarLocal(e) {
    e.preventDefault();

    const token = localStorage.getItem('jwt_token');

    const dados = {
        nome: document.getElementById('nome').value.trim(),
        endereco: document.getElementById('endereco').value.trim(),
        latitude: parseFloat(document.getElementById('latitude').value),
        longitude: parseFloat(document.getElementById('longitude').value),
        raioMetros: parseInt(document.getElementById('raioMetros').value),
        descricao: document.getElementById('descricao').value.trim() || null,
        ativo: document.getElementById('ativo').checked
    };

    // Validações
    if (!dados.nome || !dados.endereco) {
        toast.error('Preencha todos os campos obrigatórios');
        return;
    }

    if (isNaN(dados.latitude) || isNaN(dados.longitude)) {
        toast.error('Coordenadas inválidas');
        return;
    }

    if (dados.raioMetros < 10 || dados.raioMetros > 10000) {
        toast.error('Raio deve estar entre 10m e 10km');
        return;
    }

    try {
        const url = editandoId
            ? `/api/locais-permitidos/${editandoId}`
            : '/api/locais-permitidos';

        const method = editandoId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dados)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro ao salvar local');
        }

        toast.success(editandoId ? 'Local atualizado com sucesso!' : 'Local cadastrado com sucesso!');

        await carregarLocais();
        novoLocal();

    } catch (error) {
        console.error('Erro:', error);
        toast.error(error.message || 'Erro ao salvar local');
    }
}

function cancelarEdicao() {
    novoLocal();
}

async function alterarStatus(id, novoStatus) {
    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch(`/api/locais-permitidos/${id}/status?ativo=${novoStatus}`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao alterar status');
        }

        toast.success(`Local ${novoStatus ? 'ativado' : 'desativado'} com sucesso!`);
        await carregarLocais();

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao alterar status do local');
    }
}

async function excluirLocal(id, nome) {
    if (!confirm(`Deseja realmente excluir o local "${nome}"?\n\nEsta ação não pode ser desfeita.`)) {
        return;
    }

    const token = localStorage.getItem('jwt_token');

    try {
        const response = await fetch(`/api/locais-permitidos/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao excluir local');
        }

        toast.success('Local excluído com sucesso!');
        await carregarLocais();

        // Se estava editando este local, limpar formulário
        if (editandoId === id) {
            novoLocal();
        }

    } catch (error) {
        console.error('Erro:', error);
        toast.error('Erro ao excluir local');
    }
}
