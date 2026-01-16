package br.com.ponto.online.servico

import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.entidade.HistoricoAlteracao
import br.com.ponto.online.entidade.RegistroPonto
import br.com.ponto.online.enums.TipoAlteracao
import br.com.ponto.online.repositorio.HistoricoAlteracaoRepositorio
import org.springframework.stereotype.Service

@Service
class HistoricoAlteracaoServico(
    private val historicoAlteracaoRepositorio: HistoricoAlteracaoRepositorio
) {
    
    fun registrarCriacao(
        descricao: String,
        usuario: Funcionario,
        registroPonto: RegistroPonto? = null,
        ipOrigem: String? = null
    ): HistoricoAlteracao {
        val historico = HistoricoAlteracao(
            registroPonto = registroPonto,
            tipoAlteracao = TipoAlteracao.CRIACAO,
            descricao = descricao,
            usuario = usuario,
            ipOrigem = ipOrigem
        )
        return historicoAlteracaoRepositorio.save(historico)
    }
    
    fun registrarEdicao(
        descricao: String,
        valorAnterior: String,
        valorNovo: String,
        usuario: Funcionario,
        registroPonto: RegistroPonto? = null,
        ipOrigem: String? = null
    ): HistoricoAlteracao {
        val historico = HistoricoAlteracao(
            registroPonto = registroPonto,
            tipoAlteracao = TipoAlteracao.EDICAO,
            descricao = descricao,
            valorAnterior = valorAnterior,
            valorNovo = valorNovo,
            usuario = usuario,
            ipOrigem = ipOrigem
        )
        return historicoAlteracaoRepositorio.save(historico)
    }
    
    fun registrarExclusao(
        descricao: String,
        valorAnterior: String,
        usuario: Funcionario,
        registroPonto: RegistroPonto? = null,
        ipOrigem: String? = null
    ): HistoricoAlteracao {
        val historico = HistoricoAlteracao(
            registroPonto = registroPonto,
            tipoAlteracao = TipoAlteracao.EXCLUSAO,
            descricao = descricao,
            valorAnterior = valorAnterior,
            usuario = usuario,
            ipOrigem = ipOrigem
        )
        return historicoAlteracaoRepositorio.save(historico)
    }
    
    fun registrarAjuste(
        descricao: String,
        valorAnterior: String,
        valorNovo: String,
        usuario: Funcionario,
        registroPonto: RegistroPonto,
        ipOrigem: String? = null
    ): HistoricoAlteracao {
        val historico = HistoricoAlteracao(
            registroPonto = registroPonto,
            tipoAlteracao = TipoAlteracao.AJUSTE,
            descricao = descricao,
            valorAnterior = valorAnterior,
            valorNovo = valorNovo,
            usuario = usuario,
            ipOrigem = ipOrigem
        )
        return historicoAlteracaoRepositorio.save(historico)
    }
    
    fun registrarAlteracao(
        descricao: String,
        valorAnterior: String? = null,
        valorNovo: String? = null,
        usuario: Funcionario,
        registroPonto: RegistroPonto? = null,
        ipOrigem: String? = null
    ): HistoricoAlteracao {
        val historico = HistoricoAlteracao(
            registroPonto = registroPonto,
            tipoAlteracao = TipoAlteracao.EDICAO,
            descricao = descricao,
            valorAnterior = valorAnterior,
            valorNovo = valorNovo,
            usuario = usuario,
            ipOrigem = ipOrigem
        )
        return historicoAlteracaoRepositorio.save(historico)
    }
    
    fun buscarPorRegistroPonto(registroPontoId: Long): List<HistoricoAlteracao> {
        return historicoAlteracaoRepositorio.findByRegistroPontoIdOrderByDataHoraDesc(registroPontoId)
    }
    
    fun buscarPorUsuario(usuarioId: Long): List<HistoricoAlteracao> {
        return historicoAlteracaoRepositorio.findByUsuarioIdOrderByDataHoraDesc(usuarioId)
    }
}
