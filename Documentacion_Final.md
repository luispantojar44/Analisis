# 1. Resumen Ejecutivo

El presente documento detalla la arquitectura, requisitos y construcción del sistema **AccuLab**, un software de gestión diseñado para optimizar el flujo de trabajo dentro de un laboratorio clínico. El proyecto fue desarrollado en Java utilizando **JavaFX** para la interfaz gráfica y un patrón arquitectónico **MVC Multicapa** (Modelo-Vista-Controlador apoyado en Servicios y DAOs). Para asegurar la portabilidad y ligereza del sistema, la persistencia de datos se implementó al 100% mediante archivos binarios (`.bin`), logrando independencia total de motores de bases de datos externos. AccuLab permite a recepcionistas, laboratoristas y administradores gestionar pacientes, órdenes médicas, abonos financieros, catálogo de pruebas y la emisión de resultados médicos exportables a PDF.

---

# 2. Requisitos del Sistema

### Requisitos Funcionales
1. **Gestión de Usuarios y Accesos:** El sistema debe autenticar a los usuarios mediante credenciales encriptadas (BCrypt) según su rol (Administrador, Recepcionista, Laboratorista, Médico).
2. **Gestión de Pacientes y Médicos:** Permitir el registro, modificación y consulta de la información demográfica e historial.
3. **Catálogo de Pruebas y Perfiles:** El sistema debe almacenar pruebas individuales (con rangos de referencia dinámicos por sexo) y perfiles que agrupen múltiples pruebas.
4. **Módulo de Órdenes y Facturación:** Capacidad para generar órdenes, calcular el costo dinámicamente y permitir el registro de pagos (abonos parciales o totales).
5. **Ingreso de Resultados:** Permitir a los laboratoristas registrar los valores obtenidos y generar alertas visuales automáticas si el resultado está fuera del rango normal del paciente.
6. **Generación de Reportes PDF:** El sistema debe emitir reportes de resultados en formato PDF, admitiendo múltiples plantillas (membretado, sin membrete).

### Requisitos No Funcionales
1. **Interfaz Gráfica (UI):** Debe ser construida en JavaFX utilizando estilos CSS, garantizando una experiencia de usuario fluida y moderna.
2. **Persistencia Autónoma:** Todos los datos deben ser serializados localmente en archivos `.bin` utilizando el patrón Data Access Object (DAO) Genérico.
3. **Escalabilidad y Patrones:** El código debe aplicar programación orientada a objetos (POO) estricta, implementando patrones como DAO, Strategy y MVC.

---

# 3. Casos de Uso

Los Casos de Uso principales del sistema se dividen por Actor:

- **Recepcionista:**
  - *Generar Orden de Laboratorio:* Seleccionar al paciente, agregar pruebas, generar costo e imprimir el comprobante.
  - *Registrar Abono:* Buscar una orden en estado INGRESADA o PENDIENTE y registrar un abono para saldar la deuda.
- **Laboratorista:**
  - *Mantenimiento de Pruebas/Rangos:* Crear nuevas pruebas y establecer rangos de referencia para validaciones médicas.
  - *Ingresar Resultados:* Buscar órdenes pendientes y procesar (en lote) los resultados obtenidos de la muestra.
- **Administrador:**
  - *Gestión Administrativa:* Administrar los perfiles de los empleados, auditar eliminaciones de pacientes u órdenes.

*(Referencia al archivo adjunto: `uml/caso_uso.puml`)*

---

# 4. Diagramas del Sistema

Se han generado los diagramas arquitectónicos y de comportamiento en formato estándar **PlantUML**. Los archivos fuente se encuentran en la carpeta `uml/` adjunta a este paquete:

1. **Diagrama de Casos de Uso:** `uml/caso_uso.puml`
2. **Diagrama de Componentes (Arquitectura):** `uml/componentes.puml`
3. **Diagrama de Clases (Dominio):** `uml/clases.puml`
4. **Diagramas de Secuencia (Flujos lógicos):** 
   - `uml/secuencia_abono.puml`
   - `uml/secuencia_orden.puml`
   - `uml/secuencia_pruebas.puml`
   - `uml/secuencia_resultados.puml`
5. **Diagrama de Estados (Orden):** `uml/estado_orden.puml`

---

# 5. Código Java: Estado Actual

### Lo que ya tenemos (Implementado al 100%)
- **Arquitectura N-Tier y MVC:** Paquetes bien definidos (`controllers`, `dao`, `models`, `services`, `views`).
- **Interfaces Gráficas (JavaFX):** Formularios interactivos `.fxml`, hojas de estilos `main.css`, navegación completa.
- **Persistencia en Archivos Binarios:** `GenericBinaryDAO` construido y funcional para todas las entidades (`Usuario`, `Paciente`, `Medico`, `Orden`, `Prueba`, `Resultado`, `Abono`, `Perfil`).
- **Patrones de Diseño:** Strategy (reportes PDF), DAO genérico, Singleton (autenticación).
- **Control de Abonos y Deudas:** Sistema financiero funcional reflejado en las órdenes de los pacientes.
- **Validación Automática de Resultados:** Evaluador que compara los resultados obtenidos con los rangos de referencia paramétricos.
- **Ejecutable Nativo:** El proyecto fue empaquetado en un archivo nativo para Windows (`AccuLab.exe`).

### Lo que falta (Trabajo Futuro y Escalabilidad)
1. **Migración a Base de Datos Relacional:** Actualmente dependemos de `.bin`. La arquitectura está preparada (gracias al patrón DAO) para que en un futuro se implemente una interfaz JDBC o Hibernate (MySQL/PostgreSQL) y el cambio sea completamente transparente para el resto de la aplicación.
2. **Integración Real con API de WhatsApp/SMTP:** Actualmente, en la secuencia de envío de reportes, el servicio es un *Mock* interno. En el futuro, se deberá conectar con la API de Twilio (WhatsApp) y JavaMail.
3. **Exportación Financiera Avanzada:** Falta el desarrollo de reportería en Excel (`.xlsx` usando Apache POI) para cierres de caja diarios y contabilidad de los abonos.
4. **Despliegue en la Nube / Cliente-Servidor:** Transición de aplicación de escritorio monolítica local a una arquitectura cliente-servidor mediante sockets RMI o API REST en Spring Boot, para conectar múltiples laboratorios.
