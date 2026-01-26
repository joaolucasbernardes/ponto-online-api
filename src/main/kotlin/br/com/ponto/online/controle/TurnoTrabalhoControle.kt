package br.com.ponto.online.controle

import br.com.ponto.online.dto.TurnoTrabalhoRequisicaoDTO
import br.com.ponto.online.dto.TurnoTrabalhoRespostaDTO
import br.com.ponto.online.servico.TurnoTrabalhoServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/turnos")
class TurnoTrabalhoControle(
    private val turnoTrabalhoServico: TurnoTrabalhoServico
) {
    
    @GetMapping
    fun listarTodos(): ResponseEntity<List<TurnoTrabalhoRespostaDTO>> {
        val turnos = turnoTrabalhoServico.listarTodos()
        val respostaDTOs = turnos.map { TurnoTrabalhoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/ativos")
    fun listarAtivos(): ResponseEntity<List<TurnoTrabalhoRespostaDTO>> {
        val turnos = turnoTrabalhoServico.listarAtivos()
        val respostaDTOs = turnos.map { TurnoTrabalhoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<TurnoTrabalhoRespostaDTO> {
        val turno = turnoTrabalhoServico.buscarPorId(id)
        return ResponseEntity.ok(TurnoTrabalhoRespostaDTO.deEntidade(turno))
    }
    
    @PostMapping
    fun cadastrar(@RequestBody dto: TurnoTrabalhoRequisicaoDTO): ResponseEntity<TurnoTrabalhoRespostaDTO> {
        val turno = turnoTrabalhoServico.cadastrar(dto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TurnoTrabalhoRespostaDTO.deEntidade(turno))
    }
    
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody dto: TurnoTrabalhoRequisicaoDTO
    ): ResponseEntity<TurnoTrabalhoRespostaDTO> {
        val turno = turnoTrabalhoServico.atualizar(id, dto)
        return ResponseEntity.ok(TurnoTrabalhoRespostaDTO.deEntidade(turno))
    }
    
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        turnoTrabalhoServico.excluir(id)
        return ResponseEntity.noContent().build()
    }
}
