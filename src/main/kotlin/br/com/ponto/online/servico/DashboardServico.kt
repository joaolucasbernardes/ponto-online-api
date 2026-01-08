package br.com.ponto.online.servico

import br.com.ponto.online.dto.*
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class DashboardServico(
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val registroPontoRepositorio: RegistroPontoRepositorio,
    private val calculoHorasServico: CalculoHorasServico
) {

    fun obterEstatisticas(): DashboardEstatisticasDTO {
        val totalFuncionarios = funcionarioRepositorio.count().toInt()
        
        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(23, 59, 59)
        
        // Contar registros de hoje
        val registrosHoje = registroPontoRepositorio.countByDataHoraBetween(inicioDoDia, fimDoDia).toInt()
        
        // Calcular horas trabalhadas hoje
        val funcionarios = funcionarioRepositorio.findAll()
        var totalHorasHoje = Duration.ZERO
        
        funcionarios.forEach { funcionario ->
            val registros = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetween(
                funcionario.id!!,
                inicioDoDia,
                fimDoDia
            )
            totalHorasHoje = totalHorasHoje.plus(calcularHorasTrabalhadasDia(registros))
        }
        
        // Detectar inconsistências
        val inconsistencias = detectarInconsistencias()
        val inconsistenciasHoje = inconsistencias.filter { it.data == hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) }.size
        
        return DashboardEstatisticasDTO(
            totalFuncionarios = totalFuncionarios,
            funcionariosAtivos = totalFuncionarios, // Por enquanto todos são ativos
            registrosHoje = registrosHoje,
            inconsistenciasHoje = inconsistenciasHoje,
            horasTrabalhadasHoje = formatarDuracao(totalHorasHoje)
        )
    }

    fun obterGraficoPresenca(dias: Int = 7): GraficoPresencaDTO {
        val labels = mutableListOf<String>()
        val presentes = mutableListOf<Int>()
        val ausentes = mutableListOf<Int>()
        
        val totalFuncionarios = funcionarioRepositorio.count().toInt()
        
        for (i in (dias - 1) downTo 0) {
            val data = LocalDate.now().minusDays(i.toLong())
            val inicioDoDia = data.atStartOfDay()
            val fimDoDia = data.atTime(23, 59, 59)
            
            // Contar funcionários que bateram ponto neste dia
            val funcionariosComRegistro = registroPontoRepositorio
                .findByDataHoraBetween(inicioDoDia, fimDoDia)
                .map { it.funcionario.id }
                .distinct()
                .size
            
            labels.add(data.format(DateTimeFormatter.ofPattern("dd/MM")))
            presentes.add(funcionariosComRegistro)
            ausentes.add(totalFuncionarios - funcionariosComRegistro)
        }
        
        return GraficoPresencaDTO(
            labels = labels,
            presentes = presentes,
            ausentes = ausentes
        )
    }

    fun listarFuncionariosAtivos(): List<FuncionarioListagemDTO> {
        val funcionarios = funcionarioRepositorio.findAll()
        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(23, 59, 59)
        
        return funcionarios.map { funcionario ->
            val registrosHoje = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetween(
                funcionario.id!!,
                inicioDoDia,
                fimDoDia
            ).sortedBy { it.dataHora }
            
            val ultimoRegistro = registroPontoRepositorio
                .findByFuncionarioIdOrderByDataHoraDesc(funcionario.id!!)
                .firstOrNull()
            
            val statusHoje = when {
                registrosHoje.isEmpty() -> "SEM_REGISTRO"
                registrosHoje.size == 4 -> "COMPLETO"
                else -> "INCOMPLETO"
            }
            
            FuncionarioListagemDTO(
                id = funcionario.id!!,
                nome = funcionario.nome,
                email = funcionario.email,
                empresa = funcionario.empresa.razaoSocial,
                ultimoRegistro = ultimoRegistro?.dataHora?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                statusHoje = statusHoje
            )
        }
    }

    fun detectarInconsistencias(): List<InconsistenciaDTO> {
        val inconsistencias = mutableListOf<InconsistenciaDTO>()
        val funcionarios = funcionarioRepositorio.findAll()
        val hoje = LocalDate.now()
        
        // Verificar últimos 7 dias
        for (i in 0..6) {
            val data = hoje.minusDays(i.toLong())
            val inicioDoDia = data.atStartOfDay()
            val fimDoDia = data.atTime(23, 59, 59)
            
            funcionarios.forEach { funcionario ->
                val registros = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetween(
                    funcionario.id!!,
                    inicioDoDia,
                    fimDoDia
                ).sortedBy { it.dataHora }
                
                // Verificar registro incompleto (1 ou 3 registros)
                if (registros.size == 1 || registros.size == 3) {
                    inconsistencias.add(
                        InconsistenciaDTO(
                            funcionarioId = funcionario.id!!,
                            funcionarioNome = funcionario.nome,
                            data = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            tipo = "REGISTRO_INCOMPLETO",
                            descricao = "Funcionário possui ${registros.size} registro(s), esperado 2 ou 4"
                        )
                    )
                }
                
                // Verificar jornada excessiva (mais de 10 horas)
                if (registros.size >= 2) {
                    val horasTrabalhadas = calcularHorasTrabalhadasDia(registros)
                    if (horasTrabalhadas.toHours() > 10) {
                        inconsistencias.add(
                            InconsistenciaDTO(
                                funcionarioId = funcionario.id!!,
                                funcionarioNome = funcionario.nome,
                                data = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                tipo = "JORNADA_EXCESSIVA",
                                descricao = "Jornada de ${formatarDuracao(horasTrabalhadas)} excede 10 horas"
                            )
                        )
                    }
                }
                
                // Verificar intervalo de almoço (se tiver 4 registros)
                if (registros.size == 4) {
                    val intervalo = Duration.between(registros[1].dataHora, registros[2].dataHora)
                    if (intervalo.toMinutes() < 30) {
                        inconsistencias.add(
                            InconsistenciaDTO(
                                funcionarioId = funcionario.id!!,
                                funcionarioNome = funcionario.nome,
                                data = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                tipo = "SEM_INTERVALO",
                                descricao = "Intervalo de almoço de ${intervalo.toMinutes()} minutos é menor que 30 minutos"
                            )
                        )
                    }
                }
            }
        }
        
        return inconsistencias
    }

    private fun calcularHorasTrabalhadasDia(registros: List<br.com.ponto.online.entidade.RegistroPonto>): Duration {
        if (registros.isEmpty()) return Duration.ZERO

        return when (registros.size) {
            1 -> Duration.ZERO
            2 -> Duration.between(registros[0].dataHora, registros[1].dataHora)
            3 -> Duration.between(registros[0].dataHora, registros[1].dataHora)
            else -> {
                val manha = Duration.between(registros[0].dataHora, registros[1].dataHora)
                val tarde = Duration.between(registros[2].dataHora, registros[3].dataHora)
                manha.plus(tarde)
            }
        }
    }

    private fun formatarDuracao(duracao: Duration): String {
        val horas = duracao.toHours()
        val minutos = duracao.toMinutes() % 60
        return String.format("%02d:%02d", horas, minutos)
    }
}
