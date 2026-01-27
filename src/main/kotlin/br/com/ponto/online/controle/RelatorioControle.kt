package br.com.ponto.online.controle

import br.com.ponto.online.servico.RelatorioServico
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios e documentos")
class RelatorioControle(private val relatorioServico: RelatorioServico) {

        @Operation(
                summary = "Exportar Cartão Ponto (PDF)",
                description =
                        "Gera o espelho de ponto em formato PDF para o funcionário e período informados."
        )
        @ApiResponses(
                value =
                        [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "PDF gerado com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "404",
                                        description = "Funcionário não encontrado"
                                )]
        )
        @GetMapping("/cartao-ponto/{funcionarioId}")
        fun baixarCartaoPonto(
                @Parameter(description = "ID do funcionário") @PathVariable funcionarioId: Long,
                @Parameter(description = "Mês (1-12)") @RequestParam(required = false) mes: Int?,
                @Parameter(description = "Ano (Ex: 2026)") @RequestParam(required = false) ano: Int?
        ): ResponseEntity<ByteArray> {
                val dataBase = LocalDate.now()
                val mesFinal = mes ?: dataBase.monthValue
                val anoFinal = ano ?: dataBase.year

                try {
                        val pdfBytes =
                                relatorioServico.gerarCartaoPontoPdf(
                                        funcionarioId,
                                        anoFinal,
                                        mesFinal
                                )

                        val headers = HttpHeaders()
                        headers.contentType = MediaType.APPLICATION_PDF
                        headers.setContentDispositionFormData(
                                "attachment",
                                "cartao_ponto_${funcionarioId}_${mesFinal}_${anoFinal}.pdf"
                        )
                        headers.cacheControl = "must-revalidate, post-check=0, pre-check=0"

                        return ResponseEntity(
                                pdfBytes,
                                headers,
                                org.springframework.http.HttpStatus.OK
                        )
                } catch (e: Exception) {
                        e.printStackTrace()
                        return ResponseEntity.internalServerError()
                                .body("Erro ao gerar PDF: ${e.message}".toByteArray())
                }
        }
}
