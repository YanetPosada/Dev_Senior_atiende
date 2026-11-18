
package com.devsenior.atiende.compartido;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int estado,
        String error,
        String mensaje,
        LocalDateTime fecha
) {
}
