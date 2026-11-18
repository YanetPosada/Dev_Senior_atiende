package com.devsenior.atiende.reporte.dto;

import com.devsenior.atiende.ia.dto.FacturaIA;
import java.time.LocalDate;

public record ReporteSemanalResponse(
        LocalDate desde,
        LocalDate hasta,
        CifrasSemana cifras,
        String resumenEjecutivo,
        FacturaIA factura
) {
}