package com.acculab;

import com.acculab.dao.*;
import com.acculab.models.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;
import java.time.LocalDate;

public class DataSeeder {
    public static void main(String[] args) {
        System.out.println("Iniciando generación de datos falsos...");
        
        try {
            // 1. Usuarios
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (usuarioDAO.findByUsername("recepcion") == null) {
                Usuario recep = new Usuario("US-REC-1", "Ana Recepcionista", "recepcion", BCrypt.hashpw("recepcion123", BCrypt.gensalt()), Usuario.Rol.RECEPCIONISTA);
                usuarioDAO.save(recep);
            }
            if (usuarioDAO.findByUsername("laboratorio") == null) {
                Usuario lab = new Usuario("US-LAB-1", "Luis Laboratorista", "laboratorio", BCrypt.hashpw("laboratorio123", BCrypt.gensalt()), Usuario.Rol.LABORATORISTA);
                usuarioDAO.save(lab);
            }
            
            // 2. Medicos
            MedicoDAO medicoDAO = new MedicoDAO();
            if (medicoDAO.findAll().isEmpty()) {
                medicoDAO.save(new Medico("MED-1", "Dr. Carlos", "Mendoza Ríos", "0999999991", "carlos.mendoza@hospital.com", "Cardiología"));
                medicoDAO.save(new Medico("MED-2", "Dra. María", "Pérez López", "0999999992", "maria.perez@hospital.com", "Medicina General"));
                medicoDAO.save(new Medico("MED-3", "Dr. Fernando", "Gómez", "0999999993", "fer.gomez@clinica.com", "Endocrinología"));
            }
            
            // 3. Pacientes
            PacienteDAO pacienteDAO = new PacienteDAO();
            if (pacienteDAO.findAll().isEmpty()) {
                pacienteDAO.save(new Paciente("PAC-1", "Juan Andrés", "Andrade Gómez", LocalDate.of(1985, 5, 20), Paciente.Sexo.MASCULINO, "0991112223", "juan@mail.com"));
                pacienteDAO.save(new Paciente("PAC-2", "Lucía", "Zumba Pérez", LocalDate.of(1990, 8, 15), Paciente.Sexo.FEMENINO, "0991112224", "lucia@mail.com"));
                pacienteDAO.save(new Paciente("PAC-3", "Pedro", "Alvarado", LocalDate.of(1955, 2, 10), Paciente.Sexo.MASCULINO, "0991112225", "pedro@mail.com"));
                pacienteDAO.save(new Paciente("PAC-4", "Sofía", "Castillo", LocalDate.of(2002, 12, 1), Paciente.Sexo.FEMENINO, "0991112226", "sofia@mail.com"));
            }
            
            // 4. Pruebas y Perfiles (Probablemente ya autogenerados, pero nos aseguramos que existan)
            PruebaDAO pruebaDAO = new PruebaDAO();
            if (pruebaDAO.findAll().isEmpty()) {
                pruebaDAO.save(new Prueba("PR-01", "Glucosa", "mg/dL", 70.0, 100.0, 70.0, 100.0, 15.00));
                pruebaDAO.save(new Prueba("PR-02", "Creatinina", "mg/dL", 0.7, 1.3, 0.6, 1.1, 12.50));
                pruebaDAO.save(new Prueba("PR-03", "Colesterol Total", "mg/dL", 0.0, 200.0, 0.0, 200.0, 18.00));
                pruebaDAO.save(new Prueba("PR-04", "Triglicéridos", "mg/dL", 0.0, 150.0, 0.0, 150.0, 20.00));
                pruebaDAO.save(new Prueba("PR-05", "Ácido Úrico", "mg/dL", 3.4, 7.0, 2.4, 6.0, 10.00));
            }
            
            PerfilDAO perfilDAO = new PerfilDAO();
            if (perfilDAO.findAll().isEmpty()) {
                Perfil p1 = new Perfil("PF-01", "Perfil Lipídico");
                p1.addPrueba(pruebaDAO.findById("PR-03"));
                p1.addPrueba(pruebaDAO.findById("PR-04"));
                perfilDAO.save(p1);
            }
            
            // 5. Órdenes y Abonos
            OrdenDAO ordenDAO = new OrdenDAO();
            AbonoDAO abonoDAO = new AbonoDAO();
            if (ordenDAO.findAll().isEmpty()) {
                // Orden 1 (Completada, sin deuda)
                Orden o1 = new Orden("ORD-000001", pacienteDAO.findById("PAC-1"), medicoDAO.findById("MED-1").toString());
                Prueba glucosa = pruebaDAO.findById("PR-01");
                Prueba creatinina = pruebaDAO.findById("PR-02");
                o1.addPrueba(glucosa);
                o1.addPrueba(creatinina);
                o1.setCostoTotal(glucosa.getPrecio() + creatinina.getPrecio());
                
                // Abono
                Abono ab1 = new Abono("AB-001", o1.getId(), o1.getCostoTotal());
                o1.agregarAbono(ab1);
                abonoDAO.save(ab1);
                
                // Resultados
                Resultado r1 = new Resultado(glucosa, 85.0); r1.validarRango(o1.getPaciente());
                Resultado r2 = new Resultado(creatinina, 0.9); r2.validarRango(o1.getPaciente());
                o1.agregarResultado(r1);
                o1.agregarResultado(r2);
                o1.setEstado(EstadoOrden.FINALIZADA);
                ordenDAO.save(o1);
                
                // Orden 2 (Ingresada, con saldo pendiente)
                Orden o2 = new Orden("ORD-000002", pacienteDAO.findById("PAC-2"), medicoDAO.findById("MED-2").toString());
                Prueba col = pruebaDAO.findById("PR-03");
                Prueba trig = pruebaDAO.findById("PR-04");
                o2.addPrueba(col);
                o2.addPrueba(trig);
                o2.setCostoTotal(col.getPrecio() + trig.getPrecio());
                
                // Abono parcial
                Abono ab2 = new Abono("AB-002", o2.getId(), 15.00);
                o2.agregarAbono(ab2);
                abonoDAO.save(ab2);
                o2.setEstado(EstadoOrden.PENDIENTE);
                ordenDAO.save(o2);
                
                // Orden 3 (Ingresada, sin abonos)
                Orden o3 = new Orden("ORD-000003", pacienteDAO.findById("PAC-3"), medicoDAO.findById("MED-3").toString());
                Prueba uric = pruebaDAO.findById("PR-05");
                o3.addPrueba(uric);
                o3.setCostoTotal(uric.getPrecio());
                ordenDAO.save(o3);
            }
            
            System.out.println("¡Datos falsos generados con éxito!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al generar datos: " + e.getMessage());
        }
    }
}
