package br.com.ponto.online.controle

import br.com.ponto.online.dto.AprovacaoJustificativaDTO
import br.com.ponto.online.dto.JustificativaRequisicaoDTO
import br.com.ponto.online.dto.JustificativaRespostaDTO
import br.com.ponto.online.servico.JustificativaServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/justificativas")
class JustificativaControle(
    private val justificativaServico: JustificativaServico
) {
    
    @PostMapping
    fun criarJustificativa(@RequestBody requisicaoDTO: JustificativaRequisicaoDTO): ResponseEntity<JustificativaRespostaDTO> {
        val justificativa = justificativaServico.criarJustificativa(requisicaoDTO)
        val respostaDTO = JustificativaRespostaDTO.deEntidade(justificativa)
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO)
    }
    
    @GetMapping
    fun listarTodas(): ResponseEntity<List<JustificativaRespostaDTO>> {
        val justificativas = justificativaServico.listarTodas()
        val respostaDTOs = justificativas.map { JustificativaRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/funcionario/{funcionarioId}")
    fun listarPorFuncionario(@PathVariable funcionarioId: Long): ResponseEntity<List<JustificativaRespostaDTO>> {
        val justificativas = justificativaServico.listarPorFuncionario(funcionarioId)
        val respostaDTOs = justificativas.map { JustificativaRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/pendentes")
    fun listarPendentes(): ResponseEntity<List<JustificativaRespostaDTO>> {
        val justificativas = justificativaServico.listarPendentes()
        val respostaDTOs = justificativas.map { JustificativaRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
    
    @GetMapping("/pendentes/count")
    fun contarPendentes(): ResponseEntity<Map<String, Long>> {
        val count = justificativaServico.contarPendentes()
        return ResponseEntity.ok(mapOf("count" to count))
    }
    
    @PostMapping("/{id}/processar")
    fun processarJustificativa(
        @PathVariable id: Long,
        @RequestBody aprovacaoDTO: AprovacaoJustificativaDTO
    ): ResponseEntity<JustificativaRespostaDTO> {
        val justificativa = justificativaServico.aprovarOuRejeitar(id, aprovacaoDTO)
        val respostaDTO = JustificativaRespostaDTO.deEntidade(justificativa)
        return ResponseEntity.ok(respostaDTO)
    }
}
