package br.com.ponto.online.servico

import br.com.ponto.online.dto.CalculoHorasDTO
import br.com.ponto.online.dto.RegistroPontoRespostaDTO
import br.com.ponto.online.dto.ResumoMensalDTO
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class CalculoHorasServico(
    private val registroPontoRepositorio: RegistroPontoRepositorio
) {

    companion object {
        const val JORNADA_DIARIA_HORAS = 8L
        const val INTERVALO_MINIMO_ALMOCO_MINUTOS = 30L
        const val INTERVALO_MAXIMO_ALMOCO_MINUTOS = 120L
    }

    fun calcularHorasDia(funcionarioId: Long, data: LocalDate): CalculoHorasDTO {
        val registros = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetween(
            funcionarioId,
            data.atStartOfDay(),
            data.plusDays(1).atStartOfDay()
        ).sortedBy { it.dataHora }

        val registrosDTO = registros.map { RegistroPontoRespostaDTO.deEntidade(it) }

        val horasTrabalhadas = calcularHorasTrabalhadasDia(registros)
        val intervaloAlmoco = calcularIntervaloAlmoco(registros)
        val horasEsperadas = Duration.ofHours(JORNADA_DIARIA_HORAS)
        val saldo = horasTrabalhadas.minus(horasEsperadas)

        return CalculoHorasDTO(
            data = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            horasTrabalhadas = CalculoHorasDTO.formatarDuracao(horasTrabalhadas),
            horasEsperadas = CalculoHorasDTO.formatarDuracao(horasEsperadas),
            saldo = CalculoHorasDTO.formatarSaldo(saldo),
            intervaloAlmoco = CalculoHorasDTO.formatarDuracao(intervaloAlmoco),
            registros = registrosDTO
        )
    }

    fun calcularResumoMensal(funcionarioId: Long, mes: Int, ano: Int): ResumoMensalDTO {
        val inicioDomes = LocalDate.of(ano, mes, 1)
        val fimDoMes = inicioDomes.plusMonths(1).minusDays(1)

        val registros = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetween(
            funcionarioId,
            inicioDomes.atStartOfDay(),
            fimDoMes.plusDays(1).atStartOfDay()
        )

        // Agrupar por dia
        val registrosPorDia = registros.groupBy { it.dataHora.toLocalDate() }

        var totalHorasTrabalhadas = Duration.ZERO
        registrosPorDia.forEach { (_, registrosDia) ->
            totalHorasTrabalhadas = totalHorasTrabalhadas.plus(calcularHorasTrabalhadasDia(registrosDia))
        }

        val diasUteis = contarDiasUteis(inicioDomes, fimDoMes)
        val diasTrabalhados = registrosPorDia.size
        val diasFaltantes = diasUteis - diasTrabalhados

        val horasEsperadas = Duration.ofHours(diasUteis * JORNADA_DIARIA_HORAS)
        val bancoDeHoras = totalHorasTrabalhadas.minus(horasEsperadas)

        return ResumoMensalDTO(
            mes = mes,
            ano = ano,
            totalHorasTrabalhadas = CalculoHorasDTO.formatarDuracao(totalHorasTrabalhadas),
            totalHorasEsperadas = CalculoHorasDTO.formatarDuracao(horasEsperadas),
            bancoDeHoras = CalculoHorasDTO.formatarSaldo(bancoDeHoras),
            diasTrabalhados = diasTrabalhados,
            diasFaltantes = diasFaltantes.toInt()
        )
    }

    private fun calcularHorasTrabalhadasDia(registros: List<br.com.ponto.online.entidade.RegistroPonto>): Duration {
        if (registros.isEmpty()) return Duration.ZERO

        return when (registros.size) {
            1 -> Duration.ZERO // Apenas entrada
            2 -> Duration.between(registros[0].dataHora, registros[1].dataHora) // Entrada e saída
            3 -> {
                // Entrada, saída almoço, retorno almoço (falta saída final)
                Duration.between(registros[0].dataHora, registros[1].dataHora)
            }
            else -> {
                // Jornada completa: entrada, saída almoço, retorno almoço, saída
                val manha = Duration.between(registros[0].dataHora, registros[1].dataHora)
                val tarde = Duration.between(registros[2].dataHora, registros[3].dataHora)
                manha.plus(tarde)
            }
        }
    }

    private fun calcularIntervaloAlmoco(registros: List<br.com.ponto.online.entidade.RegistroPonto>): Duration {
        if (registros.size < 3) return Duration.ZERO
        return Duration.between(registros[1].dataHora, registros[2].dataHora)
    }

    private fun contarDiasUteis(inicio: LocalDate, fim: LocalDate): Long {
        var count = 0L
        var data = inicio
        while (!data.isAfter(fim)) {
            val diaSemana = data.dayOfWeek
            if (diaSemana != java.time.DayOfWeek.SATURDAY && diaSemana != java.time.DayOfWeek.SUNDAY) {
                count++
            }
            data = data.plusDays(1)
        }
        return count
    }
}
