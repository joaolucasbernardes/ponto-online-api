package br.com.ponto.online.controle

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ErroResposta(
    val mensagem: String,
    val status: Int
)

@ControllerAdvice
class TratadorDeExcecoes {

    @ExceptionHandler(IllegalStateException::class)
    fun tratarIllegalStateException(ex: IllegalStateException): ResponseEntity<ErroResposta> {
        val erro = ErroResposta(
            mensagem = ex.message ?: "Erro de validação",
            status = HttpStatus.FORBIDDEN.value()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun tratarNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ErroResposta> {
        val erro = ErroResposta(
            mensagem = ex.message ?: "Recurso não encontrado",
            status = HttpStatus.NOT_FOUND.value()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun tratarIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErroResposta> {
        val erro = ErroResposta(
            mensagem = ex.message ?: "Argumento inválido",
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro)
    }

    @ExceptionHandler(Exception::class)
    fun tratarExcecaoGenerica(ex: Exception): ResponseEntity<ErroResposta> {
        val erro = ErroResposta(
            mensagem = "Erro interno do servidor",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro)
    }
}
