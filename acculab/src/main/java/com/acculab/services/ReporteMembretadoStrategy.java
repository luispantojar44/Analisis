package com.acculab.services;

import com.acculab.models.Orden;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

public class ReporteMembretadoStrategy implements ReportStrategy {
    @Override
    public File generarReporte(Orden orden, String destinationPath) throws Exception {
        File pdfFile = new File(destinationPath, "Reporte_Membretado_" + orden.getId() + ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        // Simulación del membrete
        document.add(new Paragraph("========================================="));
        document.add(new Paragraph("               ACCULAB                   "));
        document.add(new Paragraph("       LABORATORIO CLÍNICO INTEGRAL      "));
        document.add(new Paragraph("========================================="));
        document.add(new Paragraph("\n"));
        
        document.add(new Paragraph("Paciente: " + orden.getPaciente().toString()));
        document.add(new Paragraph("Médico Solicitante: " + orden.getMedicoSolicitante()));
        document.add(new Paragraph("\nResultados:"));
        
        orden.getResultados().forEach(r -> {
            try {
                String linea = r.getPrueba().getNombre() + ": " + r.getValor() + " " + r.getPrueba().getUnidad();
                if (r.isFueraDeRango()) {
                    linea += " (ALERTA: FUERA DE RANGO)";
                }
                document.add(new Paragraph(linea));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        document.close();
        return pdfFile;
    }
}
