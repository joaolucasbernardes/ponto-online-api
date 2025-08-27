package br.com.ponto.online.controller

import br.com.ponto.online.dto.EmpresaRequisicaoDTO
import br.com.ponto.online.dto.EmpresaRespostaDTO
import br.com.ponto.online.entidade.Empresa
import br.com.ponto.online.servico.EmpresaServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/empresas")
class EmpresaControle(private val empresaServico: EmpresaServico) {

    @PostMapping
    fun criar(@RequestBody requisicaoDTO: EmpresaRequisicaoDTO): ResponseEntity<Empresa> {
        val empresaSalva = empresaServico.criar(requisicaoDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaSalva)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<EmpresaRespostaDTO> {
        val empresa = empresaServico.buscarPorId(id)
        val respostaDTO = EmpresaRespostaDTO(empresa)
        return ResponseEntity.ok(respostaDTO)
    }
}