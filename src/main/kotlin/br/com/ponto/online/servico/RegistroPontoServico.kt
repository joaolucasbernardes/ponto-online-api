package br.com.ponto.online.servico

import br.com.ponto.online.dto.RegistroPontoRequisicaoDTO
import br.com.ponto.online.entidade.RegistroPonto
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class RegistroPontoServico(
    private val registroPontoRepositorio: RegistroPontoRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio,
    private val geolocalizacaoServico: GeolocalizacaoServico
) {
    fun registrar(requisicaoDTO: RegistroPontoRequisicaoDTO): RegistroPonto {
        val funcionario = funcionarioRepositorio.findById(requisicaoDTO.funcionarioId)
            .orElseThrow { NoSuchElementException("Funcionário com ID ${requisicaoDTO.funcionarioId} não encontrado.") }

        // Determinar data/hora do registro
        val dataHoraRegistro = requisicaoDTO.dataHora ?: LocalDateTime.now()
        val dataRegistro = dataHoraRegistro.toLocalDate()
        val hoje = LocalDate.now()

        // VALIDAÇÃO 1: Bloquear registro retroativo (exceto para ADMIN)
        if (dataRegistro.isBefore(hoje)) {
            val authentication = SecurityContextHolder.getContext().authentication
            val authorities = authentication?.authorities?.map { it.authority } ?: emptyList()
            
            if (!authorities.contains("ROLE_ADMIN")) {
                throw IllegalStateException(
                    "Registro retroativo não permitido. Contate o administrador para ajustes."
                )
            }
        }

        val inicioDoDia = dataRegistro.atStartOfDay()
        val fimDoDia = dataRegistro.atTime(23, 59, 59)

        // Buscar registros do dia
        val registrosDeHoje = registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetweenOrderByDataHoraAsc(
            funcionario.id!!, inicioDoDia, fimDoDia
        )
        val quantidadeRegistros = registrosDeHoje.size

        // VALIDAÇÃO 2: Limite de 4 registros por dia
        if (quantidadeRegistros >= 4) {
            throw IllegalStateException("Limite de 4 registros de ponto por dia já foi atingido.")
        }

        // VALIDAÇÃO 3: Intervalo mínimo de 30 minutos entre registros
        val ultimoRegistro = registrosDeHoje.lastOrNull()
        if (ultimoRegistro != null) {
            val intervalo = Duration.between(ultimoRegistro.dataHora, dataHoraRegistro)
            val minutosIntervalo = intervalo.toMinutes()
            
            if (minutosIntervalo < 30) {
                throw IllegalStateException(
                    "Intervalo mínimo de 30 minutos entre registros não foi respeitado. " +
                    "Aguarde ${30 - minutosIntervalo} minuto(s)."
                )
            }
        }

        // VALIDAÇÃO 4: Determinar tipo esperado (sequência ENTRADA → SAIDA → ENTRADA → SAIDA)
        val tipoEsperado = when (quantidadeRegistros) {
            0, 2 -> "ENTRADA" // 1º e 3º registros devem ser ENTRADA
            1, 3 -> "SAIDA"   // 2º e 4º registros devem ser SAIDA
            else -> throw IllegalStateException("Estado inválido de registros")
        }

        // Usar tipo fornecido ou inferir
        val tipoRegistro = requisicaoDTO.tipo ?: tipoEsperado

        // Validar se o tipo fornecido está correto
        if (tipoRegistro != tipoEsperado) {
            throw IllegalStateException(
                "Tipo de registro inválido. Esperado: $tipoEsperado, mas recebeu: $tipoRegistro"
            )
        }

        // VALIDAÇÃO 5: Jornada excessiva (alertar em 8h, bloquear em 10h)
        if (quantidadeRegistros == 3 && tipoRegistro == "SAIDA") {
            // Calculando jornada ao registrar o 4º ponto (saída final)
            val entrada1 = registrosDeHoje[0].dataHora
            val saida1 = registrosDeHoje[1].dataHora
            val entrada2 = registrosDeHoje[2].dataHora
            val saida2 = dataHoraRegistro

            val manha = Duration.between(entrada1, saida1)
            val tarde = Duration.between(entrada2, saida2)
            val totalHoras = manha.plus(tarde).toHours()

            if (totalHoras > 10) {
                throw IllegalStateException(
                    "Jornada de trabalho excede o limite de 10 horas (8h normais + 2h extras). " +
                    "Total: ${totalHoras}h. Contate o administrador para ajuste."
                )
            }

            // Alerta se passou de 8h (início das horas extras)
            if (totalHoras > 8) {
                // Log de alerta (pode ser enviado por email/notificação futuramente)
                println("⚠️ ALERTA: Funcionário ${funcionario.nome} trabalhou ${totalHoras}h (${totalHoras - 8}h extras)")
            }
        }

        // VALIDAÇÃO 6: Processar geolocalização (se fornecida)
        var localPermitido: br.com.ponto.online.entidade.LocalPermitido? = null
        var dentroDoRaio: Boolean? = null
        
        if (requisicaoDTO.latitude != null && requisicaoDTO.longitude != null) {
            val (local, _) = geolocalizacaoServico.validarLocalizacao(
                requisicaoDTO.latitude,
                requisicaoDTO.longitude
            )
            localPermitido = local
            dentroDoRaio = local != null
        }

        // Criar e salvar o registro
        val novoRegistro = RegistroPonto(
            funcionario = funcionario,
            dataHora = dataHoraRegistro,
            tipo = tipoRegistro,
            latitude = requisicaoDTO.latitude,
            longitude = requisicaoDTO.longitude,
            precisaoMetros = requisicaoDTO.precisaoMetros,
            dentroDoRaio = dentroDoRaio,
            localPermitido = localPermitido
        )

        return registroPontoRepositorio.save(novoRegistro)
    }

    fun buscarHistoricoPorFuncionario(funcionarioId: Long): List<RegistroPonto> {
        return registroPontoRepositorio.findByFuncionarioIdOrderByDataHoraDesc(funcionarioId)
    }

    fun buscarRegistrosDeHoje(funcionarioId: Long): List<RegistroPonto> {
        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(23, 59, 59)
        return registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetweenOrderByDataHoraAsc(
            funcionarioId, inicioDoDia, fimDoDia
        )
    }

    fun buscarUltimosRegistros(): List<RegistroPonto> {
        return registroPontoRepositorio.findTop20ByOrderByDataHoraDesc()
    }
}