package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Escala
import br.com.ponto.online.entidade.TurnoTrabalho
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TurnoTrabalhoRepositorio : JpaRepository<TurnoTrabalho, Long> {
    
    // Buscar turnos ativos
    fun findByAtivoTrue(): List<TurnoTrabalho>
    
    // Buscar turnos por escala
    fun findByEscala(escala: Escala): List<TurnoTrabalho>
    
    // Buscar turnos por escala ativa
    fun findByEscalaAndAtivoTrue(escala: Escala): List<TurnoTrabalho>
    
    // Buscar por nome
    fun findByNomeContainingIgnoreCase(nome: String): List<TurnoTrabalho>
}
