package br.com.ponto.online.servico

import br.com.ponto.online.dto.CartaoPontoDTO
import br.com.ponto.online.dto.ItemPontoDTO
import br.com.ponto.online.enums.StatusFerias
import br.com.ponto.online.repositorio.FeriasRepositorio
import br.com.ponto.online.repositorio.FuncionarioRepositorio
import br.com.ponto.online.repositorio.RegistroPontoRepositorio
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class RelatorioServico(
        private val funcionarioRepositorio: FuncionarioRepositorio,
        private val registroPontoRepositorio: RegistroPontoRepositorio,
        private val feriasRepositorio: FeriasRepositorio,
        private val templateEngine: TemplateEngine
) {

    fun gerarCartaoPontoPdf(funcionarioId: Long, ano: Int, mes: Int): ByteArray {
        val dto = montarDadosCartaoPonto(funcionarioId, ano, mes)
        val html = gerarHtml(dto)
        return gerarPdfDoHtml(html)
    }

    private fun montarDadosCartaoPonto(funcionarioId: Long, ano: Int, mes: Int): CartaoPontoDTO {
        val funcionario =
                funcionarioRepositorio.findById(funcionarioId).orElseThrow {
                    RuntimeException("Funcionário não encontrado")
                }

        val empresa = funcionario.empresa
        val yearMonth = YearMonth.of(ano, mes)
        val dataInicio = yearMonth.atDay(1)
        val dataFim = yearMonth.atEndOfMonth()

        // Buscar registros (00:00 dia 1 até 23:59 dia ultimo)
        val inicioDateTime = dataInicio.atStartOfDay()
        val fimDateTime = dataFim.atTime(23, 59, 59)

        val registros =
                registroPontoRepositorio.findByFuncionarioIdAndDataHoraBetweenOrderByDataHoraAsc(
                        funcionarioId,
                        inicioDateTime,
                        fimDateTime
                )

        // Buscar ferias que interceptam o periodo (simplificado: usar query de conflito)
        // Buscar ferias que interceptam o periodo (simplificado: usar query de conflito)
        val feriasList =
                feriasRepositorio.findFeriasConflitantes(
                        funcionarioId,
                        dataInicio,
                        dataFim,
                        listOf(StatusFerias.REJEITADA, StatusFerias.CANCELADA)
                )

        val itens = mutableListOf<ItemPontoDTO>()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        var dataAtual = dataInicio
        val localeBr = Locale("pt", "BR")

        // Definição do Horário Esperado (Código da Escala ou Horários Contratuais)
        // Definição do Horário Esperado (Código da Escala ou Horários Contratuais)
        val horarioEsperado =
                funcionario.escala?.let { esc ->
                    // Tenta usar o nome (que parece ser o código "0013") + horários
                    "${esc.nome} ${esc.horaEntrada.format(timeFormatter)} ${esc.horaSaida.format(timeFormatter)}"
                }
                        ?: "" // Sem escala definida

        while (!dataAtual.isAfter(dataFim)) {
            val diaSemana =
                    dataAtual.dayOfWeek.getDisplayName(TextStyle.SHORT, localeBr).uppercase()

            // Verificar Férias
            var marcacoesTexto = ""
            val emFerias =
                    feriasList.any {
                        !dataAtual.isBefore(it.dataInicio) && !dataAtual.isAfter(it.dataFim)
                    }

            if (emFerias) {
                marcacoesTexto = "Férias"
            } else {
                // Verificar registros do dia
                val registrosDoDia = registros.filter { it.dataHora.toLocalDate() == dataAtual }
                if (registrosDoDia.isNotEmpty()) {
                    val horas =
                            registrosDoDia.joinToString(" ") { it.dataHora.format(timeFormatter) }
                    marcacoesTexto = "$horas Trabalhando"
                } else {
                    // Verificar fim de semana (Sab/Dom)
                    val isFimDeSemana = dataAtual.dayOfWeek.value >= 6 // 6=Sábado, 7=Domingo
                    if (isFimDeSemana) {
                        marcacoesTexto = if (dataAtual.dayOfWeek.value == 6) "Folga" else "Dsr"
                    } else {
                        // Dia útil sem marcação
                        marcacoesTexto = "Falta"
                    }
                }
            }

            itens.add(
                    ItemPontoDTO(
                            data = dataAtual,
                            diaSemana = diaSemana,
                            horariosEsperados = horarioEsperado,
                            marcacoes = marcacoesTexto,
                            observacao = ""
                    )
            )

            dataAtual = dataAtual.plusDays(1)
        }

        return CartaoPontoDTO(
                empresaNome = empresa.razaoSocial,
                empresaCnpj = empresa.cnpj,
                empresaEndereco = "Endereço da Empresa (Não cadastrado)",
                funcionarioNome = funcionario.nome,
                funcionarioCargo =
                        "N/A", // Não tenho cargo na entidade Funcionario, mock ou adicionar
                funcionarioCpf = funcionario.cpf,
                periodoInicio = dataInicio,
                periodoFim = dataFim,
                dataEmissao = LocalDate.now(),
                itens = itens
        )
    }

    private fun gerarHtml(dto: CartaoPontoDTO): String {
        val context = Context()
        context.setVariable("cartao", dto)
        return templateEngine.process("cartao_ponto", context)
    }

    private fun gerarPdfDoHtml(html: String): ByteArray {
        val os = ByteArrayOutputStream()
        val builder = PdfRendererBuilder()
        builder.useFastMode()
        builder.withHtmlContent(html, null)
        builder.toStream(os)
        builder.run()
        return os.toByteArray()
    }
}
