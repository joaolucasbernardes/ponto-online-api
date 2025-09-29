package br.com.ponto.online.controle

import br.com.ponto.online.dto.RegistroPontoRequisicaoDTO
import br.com.ponto.online.dto.RegistroPontoRespostaDTO
import br.com.ponto.online.servico.RegistroPontoServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/registros-ponto")
class RegistroPontoControle(
    private val registroPontoServico: RegistroPontoServico
) {

    @PostMapping
    fun registrar(@RequestBody requisicaoDTO: RegistroPontoRequisicaoDTO): ResponseEntity<RegistroPontoRespostaDTO> {
        val registroSalvo = registroPontoServico.registrar(requisicaoDTO)
        val respostaDTO = RegistroPontoRespostaDTO.deEntidade(registroSalvo)
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO)
    }

    @GetMapping("/funcionario/{funcionarioId}")
    fun buscarHistoricoPorFuncionario(@PathVariable funcionarioId: Long): ResponseEntity<List<RegistroPontoRespostaDTO>> {
        val registros = registroPontoServico.buscarHistoricoPorFuncionario(funcionarioId)
        val respostaDTOs = registros.map { registro ->
            RegistroPontoRespostaDTO.deEntidade(registro)
        }
        return ResponseEntity.ok(respostaDTOs)
    }

    @GetMapping("/funcionario/{funcionarioId}/hoje")
    fun buscarRegistrosDeHoje(@PathVariable funcionarioId: Long): ResponseEntity<List<RegistroPontoRespostaDTO>> {
        val registros = registroPontoServico.buscarRegistrosDeHoje(funcionarioId)
        val respostaDTOs = registros.map { RegistroPontoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
}