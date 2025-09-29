package br.com.ponto.online.dto

data class LoginRespostaDTO(
    val mensagem: String,
    val token: String,
    val funcionarioId: Long,
    val nome: String
)