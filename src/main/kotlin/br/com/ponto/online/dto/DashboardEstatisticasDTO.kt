package br.com.ponto.online.dto

data class DashboardEstatisticasDTO(
    val totalFuncionarios: Int,
    val funcionariosAtivos: Int,
    val registrosHoje: Int,
    val inconsistenciasHoje: Int,
    val horasTrabalhadasHoje: String
)
