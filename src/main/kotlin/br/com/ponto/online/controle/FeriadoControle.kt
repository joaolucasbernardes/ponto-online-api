package br.com.ponto.online.controle

import br.com.ponto.online.dto.FeriadoRequisicaoDTO
import br.com.ponto.online.dto.FeriadoRespostaDTO
import br.com.ponto.online.servico.FeriadoServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/feriados")
class FeriadoControle(
    private val feriadoServico: FeriadoServico
) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    /**
     * Lista todos os feriados
     */
    @GetMapping
    fun listarTodos(): ResponseEntity<List<FeriadoRespostaDTO>> {
        val feriados = feriadoServico.listarTodos()
        val respostaDTOs = feriados.map { FeriadoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    /**
     * Lista feriados em um período específico
     */
    @GetMapping("/periodo")
    fun listarPorPeriodo(
        @RequestParam inicio: String,
        @RequestParam fim: String
    ): ResponseEntity<List<FeriadoRespostaDTO>> {
        val dataInicio = LocalDate.parse(inicio, dateFormatter)
        val dataFim = LocalDate.parse(fim, dateFormatter)
        
        val feriados = feriadoServico.listarPorPeriodo(dataInicio, dataFim)
        val respostaDTOs = feriados.map { FeriadoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    /**
     * Cadastra um novo feriado
     */
    @PostMapping
    fun cadastrar(
        @RequestBody dto: FeriadoRequisicaoDTO
    ): ResponseEntity<FeriadoRespostaDTO> {
        val feriado = feriadoServico.cadastrar(dto)
        val respostaDTO = FeriadoRespostaDTO.deEntidade(feriado)
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO)
    }
    
    /**
     * Atualiza um feriado existente
     */
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody dto: FeriadoRequisicaoDTO
    ): ResponseEntity<FeriadoRespostaDTO> {
        val feriado = feriadoServico.atualizar(id, dto)
        val respostaDTO = FeriadoRespostaDTO.deEntidade(feriado)
        return ResponseEntity.ok(respostaDTO)
    }
    
    /**
     * Exclui um feriado
     */
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        feriadoServico.excluir(id)
        return ResponseEntity.noContent().build()
    }
}
