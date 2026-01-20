package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Feriado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface FeriadoRepositorio : JpaRepository<Feriado, Long> {
    /**
     * Busca feriados em um período específico
     */
    fun findByDataBetween(inicio: LocalDate, fim: LocalDate): List<Feriado>
    
    /**
     * Busca feriado em uma data específica
     */
    fun findByData(data: LocalDate): Feriado?
    
    /**
     * Busca todos os feriados recorrentes (que se repetem anualmente)
     */
    fun findByRecorrenteTrue(): List<Feriado>
    
    /**
     * Busca feriados ordenados por data
     */
    fun findAllByOrderByDataAsc(): List<Feriado>
}
