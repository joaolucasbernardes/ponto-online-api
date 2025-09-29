package br.com.ponto.online.servico

import br.com.ponto.online.dto.RegistroPontoRequisicaoDTO
import br.com.ponto.online.entidade.RegistroPonto
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class RegistroPontoServico(
    private val registroPontoRepositorio: RegistroPontoRepositorio,
    private val funcionarioRepositorio: FuncionarioRepositorio
) {
    fun registrar(requisicaoDTO: RegistroPontoRequisicaoDTO): RegistroPonto {
        val funcionario = funcionarioRepositorio.findById(requisicaoDTO.funcionarioId)
            .orElseThrow { NoSuchElementException("Funcionário com ID ${requisicaoDTO.funcionarioId} não encontrado.") }

        val hoje = LocalDate.now()
        val inicioDoDia = hoje.atStartOfDay()
        val fimDoDia = hoje.atTime(23, 59, 59)

        val registrosDeHoje = registroPontoRepositorio.countByFuncionarioIdAndDataHoraBetween(
            funcionario.id!!, inicioDoDia, fimDoDia
        )

        if (registrosDeHoje >= 4) {
            throw IllegalStateException("Limite de 4 registros de ponto por dia já foi atingido.")
        }

        val novoRegistro = RegistroPonto(funcionario = funcionario, dataHora = LocalDateTime.now())

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
}