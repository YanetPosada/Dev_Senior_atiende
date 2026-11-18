package com.devsenior.atiende.ticket.dto;

import com.devsenior.atiende.ticket.Canal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketRequest(
        @NotBlank(message = "clienteNombre es obligatorio")
        @Size(max = 100, message = "clienteNombre no puede superar 100 caracteres")
        String clienteNombre,

        @NotBlank(message = "clienteContacto es obligatorio")
        @Size(max = 120, message = "clienteContacto no puede superar 120 caracteres")
        String clienteContacto,

        @NotNull(message = "canal es obligatorio")
        Canal canal,

        @NotBlank(message = "mensaje es obligatorio")
        @Size(max = 2000, message = "mensaje no puede superar 2000 caracteres")
        String mensaje
) {
}
