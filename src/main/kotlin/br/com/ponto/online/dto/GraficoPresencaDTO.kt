package br.com.ponto.online.dto

data class GraficoPresencaDTO(
    val labels: List<String>, // Datas
    val presentes: List<Int>,
    val ausentes: List<Int>
)
