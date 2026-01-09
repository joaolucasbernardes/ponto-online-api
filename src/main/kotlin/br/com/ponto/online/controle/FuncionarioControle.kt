package br.com.ponto.online.controle

import br.com.ponto.online.dto.FuncionarioCadastroDTO
import br.com.ponto.online.dto.FuncionarioDetalheDTO
import br.com.ponto.online.dto.FuncionarioEdicaoDTO
import br.com.ponto.online.dto.FuncionarioListagemDTO
import br.com.ponto.online.servico.FuncionarioServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/funcionarios")
@PreAuthorize("hasRole('ADMIN')")
class FuncionarioControle(
    private val funcionarioServico: FuncionarioServico
) {

    @GetMapping
    fun listarTodos(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) empresaId: Long?,
        @RequestParam(required = false) role: String?,
        @RequestParam(required = false) ativo: Boolean?
    ): ResponseEntity<List<FuncionarioListagemDTO>> {
        val funcionarios = if (nome != null || empresaId != null || role != null || ativo != null) {
            funcionarioServico.buscarComFiltros(nome, empresaId, role, ativo)
        } else {
            funcionarioServico.listarTodos()
        }
        return ResponseEntity.ok(funcionarios)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<FuncionarioDetalheDTO> {
        val funcionario = funcionarioServico.buscarPorId(id)
        return ResponseEntity.ok(funcionario)
    }

    @PostMapping
    fun criar(@RequestBody dto: FuncionarioCadastroDTO): ResponseEntity<FuncionarioDetalheDTO> {
        val funcionario = funcionarioServico.criar(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario)
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @RequestBody dto: FuncionarioEdicaoDTO
    ): ResponseEntity<FuncionarioDetalheDTO> {
        val funcionario = funcionarioServico.atualizar(id, dto)
        return ResponseEntity.ok(funcionario)
    }

    @DeleteMapping("/{id}")
    fun desativar(@PathVariable id: Long): ResponseEntity<Void> {
        funcionarioServico.desativar(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/ativar")
    fun ativar(@PathVariable id: Long): ResponseEntity<Void> {
        funcionarioServico.ativar(id)
        return ResponseEntity.noContent().build()
    }
}
