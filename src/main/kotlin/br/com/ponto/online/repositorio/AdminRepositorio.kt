package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepositorio : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin?
}
