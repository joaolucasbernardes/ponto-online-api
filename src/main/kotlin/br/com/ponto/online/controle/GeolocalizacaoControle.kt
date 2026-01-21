package br.com.ponto.online.controle

import br.com.ponto.online.dto.LocalPermitidoRespostaDTO
import br.com.ponto.online.dto.ResultadoValidacaoDTO
import br.com.ponto.online.dto.ValidacaoLocalizacaoDTO
import br.com.ponto.online.servico.GeolocalizacaoServico
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/geolocalizacao")
class GeolocalizacaoControle(
    private val geolocalizacaoServico: GeolocalizacaoServico
) {
    
    /**
     * Valida se coordenadas estão dentro de algum local permitido
     */
    @PostMapping("/validar")
    fun validarLocalizacao(
        @RequestBody dto: ValidacaoLocalizacaoDTO
    ): ResponseEntity<ResultadoValidacaoDTO> {
        val (localPermitido, distancia) = geolocalizacaoServico.validarLocalizacao(
            dto.latitude,
            dto.longitude
        )
        
        val resultado = if (localPermitido != null && distancia != null) {
            ResultadoValidacaoDTO(
                valido = true,
                localPermitido = LocalPermitidoRespostaDTO.deEntidade(localPermitido),
                distanciaMetros = distancia,
                mensagem = "Localização válida. Você está a ${geolocalizacaoServico.formatarDistancia(distancia)} de ${localPermitido.nome}."
            )
        } else {
            val mensagemDistancia = if (distancia != null) {
                " O local mais próximo está a ${geolocalizacaoServico.formatarDistancia(distancia)}."
            } else {
                ""
            }
            ResultadoValidacaoDTO(
                valido = false,
                localPermitido = null,
                distanciaMetros = distancia,
                mensagem = "Você está fora do raio permitido.$mensagemDistancia"
            )
        }
        
        return ResponseEntity.ok(resultado)
    }
    
    /**
     * Calcula distância entre dois pontos
     */
    @GetMapping("/distancia")
    fun calcularDistancia(
        @RequestParam lat1: Double,
        @RequestParam lon1: Double,
        @RequestParam lat2: Double,
        @RequestParam lon2: Double
    ): ResponseEntity<Map<String, Any>> {
        val distanciaMetros = geolocalizacaoServico.calcularDistancia(lat1, lon1, lat2, lon2)
        val distanciaFormatada = geolocalizacaoServico.formatarDistancia(distanciaMetros)
        
        return ResponseEntity.ok(mapOf(
            "distanciaMetros" to distanciaMetros,
            "distanciaFormatada" to distanciaFormatada
        ))
    }
}
