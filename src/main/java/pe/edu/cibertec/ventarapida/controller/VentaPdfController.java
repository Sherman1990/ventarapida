package pe.edu.cibertec.ventarapida.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import pe.edu.cibertec.ventarapida.entity.DetalleVenta;
import pe.edu.cibertec.ventarapida.entity.Venta;
import pe.edu.cibertec.ventarapida.service.VentaService;

@Controller
public class VentaPdfController {

    @Autowired
    private VentaService ventaService;

    @GetMapping("/ventas/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) throws Exception {
        Venta venta = ventaService.buscarPorId(id);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document();
        PdfWriter.getInstance(documento, salida);
        documento.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font fuenteBlancaNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font fuenteTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

        Paragraph titulo = new Paragraph("VentaRápida - TechStore Perú S.A.C.", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        Paragraph subtitulo = new Paragraph("Comprobante de Venta", fuenteSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        documento.add(subtitulo);

        documento.add(new Paragraph("Venta N°: " + venta.getIdVenta(), fuenteNormal));
        documento.add(new Paragraph("Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fuenteNormal));
        documento.add(new Paragraph("Cliente: " + venta.getCliente().getNombreCompleto(), fuenteNormal));
        documento.add(new Paragraph("DNI: " + venta.getCliente().getDni(), fuenteNormal));
        documento.add(new Paragraph("Vendedor: " + venta.getUsuario().getNombre(), fuenteNormal));
        documento.add(new Paragraph(" "));

        // ===== TABLA DE PRODUCTOS =====
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{4, 1, 2, 2});

        agregarCeldaEncabezado(tabla, "Producto", fuenteBlancaNegrita);
        agregarCeldaEncabezado(tabla, "Cant.", fuenteBlancaNegrita);
        agregarCeldaEncabezado(tabla, "P. Unit.", fuenteBlancaNegrita);
        agregarCeldaEncabezado(tabla, "Subtotal", fuenteBlancaNegrita);

        for (DetalleVenta d : venta.getDetalles()) {
            tabla.addCell(new Phrase(d.getProducto().getNombre(), fuenteNormal));
            tabla.addCell(new Phrase(String.valueOf(d.getCantidad()), fuenteNormal));
            tabla.addCell(new Phrase("S/ " + d.getPrecioUnitario(), fuenteNormal));
            tabla.addCell(new Phrase("S/ " + d.getSubtotal(), fuenteNormal));
        }

        documento.add(tabla);
        documento.add(new Paragraph(" "));

        Paragraph total = new Paragraph("TOTAL: S/ " + venta.getTotal(), fuenteTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        documento.add(total);

        documento.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "inline" hace que se abra en el navegador (ideal para imprimir);
        // si prefieres que se descargue directo, cambia a "attachment"
        headers.setContentDispositionFormData("inline", "boleta-venta-" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(salida.toByteArray());
    }

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(new Color(30, 111, 232)); // mismo azul de tu diseño
        celda.setPadding(6);
        tabla.addCell(celda);
    }
}