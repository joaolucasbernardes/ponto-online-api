package br.com.ponto.online.dto

data class UsuarioLogadoDTO(
        val id: Long,
        val nome: String,
        val email: String,
        val role: String,
        val empresaId: Long?,
        val empresaNome: String?
)
