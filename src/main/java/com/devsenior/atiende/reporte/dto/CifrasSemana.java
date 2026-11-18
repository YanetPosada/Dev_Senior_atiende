package com.devsenior.atiende.reporte.dto;

import java.util.Map;

public record CifrasSemana(
        int totalTickets,
        Map<String, Long> porCategoria,
        Map<String, Long> porPrioridad,
        Map<String, Long> porSentimiento,
        long escaladosPorChatbot,
        long sinClasificar
) {

    public String comoTexto() {
        return "- Tickets recibidos: " + totalTickets + "\n"
                + "- Por categoría: " + porCategoria + "\n"
                + "- Por prioridad: " + porPrioridad + "\n"
                + "- Por sentimiento: " + porSentimiento + "\n"
                + "- Escalados por el chatbot: " + escaladosPorChatbot + "\n"
                + "- Sin clasificar o con fallo de IA: " + sinClasificar;
    }
}