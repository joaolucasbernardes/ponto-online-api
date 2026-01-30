package br.com.ponto.online.controle

import br.com.ponto.online.dto.RefreshTokenRequisicaoDTO
import br.com.ponto.online.dto.UsuarioLogadoDTO
import br.com.ponto.online.entidade.Admin
import br.com.ponto.online.entidade.Funcionario
import br.com.ponto.online.servico.AutenticacaoServico
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Usuário", description = "Operações relacionadas ao usuário autenticado")
class UsuarioControle(private val autenticacaoServico: AutenticacaoServico) {

        @Operation(
                summary = "Obter dados do usuário logado",
                description = "Retorna os dados do usuário autenticado a partir do token JWT"
        )
        @ApiResponses(
                value =
                        [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "Dados do usuário retornados com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "401",
                                        description = "Usuário não autenticado"
                                )]
        )
        @GetMapping("/api/me")
        fun obterUsuarioLogado(
                @AuthenticationPrincipal userDetails: UserDetails
        ): ResponseEntity<UsuarioLogadoDTO> {
                val dto =
                        when (userDetails) {
                                is Admin ->
                                        UsuarioLogadoDTO(
                                                id = userDetails.id!!,
                                                nome = userDetails.nome,
                                                email = userDetails.email,
                                                role = "ADMIN",
                                                empresaId = null, // Admin não tem empresa associada
                                                empresaNome = null
                                        )
                                is Funcionario ->
                                        UsuarioLogadoDTO(
                                                id = userDetails.id!!,
                                                nome = userDetails.nome,
                                                email = userDetails.email,
                                                role = "FUNCIONARIO",
                                                empresaId = userDetails.empresa.id,
                                                empresaNome = userDetails.empresa.razaoSocial
                                        )
                                else -> throw IllegalStateException("Tipo de usuário desconhecido")
                        }
                return ResponseEntity.ok(dto)
        }

        @Operation(
                summary = "Renovar tokens",
                description =
                        "Gera novos tokens de acesso e refresh a partir de um refresh token válido"
        )
        @ApiResponses(
                value =
                        [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "Tokens renovados com sucesso"
                                ),
                                ApiResponse(
                                        responseCode = "400",
                                        description = "Refresh token inválido ou expirado"
                                )]
        )
        @PostMapping("/api/auth/refresh")
        fun refreshToken(@RequestBody requisicao: RefreshTokenRequisicaoDTO): ResponseEntity<Any> {
                return try {
                        val resposta = autenticacaoServico.refresh(requisicao.refreshToken)
                        ResponseEntity.ok(resposta)
                } catch (e: Exception) {
                        ResponseEntity.badRequest()
                                .body(mapOf("mensagem" to (e.message ?: "Erro ao renovar token")))
                }
        }
}
