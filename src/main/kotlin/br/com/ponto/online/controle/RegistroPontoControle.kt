package br.com.ponto.online.controle

import br.com.ponto.online.dto.RegistroPontoRequisicaoDTO
import br.com.ponto.online.dto.RegistroPontoRespostaDTO
import br.com.ponto.online.servico.RegistroPontoServico
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}