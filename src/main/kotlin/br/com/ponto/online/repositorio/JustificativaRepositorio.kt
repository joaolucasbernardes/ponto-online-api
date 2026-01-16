package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Justificativa
import br.com.ponto.online.enums.StatusJustificativa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface JustificativaRepositorio : JpaRepository<Justificativa, Long> {
    
    fun findByFuncionarioIdOrderByDataCriacaoDesc(funcionarioId: Long): List<Justificativa>
    
    fun findByStatusOrderByDataCriacaoAsc(status: StatusJustificativa): List<Justificativa>
    
    fun findByFuncionarioIdAndData(funcionarioId: Long, data: LocalDate): Justificativa?
    
    fun findByFuncionarioIdAndDataBetween(
        funcionarioId: Long,
        dataInicio: LocalDate,
        dataFim: LocalDate
    ): List<Justificativa>
    
    fun countByStatus(status: StatusJustificativa): Long
}
