package com.devsenior.atiende.ticket;

public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }

    public TransicionInvalidaException(
            EstadoTicket estadoActual,
            EstadoTicket nuevoEstado) {

        super(
                "No se puede cambiar el estado de "
                        + estadoActual
                        + " a "
                        + nuevoEstado
        );
    }
}