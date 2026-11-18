package com.devsenior.atiende.reporte;

import com.devsenior.atiende.ia.AsistenteIA;
import com.devsenior.atiende.ia.BibliotecaPrompts;
import com.devsenior.atiende.ia.RespuestaIA;
import com.devsenior.atiende.ia.RespuestaIAInvalidaException;
import com.devsenior.atiende.ia.dto.FacturaIA;
import com.devsenior.atiende.reporte.dto.CifrasSemana;
import com.devsenior.atiende.reporte.dto.ReporteSemanalResponse;
import com.devsenior.atiende.ticket.Canal;
import com.devsenior.atiende.ticket.EstadoIA;
import com.devsenior.atiende.ticket.TicketService;
import com.devsenior.atiende.ticket.dto.TicketResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReporteService {

    private static final String SIN_CLASIFICAR = "SIN_CLASIFICAR";

    private final TicketService ticketService;
    private final AsistenteIA asistenteIA;
    private final BibliotecaPrompts bibliotecaPrompts;

    public ReporteService(TicketService ticketService, AsistenteIA asistenteIA,
                          BibliotecaPrompts bibliotecaPrompts) {
        this.ticketService = ticketService;
        this.asistenteIA = asistenteIA;
        this.bibliotecaPrompts = bibliotecaPrompts;
    }

    public ReporteSemanalResponse generarSemanal() {
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(6);
        List<TicketResponse> tickets = ticketService.creadosDesde(desde.atStartOfDay());

        CifrasSemana cifras = calcularCifras(tickets);
        String instrucciones = bibliotecaPrompts.cargar("reporte-semanal");
        RespuestaIA respuesta = asistenteIA.completar(instrucciones, cifras.comoTexto());
        if (respuesta.texto().isBlank()) {
            throw new RespuestaIAInvalidaException(respuesta.proveedor(), respuesta.texto());
        }
        return new ReporteSemanalResponse(desde, hasta, cifras, respuesta.texto(), FacturaIA.de(respuesta));
    }

    CifrasSemana calcularCifras(List<TicketResponse> tickets) {
        long escalados = tickets.stream().filter(ticket -> ticket.canal() == Canal.CHAT_WEB).count();
        long sinClasificar = tickets.stream().filter(ticket -> ticket.estadoIA() != EstadoIA.CLASIFICADO).count();
        return new CifrasSemana(
                tickets.size(),
                contar(tickets, TicketResponse::categoria),
                contar(tickets, TicketResponse::prioridad),
                contar(tickets, TicketResponse::sentimiento),
                escalados,
                sinClasificar);
    }

    private Map<String, Long> contar(List<TicketResponse> tickets, Function<TicketResponse, Enum<?>> campo) {
        return tickets.stream()
                .collect(Collectors.groupingBy(
                        ticket -> campo.apply(ticket) == null ? SIN_CLASIFICAR : campo.apply(ticket).name(),
                        TreeMap::new,
                        Collectors.counting()));
    }
}