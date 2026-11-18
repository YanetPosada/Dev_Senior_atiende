package com.devsenior.atiende.compartido;

import com.devsenior.atiende.ia.IaNoDisponibleException;
import com.devsenior.atiende.ia.RespuestaIAInvalidaException;
import com.devsenior.atiende.ticket.TransicionInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {

        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarTransicionInvalida(
            TransicionInvalidaException ex) {

        return construir(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IaNoDisponibleException.class)
    public ResponseEntity<ErrorRespuesta> manejarIaNoDisponible(
            IaNoDisponibleException ex) {

        return construir(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(RespuestaIAInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarRespuestaIAInvalida(
            RespuestaIAInvalidaException ex) {

        return construir(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    @ExceptionHandler(ArchivoNoValidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarArchivoNoValido(
            ArchivoNoValidoException ex) {

        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> manejarValidacion(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        return construir(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarGeneral(
            Exception ex) {

        return construir(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    private ResponseEntity<ErrorRespuesta> construir(
            HttpStatus status,
            String mensaje) {

        ErrorRespuesta cuerpo = new ErrorRespuesta(
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(cuerpo);
    }
}