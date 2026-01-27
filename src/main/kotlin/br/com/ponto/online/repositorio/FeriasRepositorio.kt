package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Ferias
import br.com.ponto.online.enums.StatusFerias
import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FeriasRepositorio : JpaRepository<Ferias, Long> {

    // Buscar férias de um funcionário ordenadas por data início decrescente
    fun findByFuncionarioIdOrderByDataInicioDesc(funcionarioId: Long): List<Ferias>

    // Buscar todas as férias ordenadas por data início decrescente
    fun findAllByOrderByDataInicioDesc(): List<Ferias>

    // Buscar férias por status
    fun findByStatusOrderByDataInicioAsc(status: StatusFerias): List<Ferias>

    // Verificar conflito de datas para o mesmo funcionário
    // Busca férias que não sejam REJEITADA ou CANCELADA e que tenham interseção com o período
    @Query(
            """
        SELECT COUNT(f) > 0 FROM Ferias f 
        WHERE f.funcionario.id = :funcionarioId 
        AND f.status NOT IN ('REJEITADA', 'CANCELADA')
        AND (
            (f.dataInicio <= :dataFim AND f.dataFim >= :dataInicio)
        )
    """
    )
    fun existeConflitoDatas(funcionarioId: Long, dataInicio: LocalDate, dataFim: LocalDate): Boolean

    @Query(
            """
        SELECT f FROM Ferias f 
        WHERE f.funcionario.id = :funcionarioId 
        AND f.status NOT IN :statuses
        AND (
            (f.dataInicio <= :dataFim AND f.dataFim >= :dataInicio)
        )
    """
    )
    fun findFeriasConflitantes(
            funcionarioId: Long,
            dataInicio: LocalDate,
            dataFim: LocalDate,
            statuses: List<StatusFerias>
    ): List<Ferias>
}
