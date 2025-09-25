package br.com.ponto.online.servico

import br.com.ponto.online.dto.RegistroPontoRequisicaoDTO
import br.com.ponto.online.entidade.RegistroPonto
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import org.springframework.stereotype.Service
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

        val novoRegistro = RegistroPonto( funcionario = funcionario, dataHora = LocalDateTime.now())

        return registroPontoRepositorio.save(novoRegistro)
    }
}