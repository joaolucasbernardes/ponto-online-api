package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.entidade.HistoricoEscala
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoricoEscalaRepositorio : JpaRepository<HistoricoEscala, Long> {
    
    // Buscar histórico por funcionário
    fun findByFuncionarioOrderByDataAlteracaoDesc(funcionario: Funcionario): List<HistoricoEscala>
    
    // Buscar histórico por funcionário ID
    fun findByFuncionarioIdOrderByDataAlteracaoDesc(funcionarioId: Long): List<HistoricoEscala>
    
    // Buscar última alteração de um funcionário
    fun findFirstByFuncionarioOrderByDataAlteracaoDesc(funcionario: Funcionario): HistoricoEscala?
}
