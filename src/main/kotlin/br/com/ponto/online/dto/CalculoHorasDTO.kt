package br.com.ponto.online.dto

import br.com.ponto.online.entidade.RegistroPonto
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalculoHorasDTO(
    val data: String,
    val horasTrabalhadas: String,
    val horasEsperadas: String = "08:00",
    val saldo: String,
    val intervaloAlmoco: String,
    val registros: List<RegistroPontoRespostaDTO>
) {
    companion object {
        fun formatarDuracao(duracao: Duration): String {
            val horas = duracao.toHours()
            val minutos = duracao.toMinutes() % 60
            return String.format("%02d:%02d", horas, minutos)
        }

        fun formatarSaldo(duracao: Duration): String {
            val sinal = if (duracao.isNegative) "-" else "+"
            val duracaoAbs = duracao.abs()
            val horas = duracaoAbs.toHours()
            val minutos = duracaoAbs.toMinutes() % 60
            return String.format("%s%02d:%02d", sinal, horas, minutos)
        }
    }
}
