package br.com.ponto.online.controle

import br.com.ponto.online.dto.ConfiguracaoEmpresaDTO
import br.com.ponto.online.servico.ConfiguracaoEmpresaServico
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/configuracoes")
class ConfiguracaoEmpresaControle(
    private val configuracaoServico: ConfiguracaoEmpresaServico
) {
    
    /**
     * Obtém a configuração atual da empresa
     */
    @GetMapping
    fun obterConfiguracao(): ResponseEntity<ConfiguracaoEmpresaDTO> {
        val configuracao = configuracaoServico.obterConfiguracao()
        val dto = ConfiguracaoEmpresaDTO.deEntidade(configuracao)
        return ResponseEntity.ok(dto)
    }
    
    /**
     * Atualiza a configuração da empresa
     */
    @PutMapping
    fun atualizarConfiguracao(
        @RequestBody dto: ConfiguracaoEmpresaDTO
    ): ResponseEntity<ConfiguracaoEmpresaDTO> {
        val configuracaoAtualizada = configuracaoServico.atualizarConfiguracao(dto)
        val respostaDTO = ConfiguracaoEmpresaDTO.deEntidade(configuracaoAtualizada)
        return ResponseEntity.ok(respostaDTO)
    }
}
