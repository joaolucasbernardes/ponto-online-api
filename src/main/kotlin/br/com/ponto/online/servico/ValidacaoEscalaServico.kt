package br.com.ponto.online.servico

import br.com.ponto.online.entidade.Escala
import br.com.ponto.online.enumeracao.DiaSemana
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class ValidacaoEscalaServico {
    
    private val objectMapper = jacksonObjectMapper()
    
    /**
     * Valida se o registro está dentro da escala do funcionário
     */
    fun validarHorarioPorEscala(
        dataHora: LocalDateTime,
        escala: Escala?,
        isEntrada: Boolean
    ): ResultadoValidacao {
        // Se não tem escala, não valida
        if (escala == null) {
            return ResultadoValidacao(true, null)
        }
        
        // Validar dia da semana
        val diaSemana = converterDiaSemana(dataHora.dayOfWeek)
        val diasPermitidos = parseDiasSemana(escala.diasSemana)
        
        if (diaSemana !in diasPermitidos) {
            return ResultadoValidacao(
                false,
                "Registro fora da escala: ${diaSemana.name} não é dia de trabalho nesta escala"
            )
        }
        
        // Validar horário
        val hora = dataHora.toLocalTime()
        val tolerancia = if (isEntrada) {
            escala.toleranciaEntradaMinutos
        } else {
            escala.toleranciaSaidaMinutos
        }
        
        val horarioEsperado = if (isEntrada) escala.horaEntrada else escala.horaSaida
        val diferencaMinutos = Duration.between(horarioEsperado, hora).toMinutes().toInt()
        
        // Verificar se está dentro da tolerância
        if (Math.abs(diferencaMinutos) > tolerancia) {
            val tipo = if (isEntrada) "entrada" else "saída"
            val mensagem = if (diferencaMinutos > 0) {
                "Registro de $tipo com atraso de ${Math.abs(diferencaMinutos)} minutos (tolerância: $tolerancia min)"
            } else {
                "Registro de $tipo com antecedência de ${Math.abs(diferencaMinutos)} minutos (tolerância: $tolerancia min)"
            }
            return ResultadoValidacao(false, mensagem)
        }
        
        return ResultadoValidacao(true, null)
    }
    
    /**
     * Calcula horas trabalhadas considerando a escala
     */
    fun calcularHorasPorEscala(
        entrada: LocalDateTime,
        saida: LocalDateTime,
        escala: Escala?
    ): CalculoHoras {
        val horasTrabalhadas = Duration.between(entrada, saida)
        
        if (escala == null) {
            return CalculoHoras(
                horasTrabalhadas = horasTrabalhadas.toMinutes() / 60.0,
                horasEsperadas = 8.0,
                saldo = (horasTrabalhadas.toMinutes() / 60.0) - 8.0
            )
        }
        
        // Descontar intervalo
        val horasTrabalhadasLiquidas = horasTrabalhadas.toMinutes() - escala.intervaloMinutos
        val horasTrabalhadasDecimal = horasTrabalhadasLiquidas / 60.0
        
        val saldo = horasTrabalhadasDecimal - escala.cargaHorariaDiaria
        
        return CalculoHoras(
            horasTrabalhadas = horasTrabalhadasDecimal,
            horasEsperadas = escala.cargaHorariaDiaria,
            saldo = saldo
        )
    }
    
    /**
     * Converte DayOfWeek para DiaSemana
     */
    private fun converterDiaSemana(dayOfWeek: DayOfWeek): DiaSemana {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> DiaSemana.SEGUNDA
            DayOfWeek.TUESDAY -> DiaSemana.TERCA
            DayOfWeek.WEDNESDAY -> DiaSemana.QUARTA
            DayOfWeek.THURSDAY -> DiaSemana.QUINTA
            DayOfWeek.FRIDAY -> DiaSemana.SEXTA
            DayOfWeek.SATURDAY -> DiaSemana.SABADO
            DayOfWeek.SUNDAY -> DiaSemana.DOMINGO
        }
    }
    
    /**
     * Parse dias da semana do JSON
     */
    private fun parseDiasSemana(diasJson: String): List<DiaSemana> {
        return try {
            val diasString: List<String> = objectMapper.readValue(diasJson)
            diasString.map { DiaSemana.valueOf(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class ResultadoValidacao(
    val valido: Boolean,
    val mensagem: String?
)

data class CalculoHoras(
    val horasTrabalhadas: Double,
    val horasEsperadas: Double,
    val saldo: Double
)
