package com.devsenior.atiende.compartido;

import com.devsenior.atiende.ia.IaNoDisponibleException;
import com.devsenior.atiende.ia.RespuestaIAInvalidaException;
import com.devsenior.atiende.ticket.TransicionInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> manejarValidacion(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mensajeDeCampo)
                .collect(Collectors.joining("; "));

        return error(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> manejarCuerpoIlegible(
            HttpMessageNotReadableException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición es inválido. Revisa campos como canal o estado."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> manejarTipoInvalido(
            MethodArgumentTypeMismatchException ex) {

        String campo = ex.getName();

        return error(
                HttpStatus.BAD_REQUEST,
                campo + " tiene un valor inválido"
        );
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {

        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarTransicion(
            TransicionInvalidaException ex) {

        return error(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(RespuestaIAInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarRespuestaIaInvalida(
            RespuestaIAInvalidaException ex) {

        return error(
                HttpStatus.BAD_GATEWAY,
                ex.getMessage()
        );
    }

    @ExceptionHandler(IaNoDisponibleException.class)
    public ResponseEntity<ErrorRespuesta> manejarIaNoDisponible(
            IaNoDisponibleException ex) {

        return error(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarGeneral(
            Exception ex) {

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
    }

    private String mensajeDeCampo(FieldError error) {

        if (error.getDefaultMessage() != null) {
            return error.getDefaultMessage();
        }

        return error.getField() + " es inválido";
    }

    private ResponseEntity<ErrorRespuesta> error(
            HttpStatus estado,
            String mensaje) {

        ErrorRespuesta cuerpo = new ErrorRespuesta(
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(estado)
                .body(cuerpo);
    }
}