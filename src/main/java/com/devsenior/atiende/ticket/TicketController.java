package com.devsenior.atiende.ticket;

import java.util.List;

import com.devsenior.atiende.ticket.dto.TicketResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devsenior.atiende.ticket.dto.CambioEstadoRequest;
import com.devsenior.atiende.ticket.dto.TicketRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketServicio;

    public TicketController(TicketService ticketServicio) {
        this.ticketServicio = ticketServicio;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> crear(
            @Valid @RequestBody TicketRequest solicitud) {

        TicketResponse creado = ticketServicio.crear(solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> listar(
            @RequestParam(required = false) EstadoTicket estado) {

        return ResponseEntity.ok(
                ticketServicio.listar(estado)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ticketServicio.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequest solicitud) {

        return ResponseEntity.ok(
                ticketServicio.cambiarEstado(
                        id,
                        solicitud.estado()
                )
        );
    }
}