package br.com.ponto.online.repositorio

import br.com.ponto.online.entidade.RegistroPonto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface RegistroPontoRepositorio : JpaRepository<RegistroPonto, Long> {
    fun findByFuncionarioIdOrderByDataHoraDesc(funcionarioId: Long): List<RegistroPonto>

    fun countByFuncionarioIdAndDataHoraBetween(funcionarioId: Long, inicio: LocalDateTime, fim: LocalDateTime): Long

    fun findByFuncionarioIdAndDataHoraBetweenOrderByDataHoraAsc(funcionarioId: Long, inicio: LocalDateTime, fim: LocalDateTime): List<RegistroPonto>
}