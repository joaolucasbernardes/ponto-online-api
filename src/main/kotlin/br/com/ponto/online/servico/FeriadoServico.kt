package br.com.ponto.online.servico

import br.com.ponto.online.dto.FeriadoRequisicaoDTO
import br.com.ponto.online.entidade.Feriado
import br.com.ponto.online.repositorio.FeriadoRepositorio
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class FeriadoServico(
    private val feriadoRepositorio: FeriadoRepositorio
) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Lista todos os feriados ordenados por data
     */
    fun listarTodos(): List<Feriado> {
        return feriadoRepositorio.findAllByOrderByDataAsc()
    }
    
    /**
     * Lista feriados em um período específico
     */
    fun listarPorPeriodo(inicio: LocalDate, fim: LocalDate): List<Feriado> {
        return feriadoRepositorio.findByDataBetween(inicio, fim)
    }
    
    /**
     * Cadastra um novo feriado
     */
    fun cadastrar(dto: FeriadoRequisicaoDTO): Feriado {
        validarFeriado(dto)
        
        val feriado = Feriado(
            nome = dto.nome.trim(),
            data = LocalDate.parse(dto.data, dateFormatter),
            tipo = dto.tipo,
            recorrente = dto.recorrente
        )
        
        return feriadoRepositorio.save(feriado)
    }
    
    /**
     * Atualiza um feriado existente
     */
    fun atualizar(id: Long, dto: FeriadoRequisicaoDTO): Feriado {
        validarFeriado(dto)
        
        val feriadoExistente = feriadoRepositorio.findById(id)
            .orElseThrow { IllegalArgumentException("Feriado com ID $id não encontrado") }
        
        val feriadoAtualizado = feriadoExistente.copy(
            nome = dto.nome.trim(),
            data = LocalDate.parse(dto.data, dateFormatter),
            tipo = dto.tipo,
            recorrente = dto.recorrente
        )
        
        return feriadoRepositorio.save(feriadoAtualizado)
    }
    
    /**
     * Exclui um feriado
     */
    fun excluir(id: Long) {
        if (!feriadoRepositorio.existsById(id)) {
            throw IllegalArgumentException("Feriado com ID $id não encontrado")
        }
        feriadoRepositorio.deleteById(id)
    }
    
    /**
     * Verifica se uma data é feriado
     */
    fun isFeriado(data: LocalDate): Boolean {
        // Verifica feriado exato
        val feriadoExato = feriadoRepositorio.findByData(data)
        if (feriadoExato != null) {
            return true
        }
        
        // Verifica feriados recorrentes (mesmo dia e mês, qualquer ano)
        val feriadosRecorrentes = feriadoRepositorio.findByRecorrenteTrue()
        return feriadosRecorrentes.any { 
            it.data.dayOfMonth == data.dayOfMonth && 
            it.data.month == data.month 
        }
    }
    
    /**
     * Busca feriado por data
     */
    fun buscarPorData(data: LocalDate): Feriado? {
        return feriadoRepositorio.findByData(data)
    }
    
    /**
     * Valida dados do feriado
     */
    private fun validarFeriado(dto: FeriadoRequisicaoDTO) {
        require(dto.nome.isNotBlank()) {
            "Nome do feriado é obrigatório"
        }
        require(dto.nome.length <= 100) {
            "Nome do feriado deve ter no máximo 100 caracteres"
        }
        
        try {
            LocalDate.parse(dto.data, dateFormatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Data inválida. Use o formato YYYY-MM-DD")
        }
    }
}
