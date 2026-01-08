package br.com.ponto.online.controle

import br.com.ponto.online.dto.*
import br.com.ponto.online.servico.DashboardServico
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/dashboard")
class DashboardControle(
    private val dashboardServico: DashboardServico
) {

    @GetMapping("/estatisticas")
    fun obterEstatisticas(): ResponseEntity<DashboardEstatisticasDTO> {
        val estatisticas = dashboardServico.obterEstatisticas()
        return ResponseEntity.ok(estatisticas)
    }

    @GetMapping("/grafico-presenca")
    fun obterGraficoPresenca(
        @RequestParam(defaultValue = "7") dias: Int
    ): ResponseEntity<GraficoPresencaDTO> {
        val grafico = dashboardServico.obterGraficoPresenca(dias)
        return ResponseEntity.ok(grafico)
    }

    @GetMapping("/funcionarios-ativos")
    fun listarFuncionariosAtivos(): ResponseEntity<List<FuncionarioListagemDTO>> {
        val funcionarios = dashboardServico.listarFuncionariosAtivos()
        return ResponseEntity.ok(funcionarios)
    }

    @GetMapping("/inconsistencias")
    fun detectarInconsistencias(): ResponseEntity<List<InconsistenciaDTO>> {
        val inconsistencias = dashboardServico.detectarInconsistencias()
        return ResponseEntity.ok(inconsistencias)
    }
}
