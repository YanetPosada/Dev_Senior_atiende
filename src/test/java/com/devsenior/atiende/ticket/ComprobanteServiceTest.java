package com.devsenior.atiende.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.devsenior.atiende.pedido.PedidoService;
import com.devsenior.atiende.ticket.dto.ComprobanteResponse.Resultado;
import com.devsenior.atiende.ticket.dto.TicketResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ComprobanteServiceTest {

    private final ComprobanteService comprobanteService =
            new ComprobanteService(null, new PedidoService(), null, null, null);

    @ParameterizedTest(name = "pedido {1} por {2} -> {3}")
    @CsvSource({
            "true, 1047, 64000, COINCIDE",
            "true, 1047, 46000, VALOR_DISTINTO",
            "true, 1045, 58000, PEDIDO_NO_ESPERA_PAGO",
            "true, 9999, 64000, PEDIDO_NO_ENCONTRADO",
            "false, , , NO_ES_COMPROBANTE"
    })
    void verificar_comparaElComprobanteConElPedido(boolean esComprobante, Long numeroPedido,
                                                   Long valor, Resultado esperado) {
        ComprobanteIA datos = new ComprobanteIA(esComprobante, "Banco Andino", valor,
                LocalDate.of(2026, 10, 16), "AND-20261016-5521", numeroPedido);

        assertEquals(esperado, comprobanteService.verificar(datos, ticketSinPedido()));
    }

    private TicketResponse ticketSinPedido() {
        return new TicketResponse(4L, "Mario Díaz", "mario.diaz@correo.com", Canal.WHATSAPP,
                "Ya hice la transferencia", EstadoTicket.ABIERTO, Prioridad.MEDIA,
                LocalDateTime.now(), LocalDateTime.now(), null, null, null, null, null, EstadoIA.PENDIENTE);
    }
}