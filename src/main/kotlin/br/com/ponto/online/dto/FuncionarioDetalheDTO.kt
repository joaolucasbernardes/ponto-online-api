package br.com.ponto.online.dto

data class FuncionarioDetalheDTO(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val role: String,
    val empresaNome: String,
    val empresaId: Long,
    val ativo: Boolean,
    val escalaId: Long?,
    val escalaNome: String?
)
