package br.com.ponto.online.enumeracao

enum class TipoEscala {
    FIXA,        // Horário fixo todos os dias
    FLEXIVEL,    // Horário flexível com carga horária
    ROTATIVA,    // Alterna entre turnos
    ESCALA_12X36 // 12 horas trabalho, 36 descanso
}
