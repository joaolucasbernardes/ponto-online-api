package br.com.ponto.online.controle

import br.com.ponto.online.dto.HistoricoAlteracaoRespostaDTO
import br.com.ponto.online.servico.HistoricoAlteracaoServico
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/historico-alteracoes")
class HistoricoAlteracaoControle(
    private val historicoServico: HistoricoAlteracaoServico
) {

    @GetMapping("/justificativa/{id}")
    fun buscarPorJustificativa(@PathVariable id: Long): ResponseEntity<List<HistoricoAlteracaoRespostaDTO>> {
        val historico = historicoServico.buscarPorJustificativa(id)
        val dtos = historico.map { HistoricoAlteracaoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(dtos)
    }
}
