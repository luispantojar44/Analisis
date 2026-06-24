package com.acculab.services;

import com.acculab.models.Orden;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

public class ReporteConvenioStrategy implements ReportStrategy {
    @Override
    public File generarReporte(Orden orden, String destinationPath) throws Exception {
        File pdfFile = new File(destinationPath, "Reporte_Convenio_" + orden.getId() + ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        document.add(new Paragraph("=== REPORTE INSTITUCIONAL / CONVENIO ==="));
        document.add(new Paragraph("Paciente: " + orden.getPaciente().toString()));
        document.add(new Paragraph("Referencia: Orden N° " + orden.getId()));
        document.add(new Paragraph("Confidencialidad: ALTA"));
        document.add(new Paragraph("\n"));
        
        // Estilo diferente para convenios
        orden.getResultados().forEach(r -> {
            try {
                document.add(new Paragraph(String.format("Prueba: %s | Resultado: %s %s", 
                        r.getPrueba().getNombre(), r.getValor(), r.getPrueba().getUnidad())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        document.close();
        return pdfFile;
    }
}
