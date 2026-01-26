package br.com.ponto.online.dto

data class FuncionarioCadastroDTO(
    val nome: String,
    val cpf: String,
    val email: String,
    val senha: String,
    val role: String, // "ADMIN" ou "FUNCIONARIO"
    val empresaId: Long,
    val escalaId: Long? = null
)
