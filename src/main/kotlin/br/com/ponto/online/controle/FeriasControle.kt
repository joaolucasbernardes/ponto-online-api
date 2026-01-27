package br.com.ponto.online.controle

import br.com.ponto.online.dto.AprovacaoFeriasDTO
import br.com.ponto.online.dto.FeriasRequisicaoDTO
import br.com.ponto.online.dto.FeriasRespostaDTO
import br.com.ponto.online.entidade.Ferias
import br.com.ponto.online.enums.StatusFerias
import br.com.ponto.online.servico.FeriasServico
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.temporal.ChronoUnit
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ferias")
@Tag(name = "Férias", description = "Gerenciamento de solicitações de férias e afastamentos")
class FeriasControle(private val feriasServico: FeriasServico) {

    @Operation(
            summary = "Solicitar Férias",
            description = "Cria uma nova solicitação de férias para um funcionário."
    )
    @ApiResponses(
            value =
                    [
                            ApiResponse(
                                    responseCode = "200",
                                    description = "Solicitação criada com sucesso"
                            ),
                            ApiResponse(
                                    responseCode = "400",
                                    description =
                                            "Erro de validação (datas conflitantes, saldo insuficiente, etc)"
                            )]
    )
    @PostMapping
    fun solicitar(@RequestBody requisicao: FeriasRequisicaoDTO): ResponseEntity<Any> {
        try {
            val ferias = feriasServico.solicitar(requisicao)
            return ResponseEntity.ok(mapearParaDTO(ferias))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(mapOf("mensagem" to e.message))
        }
    }

    @Operation(
            summary = "Listar Férias",
            description = "Lista todas as solicitações de férias, com filtro opcional por status."
    )
    @GetMapping
    fun listarTodas(
            @RequestParam(required = false) status: StatusFerias?
    ): ResponseEntity<List<FeriasRespostaDTO>> {
        val lista =
                if (status != null) {
                    feriasServico.listarPorStatus(status)
                } else {
                    feriasServico.listarTodas()
                }
        return ResponseEntity.ok(lista.map { mapearParaDTO(it) as FeriasRespostaDTO })
    }

    @Operation(
            summary = "Histórico do Funcionário",
            description = "Lista o histórico de férias de um funcionário específico."
    )
    @GetMapping("/funcionario/{id}")
    fun listarPorFuncionario(@PathVariable id: Long): ResponseEntity<List<FeriasRespostaDTO>> {
        val lista = feriasServico.listarPorFuncionario(id)
        return ResponseEntity.ok(lista.map { mapearParaDTO(it) as FeriasRespostaDTO })
    }

    @Operation(
            summary = "Analisar Solicitação (Admin)",
            description = "Aprova ou rejeita uma solicitação de férias."
    )
    @PostMapping("/{id}/analisar")
    fun analisar(
            @PathVariable id: Long,
            @RequestBody aprovacao: AprovacaoFeriasDTO
    ): ResponseEntity<Any> {
        try {
            val ferias = feriasServico.analisar(id, aprovacao)
            return ResponseEntity.ok(mapearParaDTO(ferias))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(mapOf("mensagem" to e.message))
        }
    }

    @Operation(
            summary = "Cancelar Férias",
            description = "Cancela uma solicitação de férias (apenas se ainda não iniciada)."
    )
    @DeleteMapping("/{id}")
    fun cancelar(@PathVariable id: Long, @RequestParam solicitanteId: Long): ResponseEntity<Any> {
        try {
            val ferias = feriasServico.cancelar(id, solicitanteId)
            return ResponseEntity.ok(mapearParaDTO(ferias, "Férias canceladas com sucesso"))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(mapOf("mensagem" to e.message))
        }
    }

    private fun mapearParaDTO(ferias: Ferias, msg: String? = null): Any {
        val dias = ChronoUnit.DAYS.between(ferias.dataInicio, ferias.dataFim) + 1

        val dto =
                FeriasRespostaDTO(
                        id = ferias.id!!,
                        funcionarioId = ferias.funcionario.id!!,
                        funcionarioNome = ferias.funcionario.nome,
                        dataInicio = ferias.dataInicio,
                        dataFim = ferias.dataFim,
                        dias = dias,
                        tipo = ferias.tipo,
                        status = ferias.status,
                        observacao = ferias.observacao,
                        observacaoGestor = ferias.observacaoGestor,
                        dataSolicitacao = ferias.dataSolicitacao,
                        dataAprovacao = ferias.dataAprovacao,
                        aprovadorNome = ferias.aprovadorNome
                )

        return if (msg != null) mapOf("mensagem" to msg, "dados" to dto) else dto
    }
}
