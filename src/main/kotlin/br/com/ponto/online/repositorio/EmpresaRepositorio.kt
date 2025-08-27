package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Empresa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmpresaRepositorio : JpaRepository<Empresa, Long> {
}