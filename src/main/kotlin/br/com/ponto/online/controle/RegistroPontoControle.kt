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
    private val registroPontoServico: RegistroPontoServico,
    private val calculoHorasServico: br.com.ponto.online.servico.CalculoHorasServico
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

    @GetMapping("/funcionario/{funcionarioId}/horas/dia")
    fun calcularHorasDia(
        @PathVariable funcionarioId: Long,
        @RequestParam(required = false) data: String?
    ): ResponseEntity<br.com.ponto.online.dto.CalculoHorasDTO> {
        val dataCalculo = if (data != null) {
            java.time.LocalDate.parse(data, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } else {
            java.time.LocalDate.now()
        }
        
        val calculo = calculoHorasServico.calcularHorasDia(funcionarioId, dataCalculo)
        return ResponseEntity.ok(calculo)
    }

    @GetMapping("/funcionario/{funcionarioId}/horas/mes")
    fun calcularResumoMensal(
        @PathVariable funcionarioId: Long,
        @RequestParam(required = false) mes: Int?,
        @RequestParam(required = false) ano: Int?
    ): ResponseEntity<br.com.ponto.online.dto.ResumoMensalDTO> {
        val mesCalculo = mes ?: java.time.LocalDate.now().monthValue
        val anoCalculo = ano ?: java.time.LocalDate.now().year
        
        val resumo = calculoHorasServico.calcularResumoMensal(funcionarioId, mesCalculo, anoCalculo)
        return ResponseEntity.ok(resumo)
    }

    @GetMapping("/ultimos")
    fun buscarUltimosRegistros(): ResponseEntity<List<RegistroPontoRespostaDTO>> {
        val registros = registroPontoServico.buscarUltimosRegistros()
        val respostaDTOs = registros.map { RegistroPontoRespostaDTO.deEntidade(it) }
        return ResponseEntity.ok(respostaDTOs)
    }
}