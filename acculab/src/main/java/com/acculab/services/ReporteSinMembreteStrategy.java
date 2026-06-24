package com.acculab.services;

import com.acculab.models.Orden;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

public class ReporteSinMembreteStrategy implements ReportStrategy {
    @Override
    public File generarReporte(Orden orden, String destinationPath) throws Exception {
        File pdfFile = new File(destinationPath, "Reporte_SinMembrete_" + orden.getId() + ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        // Sin membrete, impresión directa para hoja membretada física
        document.add(new Paragraph("\n\n\n\n\n")); // Espacio para el membrete físico
        
        document.add(new Paragraph("Paciente: " + orden.getPaciente().toString()));
        document.add(new Paragraph("Médico Solicitante: " + orden.getMedicoSolicitante()));
        document.add(new Paragraph("\nResultados:"));
        
        orden.getResultados().forEach(r -> {
            try {
                String linea = r.getPrueba().getNombre() + ": " + r.getValor() + " " + r.getPrueba().getUnidad();
                if (r.isFueraDeRango()) {
                    linea += " (*)"; // Menos intrusivo para sin membrete
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
