package br.com.ponto.online.dto

data class InconsistenciaDTO(
    val funcionarioId: Long,
    val funcionarioNome: String,
    val data: String,
    val tipo: String, // REGISTRO_INCOMPLETO, JORNADA_EXCESSIVA, SEM_INTERVALO
    val descricao: String
)
