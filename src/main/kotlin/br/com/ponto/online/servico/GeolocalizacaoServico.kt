package br.com.ponto.online.servico

import br.com.ponto.online.entidade.LocalPermitido
import br.com.ponto.online.repositorio.LocalPermitidoRepositorio
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class GeolocalizacaoServico(
    private val localPermitidoRepositorio: LocalPermitidoRepositorio
) {
    
    /**
     * Calcula a distância entre dois pontos geográficos usando a fórmula de Haversine
     * @param lat1 Latitude do ponto 1 em graus
     * @param lon1 Longitude do ponto 1 em graus
     * @param lat2 Latitude do ponto 2 em graus
     * @param lon2 Longitude do ponto 2 em graus
     * @return Distância em metros
     */
    fun calcularDistancia(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371000.0 // Raio da Terra em metros
        
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return R * c
    }
    
    /**
     * Valida se as coordenadas estão dentro de algum local permitido
     * @param lat Latitude
     * @param lon Longitude
     * @return Par contendo o local permitido mais próximo (se dentro do raio) e a distância
     */
    fun validarLocalizacao(lat: Double, lon: Double): Pair<LocalPermitido?, Double?> {
        val locaisAtivos = localPermitidoRepositorio.findByAtivoTrue()
        
        if (locaisAtivos.isEmpty()) {
            return Pair(null, null)
        }
        
        val locaisComDistancia = locaisAtivos.map { local ->
            val distancia = calcularDistancia(lat, lon, local.latitude, local.longitude)
            Triple(local, distancia, distancia <= local.raioMetros)
        }
        
        // Buscar o local mais próximo que está dentro do raio
        val localValido = locaisComDistancia
            .filter { (_, _, dentroDoRaio) -> dentroDoRaio }
            .minByOrNull { (_, distancia, _) -> distancia }
        
        return if (localValido != null) {
            Pair(localValido.first, localValido.second)
        } else {
            // Se nenhum está dentro do raio, retornar o mais próximo para referência
            val maisProximo = locaisComDistancia.minByOrNull { (_, distancia, _) -> distancia }
            Pair(null, maisProximo?.second)
        }
    }
    
    /**
     * Verifica se está dentro do raio de um local específico
     * @param lat Latitude
     * @param lon Longitude
     * @param local Local a verificar
     * @return true se está dentro do raio, false caso contrário
     */
    fun estaDentroDoRaio(lat: Double, lon: Double, local: LocalPermitido): Boolean {
        val distancia = calcularDistancia(lat, lon, local.latitude, local.longitude)
        return distancia <= local.raioMetros
    }
    
    /**
     * Busca o local permitido mais próximo das coordenadas fornecidas
     * @param lat Latitude
     * @param lon Longitude
     * @return Par contendo o local mais próximo e a distância
     */
    fun buscarLocalMaisProximo(lat: Double, lon: Double): Pair<LocalPermitido?, Double?> {
        val locaisAtivos = localPermitidoRepositorio.findByAtivoTrue()
        
        if (locaisAtivos.isEmpty()) {
            return Pair(null, null)
        }
        
        val maisProximo = locaisAtivos
            .map { local ->
                val distancia = calcularDistancia(lat, lon, local.latitude, local.longitude)
                Pair(local, distancia)
            }
            .minByOrNull { (_, distancia) -> distancia }
        
        return maisProximo ?: Pair(null, null)
    }
    
    /**
     * Formata distância para exibição amigável
     * @param metros Distância em metros
     * @return String formatada (ex: "150m" ou "1.5km")
     */
    fun formatarDistancia(metros: Double): String {
        return if (metros < 1000) {
            "${metros.toInt()}m"
        } else {
            val km = metros / 1000
            "%.1fkm".format(km)
        }
    }
}
