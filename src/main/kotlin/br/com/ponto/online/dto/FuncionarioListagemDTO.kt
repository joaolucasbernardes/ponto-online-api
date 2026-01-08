package br.com.ponto.online.dto

data class FuncionarioListagemDTO(
    val id: Long,
    val nome: String,
    val email: String,
    val empresa: String,
    val ultimoRegistro: String?,
    val statusHoje: String // COMPLETO, INCOMPLETO, SEM_REGISTRO
)
