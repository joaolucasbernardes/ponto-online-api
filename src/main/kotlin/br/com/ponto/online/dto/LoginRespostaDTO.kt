package br.com.ponto.online.dto

data class LoginRespostaDTO(
        val mensagem: String,
        val token: String,
        val refreshToken: String? = null,
        val funcionarioId: Long?,
        val nome: String,
        val role: String
)
