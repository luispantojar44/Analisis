package com.acculab.services;

import com.acculab.models.Orden;
import java.io.File;

public interface ReportStrategy {
    File generarReporte(Orden orden, String destinationPath) throws Exception;
}
