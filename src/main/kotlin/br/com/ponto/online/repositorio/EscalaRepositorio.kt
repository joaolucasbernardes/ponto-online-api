package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Escala
import br.com.ponto.online.enumeracao.TipoEscala
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EscalaRepositorio : JpaRepository<Escala, Long> {
    
    // Buscar escalas ativas
    fun findByAtivoTrue(): List<Escala>
    
    // Buscar escala padrão
    fun findByEscalaPadraoTrue(): Escala?
    
    // Buscar por tipo
    fun findByTipo(tipo: TipoEscala): List<Escala>
    
    // Buscar por nome
    fun findByNomeContainingIgnoreCase(nome: String): List<Escala>
    
    // Buscar escalas ativas ordenadas por nome
    fun findByAtivoTrueOrderByNomeAsc(): List<Escala>
}
