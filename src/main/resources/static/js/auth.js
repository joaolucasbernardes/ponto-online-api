/**
 * Auth Utilities - Shared authentication functions
 */

// Logout - limpa dados e redireciona para login
function logout() {
    localStorage.clear();
    // Remove também cookies se houver
    document.cookie = 'jwt_token=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    window.location.href = '/login.html';
}

// Verifica se usuário está autenticado
function verificarAutenticacao() {
    const token = localStorage.getItem('token') || localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// Obtém token de autenticação
function getToken() {
    return localStorage.getItem('token') || localStorage.getItem('jwt_token');
}

// Obtém ID do usuário logado
function getUserId() {
    return localStorage.getItem('userId') || localStorage.getItem('funcionario_id');
}

// Obtém nome do usuário logado
function getUserName() {
    return localStorage.getItem('userName') || localStorage.getItem('funcionario_nome');
}

// Obtém role do usuário
function getUserRole() {
    return localStorage.getItem('role');
}
