package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.LocalPermitido
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalPermitidoRepositorio : JpaRepository<LocalPermitido, Long> {
    
    // Buscar locais ativos
    fun findByAtivoTrue(): List<LocalPermitido>
    
    // Buscar por nome
    fun findByNomeContainingIgnoreCase(nome: String): List<LocalPermitido>
    
    // Buscar locais ativos ordenados por nome
    fun findByAtivoTrueOrderByNomeAsc(): List<LocalPermitido>
}
