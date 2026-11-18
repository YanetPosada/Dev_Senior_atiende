package com.devsenior.atiende.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.devsenior.atiende.ticket.dto.TicketResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class CalculadoraUrgenciaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 16);

    private final CalculadoraUrgencia calculadora = new CalculadoraUrgencia();

    @Test
    void calcular_casoDeLaura_sumaLasCuatroSenales() {
        TicketResponse laura = ticket(Prioridad.URGENTE, Sentimiento.MOLESTO, HOY.plusDays(1));

        CalculadoraUrgencia.Puntaje puntaje = calculadora.calcular(laura, 3, HOY);

        assertEquals(95, puntaje.valor());
        assertEquals(List.of("Prioridad URGENTE: +40", "Cliente molesto: +15",
                "Fecha límite en 1 día: +25", "Pedido con 3 días de retraso: +15"), puntaje.razones());
    }

    @Test
    void calcular_consultaSinSenales_soloSumaLaPrioridad() {
        TicketResponse andres = ticket(Prioridad.BAJA, Sentimiento.NEUTRAL, null);

        assertEquals(5, calculadora.calcular(andres, 0, HOY).valor());
    }

    @Test
    void calcular_fechaLimiteEnSeisDias_sumaDiez() {
        TicketResponse mario = ticket(Prioridad.URGENTE, Sentimiento.NEUTRAL, HOY.plusDays(6));

        assertEquals(50, calculadora.calcular(mario, 0, HOY).valor());
    }

    @Test
    void calcular_fechaLimiteLejana_noSuma() {
        TicketResponse ticket = ticket(Prioridad.MEDIA, Sentimiento.NEUTRAL, HOY.plusDays(20));

        assertEquals(15, calculadora.calcular(ticket, 0, HOY).valor());
    }

    @Test
    void calcular_fechaLimiteVencida_sumaVeinticinco() {
        TicketResponse ticket = ticket(Prioridad.ALTA, Sentimiento.MOLESTO, HOY.minusDays(1));

        CalculadoraUrgencia.Puntaje puntaje = calculadora.calcular(ticket, 0, HOY);

        assertEquals(70, puntaje.valor());
        assertEquals("Fecha límite vencida: +25", puntaje.razones().get(2));
    }

    @Test
    void calcular_retrasoLargo_tieneTopeDeVeinte() {
        TicketResponse ticket = ticket(Prioridad.ALTA, Sentimiento.MOLESTO, null);

        assertEquals(65, calculadora.calcular(ticket, 10, HOY).valor());
    }

    private TicketResponse ticket(Prioridad prioridad, Sentimiento sentimiento, LocalDate fechaLimite) {
        return new TicketResponse(1L, "Cliente", "contacto", Canal.WHATSAPP, "mensaje",
                EstadoTicket.ABIERTO, prioridad, LocalDateTime.now(), LocalDateTime.now(),
                Categoria.ENVIO, sentimiento, "resumen", null, fechaLimite, EstadoIA.CLASIFICADO);
    }
}