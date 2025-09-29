package br.com.ponto.online.dto

data class LoginRespostaDTO(
    val mensagem: String,
    val token: String // NOVO CAMPO
)