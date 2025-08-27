package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.Funcionario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FuncionarioRepositorio : JpaRepository<Funcionario, Long> {
}