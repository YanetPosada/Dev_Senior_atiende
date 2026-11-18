package com.devsenior.atiende.reporte;

import com.devsenior.atiende.reporte.dto.ReporteSemanalResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping("/semanal")
    public ReporteSemanalResponse generarSemanal() {
        return reporteService.generarSemanal();
    }
}