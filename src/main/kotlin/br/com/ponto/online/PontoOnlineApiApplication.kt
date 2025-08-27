package br.com.ponto.online

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@EntityScan("br.com.ponto.online.entidade")
@SpringBootApplication
class PontoOnlineApiApplication

fun main(args: Array<String>) {
	runApplication<PontoOnlineApiApplication>(*args)
}
