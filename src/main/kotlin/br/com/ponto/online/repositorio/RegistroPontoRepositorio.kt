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
    
    fun findByFuncionarioIdAndDataHoraBetween(funcionarioId: Long, inicio: LocalDateTime, fim: LocalDateTime): List<RegistroPonto>
    
    // Métodos para dashboard
    fun countByDataHoraBetween(inicio: LocalDateTime, fim: LocalDateTime): Long
    
    fun findByDataHoraBetween(inicio: LocalDateTime, fim: LocalDateTime): List<RegistroPonto>

    // Buscar últimos registros gerais (para histórico admin)
    fun findTop20ByOrderByDataHoraDesc(): List<RegistroPonto>
}