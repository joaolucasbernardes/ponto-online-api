package br.com.ponto.online.dto

data class FuncionarioEdicaoDTO(
    val nome: String,
    val email: String,
    val senha: String?, // Opcional - se vazio, mantém senha atual
    val role: String,
    val empresaId: Long
)
