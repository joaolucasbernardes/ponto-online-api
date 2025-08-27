package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.RegistroPonto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistroPontoRepositorio : JpaRepository<RegistroPonto, Long> {
}