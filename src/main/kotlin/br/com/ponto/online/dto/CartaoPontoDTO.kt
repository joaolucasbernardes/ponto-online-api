package br.com.ponto.online.dto

import java.time.LocalDate

data class CartaoPontoDTO(
        val empresaNome: String,
        val empresaCnpj: String,
        val empresaEndereco: String,
        val funcionarioNome: String,
        val funcionarioCargo: String,
        val funcionarioCpf: String, // Usando CPF como ID visual, ou matrícula
        val periodoInicio: LocalDate,
        val periodoFim: LocalDate,
        val dataEmissao: LocalDate,
        val itens: List<ItemPontoDTO>,
        val resumoHoras: String = "00:00" // Exemplo de totalizador
)

data class ItemPontoDTO(
        val data: LocalDate,
        val diaSemana: String, // SEG, TER, QUA...
        val horariosEsperados: String, // "08:00 12:00 13:00 17:00"
        val marcacoes: String, // "08:01 12:10 13:05 17:02" ou "Falta", "Atestado"
        val observacao: String = ""
)
