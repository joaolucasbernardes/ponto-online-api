package br.com.ponto.online.dto

data class ResumoMensalDTO(
    val mes: Int,
    val ano: Int,
    val totalHorasTrabalhadas: String,
    val totalHorasEsperadas: String,
    val bancoDeHoras: String,
    val diasTrabalhados: Int,
    val diasFaltantes: Int
)
