package br.com.ponto.online.controle

import br.com.ponto.online.dto.EscalaRequisicaoDTO
import br.com.ponto.online.dto.EscalaRespostaDTO
import br.com.ponto.online.servico.EscalaServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/escalas")
class EscalaControle(
    private val escalaServico: EscalaServico
) {
    
    @GetMapping
    fun listarTodas(): ResponseEntity<List<EscalaRespostaDTO>> {
        val escalas = escalaServico.listarTodas()
        val respostaDTOs = escalas.map { EscalaRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/ativas")
    fun listarAtivas(): ResponseEntity<List<EscalaRespostaDTO>> {
        val escalas = escalaServico.listarAtivas()
        val respostaDTOs = escalas.map { EscalaRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/padrao")
    fun buscarPadrao(): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.buscarEscalaPadrao()
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(EscalaRespostaDTO.deEntidade(escala))
    }
    
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.buscarPorId(id)
        return ResponseEntity.ok(EscalaRespostaDTO.deEntidade(escala))
    }
    
    @PostMapping
    fun cadastrar(@RequestBody dto: EscalaRequisicaoDTO): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.cadastrar(dto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(EscalaRespostaDTO.deEntidade(escala))
    }
    
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody dto: EscalaRequisicaoDTO
    ): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.atualizar(id, dto)
        return ResponseEntity.ok(EscalaRespostaDTO.deEntidade(escala))
    }
    
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        escalaServico.excluir(id)
        return ResponseEntity.noContent().build()
    }
    
    @PatchMapping("/{id}/status")
    fun alterarStatus(
        @PathVariable id: Long,
        @RequestParam ativo: Boolean
    ): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.alterarStatus(id, ativo)
        return ResponseEntity.ok(EscalaRespostaDTO.deEntidade(escala))
    }
    
    @PatchMapping("/{id}/padrao")
    fun definirComoPadrao(@PathVariable id: Long): ResponseEntity<EscalaRespostaDTO> {
        val escala = escalaServico.definirComoPadrao(id)
        return ResponseEntity.ok(EscalaRespostaDTO.deEntidade(escala))
    }
}
