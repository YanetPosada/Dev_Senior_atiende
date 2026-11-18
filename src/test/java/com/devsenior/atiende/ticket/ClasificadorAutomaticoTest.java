package com.devsenior.atiende.automatico;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devsenior.atiende.automatizacion.ClasificadorAutomatico;
import com.devsenior.atiende.ia.IaNoDisponibleException;
import com.devsenior.atiende.ia.RespuestaIAInvalidaException;
import com.devsenior.atiende.ticket.ClasificacionService;
import com.devsenior.atiende.ticket.TicketService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasificadorAutomaticoTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private ClasificacionService clasificacionService;

    private ClasificadorAutomatico clasificador;

    @BeforeEach
    void setUp() {
        clasificador = new ClasificadorAutomatico(ticketService, clasificacionService, 5, 3);
    }

    @Test
    void clasificarPendientes_sinPendientes_noLlamaALaIA() {
        when(ticketService.pendientesDeClasificar(5)).thenReturn(List.of());

        clasificador.clasificarPendientes();

        verify(clasificacionService, never()).clasificar(any());
    }

    @Test
    void clasificarPendientes_respuestaInvalida_registraElFalloYSigue() {
        when(ticketService.pendientesDeClasificar(5)).thenReturn(List.of(1L, 2L));
        when(clasificacionService.clasificar(1L))
                .thenThrow(new RespuestaIAInvalidaException("openai", "texto sin JSON"));

        clasificador.clasificarPendientes();

        verify(ticketService).registrarFalloIA(1L, 3);
        verify(clasificacionService).clasificar(2L);
    }

    @Test
    void clasificarPendientes_iaNoDisponible_detieneLaVueltaSinGastarIntentos() {
        when(ticketService.pendientesDeClasificar(5)).thenReturn(List.of(1L, 2L));
        when(clasificacionService.clasificar(1L)).thenThrow(new IaNoDisponibleException());

        clasificador.clasificarPendientes();

        verify(clasificacionService, never()).clasificar(2L);
        verify(ticketService, never()).registrarFalloIA(anyLong(), anyInt());
    }
}