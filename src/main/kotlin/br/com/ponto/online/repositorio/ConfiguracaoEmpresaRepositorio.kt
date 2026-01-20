package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.ConfiguracaoEmpresa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfiguracaoEmpresaRepositorio : JpaRepository<ConfiguracaoEmpresa, Long> {
    /**
     * Busca a configuração mais recente (última criada/atualizada)
     * Assumindo que sempre haverá apenas uma configuração ativa
     */
    fun findFirstByOrderByIdDesc(): ConfiguracaoEmpresa?
}
