package br.com.ponto.online.controle

import br.com.ponto.online.dto.LocalPermitidoRequisicaoDTO
import br.com.ponto.online.dto.LocalPermitidoRespostaDTO
import br.com.ponto.online.servico.LocalPermitidoServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/locais-permitidos")
class LocalPermitidoControle(
    private val localPermitidoServico: LocalPermitidoServico
) {
    
    /**
     * Lista todos os locais permitidos
     */
    @GetMapping
    fun listarTodos(): ResponseEntity<List<LocalPermitidoRespostaDTO>> {
        val locais = localPermitidoServico.listarTodos()
        val respostaDTOs = locais.map { LocalPermitidoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    /**
     * Lista apenas locais ativos
     */
    @GetMapping("/ativos")
    fun listarAtivos(): ResponseEntity<List<LocalPermitidoRespostaDTO>> {
        val locais = localPermitidoServico.listarAtivos()
        val respostaDTOs = locais.map { LocalPermitidoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    /**
     * Busca local por ID
     */
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<LocalPermitidoRespostaDTO> {
        val local = localPermitidoServico.buscarPorId(id)
        val respostaDTO = LocalPermitidoRespostaDTO.deEntidade(local)
        return ResponseEntity.ok(respostaDTO)
    }
    
    /**
     * Cadastra novo local
     */
    @PostMapping
    fun cadastrar(@RequestBody dto: LocalPermitidoRequisicaoDTO): ResponseEntity<LocalPermitidoRespostaDTO> {
        val local = localPermitidoServico.cadastrar(dto)
        val respostaDTO = LocalPermitidoRespostaDTO.deEntidade(local)
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO)
    }
    
    /**
     * Atualiza local existente
     */
    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody dto: LocalPermitidoRequisicaoDTO
    ): ResponseEntity<LocalPermitidoRespostaDTO> {
        val local = localPermitidoServico.atualizar(id, dto)
        val respostaDTO = LocalPermitidoRespostaDTO.deEntidade(local)
        return ResponseEntity.ok(respostaDTO)
    }
    
    /**
     * Exclui local
     */
    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        localPermitidoServico.excluir(id)
        return ResponseEntity.noContent().build()
    }
    
    /**
     * Ativa ou desativa local
     */
    @PatchMapping("/{id}/status")
    fun alterarStatus(
        @PathVariable id: Long,
        @RequestParam ativo: Boolean
    ): ResponseEntity<LocalPermitidoRespostaDTO> {
        val local = localPermitidoServico.alterarStatus(id, ativo)
        val respostaDTO = LocalPermitidoRespostaDTO.deEntidade(local)
        return ResponseEntity.ok(respostaDTO)
    }
}
