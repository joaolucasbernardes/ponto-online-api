package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.HistoricoAlteracao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoricoAlteracaoRepositorio : JpaRepository<HistoricoAlteracao, Long> {
    
    fun findByRegistroPontoIdOrderByDataHoraDesc(registroPontoId: Long): List<HistoricoAlteracao>
    
    fun findByUsuarioIdOrderByDataHoraDesc(usuarioId: Long): List<HistoricoAlteracao>

    fun findByJustificativaIdOrderByDataHoraDesc(justificativaId: Long): List<HistoricoAlteracao>
}
