import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import udla.adminus.mmunoz.SQL;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static SQL sql = new SQL();

    private static String cedulaActual = "";
    private static String rolActual = "";
    private static boolean sesionIniciada = false;

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            if (!sesionIniciada) {
                mostrarMenuInicial();
                int opcion = leerOpcion();

                switch (opcion) {
                    case 1:
                        iniciarSesion();
                        break;
                    case 2:
                        registrarse();
                        break;
                    case 3:
                        continuar = false;
                        System.out.println("\n¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } else {
                menuPrincipal();
            }
        }

        scanner.close();
    }

    // ==================== MENÚ INICIAL ====================

    private static void mostrarMenuInicial() {
        System.out.println("\n========================================");
        System.out.println("  SISTEMA EDUCATIVO - MENÚ PRINCIPAL");
        System.out.println("========================================");
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ==================== REGISTRO ====================

    private static void registrarse() {
        System.out.println("\n========================================");
        System.out.println("  REGISTRO DE NUEVO USUARIO");
        System.out.println("========================================");
        System.out.println("¿Qué tipo de usuario desea registrar?");
        System.out.println("1. Estudiante");
        System.out.println("2. Docente");
        System.out.println("3. Personal Administrativo");
        System.out.print("Seleccione una opción: ");

        int tipoUsuario = leerOpcion();

        System.out.println("\n--- DATOS PERSONALES ---");
        System.out.print("Cédula: ");
        String cedula = scanner.nextLine();

        Connection conn = sql.getConnection();
        if (conn == null) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            return;
        }

        if (sql.estudianteExiste(cedula, conn) || sql.docenteExiste(cedula, conn)) {
            System.out.println("Error: Ya existe un usuario con esta cédula.");
            cerrarConexion(conn);
            return;
        }

        System.out.print("Nombre Completo: ");
        String nombreCompleto = scanner.nextLine();

        System.out.print("Edad: ");
        int edad = leerOpcion();

        System.out.print("Género (M/F): ");
        String genero = scanner.nextLine();

        System.out.print("Dirección: ");
        String direccion = scanner.nextLine();

        System.out.print("Teléfono: ");
        long telefono = leerTelefono();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        switch (tipoUsuario) {
            case 1:
                registrarEstudiante(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);
                break;
            case 2:
                registrarDocente(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);
                break;
            case 3:
                registrarAdministrativo(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);
                break;
            default:
                System.out.println("Opción no válida.");
        }

        cerrarConexion(conn);
    }

    private static void registrarEstudiante(String cedula, String nombreCompleto, int edad, String genero,
                                            String direccion, long telefono, String email, Connection conn) {
        System.out.println("\n--- DATOS ACADÉMICOS ---");
        System.out.print("Nivel Educativo: ");
        String nivelEducativo = scanner.nextLine();

        System.out.print("Paralelo: ");
        String paralelo = scanner.nextLine();

        System.out.print("Nombre del Representante: ");
        String representante = scanner.nextLine();

        System.out.print("ID del Curso: ");
        int cursoId = leerOpcion();

        sql.insertarDatos(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);
        sql.insertarEstudiante(cedula, nivelEducativo, paralelo, representante, cursoId, conn);

        System.out.println("\n========================================");
        System.out.println("✓ Estudiante registrado exitosamente");
        System.out.println("Ya puede iniciar sesión con su cédula");
        System.out.println("========================================");
    }

    private static void registrarDocente(String cedula, String nombreCompleto, int edad, String genero,
                                         String direccion, long telefono, String email, Connection conn) {
        System.out.println("\n--- DATOS LABORALES ---");
        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine();

        System.out.print("Título Académico: ");
        String tituloAcademico = scanner.nextLine();

        System.out.print("Jornada Laboral: ");
        String jornadaLaboral = scanner.nextLine();

        System.out.print("Sueldo Mensual: ");
        double sueldoMensual = leerDouble();

        System.out.print("Carga Horaria (horas): ");
        int cargaHoraria = leerOpcion();

        System.out.print("Horario de Clases: ");
        String horarioClases = scanner.nextLine();

        System.out.println("\n--- ASIGNATURA ---");
        System.out.println("Ahora debe crear la asignatura que impartirá");

        System.out.print("Nombre de la Asignatura: ");
        String nombreAsignatura = scanner.nextLine();

        System.out.print("Código de la Asignatura: ");
        String codigoAsignatura = scanner.nextLine();

        System.out.print("Horas Semanales: ");
        int horasSemanales = leerOpcion();

        sql.insertarDocente(cedula, nombreCompleto, edad, genero, direccion, telefono, email,
                especialidad, tituloAcademico, jornadaLaboral, sueldoMensual, cargaHoraria, horarioClases,
                nombreAsignatura, codigoAsignatura, horasSemanales, conn);

        System.out.println("\n========================================");
        System.out.println("✓ Docente registrado exitosamente");
        System.out.println("Asignatura creada: " + nombreAsignatura + " (" + codigoAsignatura + ")");
        System.out.println("Ya puede iniciar sesión con su cédula");
        System.out.println("========================================");
    }

    private static void registrarAdministrativo(String cedula, String nombreCompleto, int edad, String genero,
                                                String direccion, long telefono, String email, Connection conn) {
        System.out.println("\n--- DATOS LABORALES ---");
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();

        System.out.print("Área: ");
        String area = scanner.nextLine();

        System.out.print("Jornada Laboral: ");
        String jornadaLaboral = scanner.nextLine();

        System.out.print("Horas Trabajadas: ");
        int horasTrabajadas = leerOpcion();

        System.out.print("Sueldo: ");
        double sueldo = leerDouble();

        sql.insertarAdministrativo(cedula, nombreCompleto, edad, genero, direccion, telefono, email,
                cargo, area, jornadaLaboral, horasTrabajadas, sueldo, conn);

        System.out.println("\n========================================");
        System.out.println("✓ Personal Administrativo registrado exitosamente");
        System.out.println("Ya puede iniciar sesión con su cédula");
        System.out.println("========================================");
    }

    // ==================== INICIO DE SESIÓN ====================

    private static void iniciarSesion() {
        System.out.println("\n========================================");
        System.out.println("  INICIAR SESIÓN");
        System.out.println("========================================");
        System.out.print("Cédula: ");
        String cedula = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        Connection conn = sql.getConnection();
        if (conn == null) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            return;
        }

        String rol = sql.verificarCredenciales(cedula, email, conn);
        cerrarConexion(conn);

        if (rol == null) {
            System.out.println("\nError: Credenciales incorrectas.");
            return;
        }

        cedulaActual = cedula;
        rolActual = rol;
        sesionIniciada = true;

        System.out.println("\n¡Bienvenido! Ha iniciado sesión como: " + rol);
    }

    // ==================== MENÚ PRINCIPAL ====================

    private static void menuPrincipal() {
        if (rolActual.equals("Estudiante")) {
            mostrarMenuEstudiante();
            int opcion = leerOpcion();
            menuEstudiante(opcion);
        } else if (rolActual.equals("Docente")) {
            mostrarMenuDocente();
            int opcion = leerOpcion();
            menuDocente(opcion);
        } else if (rolActual.equals("Administrativo")) {
            mostrarMenuAdministrativo();
            int opcion = leerOpcion();
            menuAdministrativo(opcion);
        }
    }

    // ==================== MENÚS DE CADA ROL ====================

    private static void mostrarMenuEstudiante() {
        System.out.println("\n========================================");
        System.out.println("  MENÚ ESTUDIANTE");
        System.out.println("========================================");
        System.out.println("1. Ver mis Notas");
        System.out.println("2. Ver mi Asistencia");
        System.out.println("3. Ver mis Asignaturas Inscritas");
        System.out.println("4. Ver mi Información Personal");
        System.out.println("5. Actualizar mi Información");
        System.out.println("6. Cerrar Sesión");
        System.out.print("Seleccione una opción: ");
    }

    private static void mostrarMenuDocente() {
        System.out.println("\n========================================");
        System.out.println("  MENÚ DOCENTE");
        System.out.println("========================================");
        System.out.println("1. Ver y Editar Notas de Estudiantes");
        System.out.println("2. Ver y Editar Asistencia de Estudiantes");
        System.out.println("3. Inscribir Estudiante a mi Materia");
        System.out.println("4. Ver Estudiantes Inscritos en mi Materia");
        System.out.println("5. Ver mi Información");
        System.out.println("6. Cerrar Sesión");
        System.out.print("Seleccione una opción: ");
    }

    private static void mostrarMenuAdministrativo() {
        System.out.println("\n========================================");
        System.out.println("  MENÚ PERSONAL ADMINISTRATIVO");
        System.out.println("========================================");
        System.out.println("1. Gestionar Notas de Estudiantes");
        System.out.println("2. Gestionar Asistencia de Estudiantes");
        System.out.println("3. Gestionar Estado de Docentes");
        System.out.println("4. Ver Todos los Docentes");
        System.out.println("5. Ver mi Información");
        System.out.println("6. Cerrar Sesión");
        System.out.print("Seleccione una opción: ");
    }

    // ==================== LÓGICA DE MENÚS ====================

    private static void menuEstudiante(int opcion) {
        Connection conn = sql.getConnection();
        if (conn == null) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            return;
        }

        switch (opcion) {
            case 1:
                System.out.print("ID de la Asignatura: ");
                int asignaturaIdNotas = leerOpcion();
                sql.verNotasEstudianteEnAsignatura(cedulaActual, asignaturaIdNotas, conn);
                break;
            case 2:
                System.out.print("ID de la Asignatura: ");
                int asignaturaIdAsistencia = leerOpcion();
                sql.verAsistenciaEstudianteEnAsignatura(cedulaActual, asignaturaIdAsistencia, conn);
                break;
            case 3:
                verAsignaturasInscritasEstudiante(conn);
                break;
            case 4:
                verInformacionEstudiante(conn);
                break;
            case 5:
                actualizarInformacionEstudiante(conn);
                break;
            case 6:
                cerrarSesion();
                break;
            default:
                System.out.println("Opción no válida.");
        }

        cerrarConexion(conn);
    }

    private static void menuDocente(int opcion) {
        Connection conn = sql.getConnection();
        if (conn == null) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            return;
        }

        int asignaturaId = sql.obtenerAsignaturaDocente(cedulaActual, conn);

        if (asignaturaId == -1) {
            System.out.println("Error: No tiene asignatura asignada.");
            cerrarConexion(conn);
            return;
        }

        String[] datosAsignatura = sql.obtenerDatosAsignatura(asignaturaId, conn);

        switch (opcion) {
            case 1:
                gestionarNotasDocente(asignaturaId, datosAsignatura, conn);
                break;
            case 2:
                gestionarAsistenciaDocente(asignaturaId, datosAsignatura, conn);
                break;
            case 3:
                inscribirEstudianteEnMateriaDocente(asignaturaId, datosAsignatura, conn);
                break;
            case 4:
                verEstudiantesInscritosDocente(asignaturaId, datosAsignatura, conn);
                break;
            case 5:
                verInformacionDocente(conn);
                break;
            case 6:
                cerrarSesion();
                break;
            default:
                System.out.println("Opción no válida.");
        }

        cerrarConexion(conn);
    }

    private static void menuAdministrativo(int opcion) {
        Connection conn = sql.getConnection();
        if (conn == null) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            return;
        }

        switch (opcion) {
            case 1:
                gestionarNotasAdministrativo(conn);
                break;
            case 2:
                gestionarAsistenciaAdministrativo(conn);
                break;
            case 3:
                gestionarEstadoDocentes(conn);
                break;
            case 4:
                sql.verTodosDocentesConEstado(conn);
                break;
            case 5:
                sql.verInformacionAdministrativo(cedulaActual, conn);
                break;
            case 6:
                cerrarSesion();
                break;
            default:
                System.out.println("Opción no válida.");
        }

        cerrarConexion(conn);
    }

    // ==================== FUNCIONALIDADES DOCENTE ====================

    private static void gestionarNotasDocente(int asignaturaId, String[] datosAsignatura, Connection conn) {
        System.out.println("\n========================================");
        System.out.println("GESTIÓN DE NOTAS");
        System.out.println("Asignatura: " + datosAsignatura[0] + " (" + datosAsignatura[1] + ")");
        System.out.println("========================================");

        sql.listarEstudiantesInscritosEnMateria(asignaturaId, conn);

        System.out.println("\n1. Registrar Nota de un Estudiante");
        System.out.println("2. Ver Notas de un Estudiante");
        System.out.println("3. Ver Todas las Notas de la Asignatura");
        System.out.println("4. Volver al Menú Principal");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1:
                registrarNotaDocente(asignaturaId, conn);
                break;
            case 2:
                verNotasEstudianteDocente(asignaturaId, conn);
                break;
            case 3:
                sql.verTodasNotasAsignatura(asignaturaId, conn);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void registrarNotaDocente(int asignaturaId, Connection conn) {
        System.out.print("\nIngrese la cédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        if (!sql.estudianteInscritoEnAsignatura(cedulaEstudiante, asignaturaId, conn)) {
            System.out.println("Error: El estudiante no está inscrito en su materia.");
            return;
        }

        System.out.print("Nombre del parcial: ");
        String parcial = scanner.nextLine();

        if (sql.notaExiste(cedulaEstudiante, asignaturaId, parcial, conn)) {
            System.out.println("Error: Ya existe una nota para este parcial.");
            return;
        }

        System.out.print("Nota (0-10): ");
        double nota = leerDouble();

        if (nota < 0 || nota > 10) {
            System.out.println("Error: La nota debe estar entre 0 y 10.");
            return;
        }

        System.out.print("Observaciones (Enter para omitir): ");
        String observaciones = scanner.nextLine();

        sql.insertarNota(cedulaEstudiante, asignaturaId, parcial, nota, observaciones, conn);
        System.out.println("✓ Nota registrada exitosamente");
    }

    private static void verNotasEstudianteDocente(int asignaturaId, Connection conn) {
        System.out.print("\nIngrese la cédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        sql.verNotasEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);
    }

    private static void gestionarAsistenciaDocente(int asignaturaId, String[] datosAsignatura, Connection conn) {
        System.out.println("\n========================================");
        System.out.println("GESTIÓN DE ASISTENCIA");
        System.out.println("Asignatura: " + datosAsignatura[0] + " (" + datosAsignatura[1] + ")");
        System.out.println("========================================");

        sql.listarEstudiantesInscritosEnMateria(asignaturaId, conn);

        System.out.println("\n1. Registrar Asistencia de un Estudiante");
        System.out.println("2. Ver Asistencia de un Estudiante");
        System.out.println("3. Ver Registro Completo de Asistencia");
        System.out.println("4. Volver al Menú Principal");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1:
                registrarAsistenciaDocente(asignaturaId, conn);
                break;
            case 2:
                verAsistenciaEstudianteDocente(asignaturaId, conn);
                break;
            case 3:
                sql.verRegistroCompletoAsistencia(asignaturaId, conn);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void registrarAsistenciaDocente(int asignaturaId, Connection conn) {
        System.out.print("\nIngrese la cédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        if (!sql.estudianteInscritoEnAsignatura(cedulaEstudiante, asignaturaId, conn)) {
            System.out.println("Error: El estudiante no está inscrito en su materia.");
            return;
        }

        System.out.print("Fecha (YYYY-MM-DD): ");
        String fecha = scanner.nextLine();

        if (sql.asistenciaExiste(cedulaEstudiante, asignaturaId, fecha, conn)) {
            System.out.println("Error: Ya existe registro de asistencia para esta fecha.");
            return;
        }

        int cursoId = sql.obtenerCursoIdEstudiante(cedulaEstudiante, conn);

        if (cursoId == -1) {
            System.out.println("Error: No se pudo obtener el curso del estudiante.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("Seleccione el estado de asistencia:");
        System.out.println("========================================");
        System.out.println("1. Presente");
        System.out.println("2. Ausente");
        System.out.println("3. Tarde");
        System.out.println("4. Justificado");
        System.out.println("========================================");
        System.out.print("Opción: ");

        int opcionEstado = leerOpcion();
        String estado;

        switch (opcionEstado) {
            case 1:
                estado = "Presente";
                break;
            case 2:
                estado = "Ausente";
                break;
            case 3:
                estado = "Tarde";
                break;
            case 4:
                estado = "Justificado";
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        System.out.print("Observación (Enter para omitir): ");
        String observacion = scanner.nextLine();

        sql.insertarAsistencia(cedulaEstudiante, asignaturaId, cursoId, fecha, estado, observacion, conn);
        System.out.println("✓ Asistencia registrada exitosamente");
    }

    private static void verAsistenciaEstudianteDocente(int asignaturaId, Connection conn) {
        System.out.print("\nIngrese la cédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        sql.verAsistenciaEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);
    }

    private static void inscribirEstudianteEnMateriaDocente(int asignaturaId, String[] datosAsignatura, Connection conn) {
        System.out.println("\n========================================");
        System.out.println("INSCRIBIR ESTUDIANTE");
        System.out.println("Asignatura: " + datosAsignatura[0] + " (" + datosAsignatura[1] + ")");
        System.out.println("========================================");

        sql.listarTodosEstudiantes(conn);

        System.out.print("\nIngrese la cédula del estudiante a inscribir (o 0 para cancelar): ");
        String cedulaEstudiante = scanner.nextLine();

        if (cedulaEstudiante.equals("0")) {
            return;
        }

        if (!sql.estudianteExiste(cedulaEstudiante, conn)) {
            System.out.println("Error: La cédula no corresponde a ningún estudiante registrado.");
            return;
        }

        if (sql.estudianteInscritoEnAsignatura(cedulaEstudiante, asignaturaId, conn)) {
            System.out.println("Error: El estudiante ya está inscrito en esta materia.");
            return;
        }

        String nombreEstudiante = sql.obtenerNombreEstudiante(cedulaEstudiante, conn);

        System.out.print("¿Está seguro de inscribir a " + nombreEstudiante + " en " + datosAsignatura[0] + "? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            sql.inscribirEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);

            System.out.println("\n========================================");
            System.out.println("✓ INSCRIPCIÓN EXITOSA");
            System.out.println("========================================");
            System.out.println("Estudiante: " + nombreEstudiante);
            System.out.println("Asignatura: " + datosAsignatura[0]);
            System.out.println("Fecha: " + java.time.LocalDate.now());
            System.out.println("========================================");
        }
    }

    private static void verEstudiantesInscritosDocente(int asignaturaId, String[] datosAsignatura, Connection conn) {
        System.out.println("\n========================================");
        System.out.println("ESTUDIANTES INSCRITOS");
        System.out.println("Asignatura: " + datosAsignatura[0] + " (" + datosAsignatura[1] + ")");
        System.out.println("========================================");

        sql.verEstudiantesInscritosDetallado(asignaturaId, conn);
    }

    private static void verInformacionDocente(Connection conn) {
        sql.verInformacionDocente(cedulaActual, conn);
    }

    // ==================== FUNCIONALIDADES ESTUDIANTE ====================

    private static void verAsignaturasInscritasEstudiante(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT a.nombre, a.codigo, a.horas_semanales, i.fecha_inscripcion " +
                        "FROM inscripcion i " +
                        "INNER JOIN asignatura a ON i.asignatura_id = a.id_asignatura " +
                        "WHERE i.estudiante_cedula = ? " +
                        "ORDER BY a.nombre";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaActual);
            rs = ps.executeQuery();

            System.out.println("\n========================================");
            System.out.println("MIS ASIGNATURAS INSCRITAS");
            System.out.println("========================================");

            boolean hayAsignaturas = false;
            while (rs.next()) {
                hayAsignaturas = true;
                System.out.println("Materia: " + rs.getString("nombre"));
                System.out.println("Código: " + rs.getString("codigo"));
                System.out.println("Horas Semanales: " + rs.getInt("horas_semanales"));
                System.out.println("Fecha Inscripción: " + rs.getDate("fecha_inscripcion"));
                System.out.println("----------------------------------------");
            }

            if (!hayAsignaturas) {
                System.out.println("No está inscrito en ninguna asignatura.");
            }

            System.out.println("========================================");

        } catch (SQLException e) {
            System.out.println("Error al ver asignaturas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    private static void verInformacionEstudiante(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.nombre_completo, p.cedula, p.edad, p.genero, p.direccion, p.telefono, p.email, " +
                        "e.nivel_educativo, e.paralelo, e.representante " +
                        "FROM persona p " +
                        "INNER JOIN estudiante e ON p.cedula = e.cedula " +
                        "WHERE e.cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaActual);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n========================================");
                System.out.println("  MI INFORMACIÓN PERSONAL");
                System.out.println("========================================");
                System.out.println("Cédula: " + rs.getString("cedula"));
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad") + " años");
                System.out.println("Género: " + rs.getString("genero"));
                System.out.println("Dirección: " + rs.getString("direccion"));
                System.out.println("Teléfono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("\n--- INFORMACIÓN ACADÉMICA ---");
                System.out.println("Nivel Educativo: " + rs.getString("nivel_educativo"));
                System.out.println("Paralelo: " + rs.getString("paralelo"));
                System.out.println("Representante: " + rs.getString("representante"));
                System.out.println("========================================");
            }

        } catch (SQLException e) {
            System.out.println("Error al ver información: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    private static void actualizarInformacionEstudiante(Connection conn) {
        System.out.println("\n========================================");
        System.out.println("  ACTUALIZAR MI INFORMACIÓN");
        System.out.println("========================================");

        System.out.print("Nuevo Email: ");
        String email = scanner.nextLine();

        System.out.print("Nuevo Teléfono: ");
        long telefono = leerTelefono();

        System.out.print("Nueva Dirección: ");
        String direccion = scanner.nextLine();

        sql.actualizarInformacionEstudiante(cedulaActual, email, telefono, direccion, conn);
    }

    // ==================== FUNCIONALIDADES ADMINISTRATIVO ====================

    private static void gestionarNotasAdministrativo(Connection conn) {
        System.out.println("\n========================================");
        System.out.println("  GESTIONAR NOTAS DE ESTUDIANTES");
        System.out.println("========================================");

        sql.listarTodasAsignaturas(conn);

        System.out.print("\nID de la Asignatura: ");
        int asignaturaId = leerOpcion();

        if (!sql.asignaturaExiste(asignaturaId, conn)) {
            System.out.println("Error: La asignatura no existe.");
            return;
        }

        sql.listarEstudiantesInscritosEnMateria(asignaturaId, conn);

        System.out.println("\n1. Registrar Nota");
        System.out.println("2. Ver Notas de un Estudiante");
        System.out.println("3. Ver Todas las Notas");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1:
                registrarNotaAdministrativo(asignaturaId, conn);
                break;
            case 2:
                verNotasEstudianteAdministrativo(asignaturaId, conn);
                break;
            case 3:
                sql.verTodasNotasAsignatura(asignaturaId, conn);
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void registrarNotaAdministrativo(int asignaturaId, Connection conn) {
        System.out.print("\nCédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        System.out.print("Nombre del parcial: ");
        String parcial = scanner.nextLine();

        System.out.print("Nota (0-10): ");
        double nota = leerDouble();

        System.out.print("Observaciones: ");
        String observaciones = scanner.nextLine();

        sql.insertarNota(cedulaEstudiante, asignaturaId, parcial, nota, observaciones, conn);
    }

    private static void verNotasEstudianteAdministrativo(int asignaturaId, Connection conn) {
        System.out.print("\nCédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        sql.verNotasEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);
    }

    private static void gestionarAsistenciaAdministrativo(Connection conn) {
        System.out.println("\n========================================");
        System.out.println("  GESTIONAR ASISTENCIA DE ESTUDIANTES");
        System.out.println("========================================");

        sql.listarTodasAsignaturas(conn);

        System.out.print("\nID de la Asignatura: ");
        int asignaturaId = leerOpcion();

        if (!sql.asignaturaExiste(asignaturaId, conn)) {
            System.out.println("Error: La asignatura no existe.");
            return;
        }

        sql.listarEstudiantesInscritosEnMateria(asignaturaId, conn);

        System.out.println("\n1. Registrar Asistencia");
        System.out.println("2. Ver Asistencia de un Estudiante");
        System.out.println("3. Ver Registro Completo");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1:
                registrarAsistenciaAdministrativo(asignaturaId, conn);
                break;
            case 2:
                verAsistenciaEstudianteAdministrativo(asignaturaId, conn);
                break;
            case 3:
                sql.verRegistroCompletoAsistencia(asignaturaId, conn);
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void registrarAsistenciaAdministrativo(int asignaturaId, Connection conn) {
        System.out.print("\nCédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        System.out.print("Fecha (YYYY-MM-DD): ");
        String fecha = scanner.nextLine();

        int cursoId = sql.obtenerCursoIdEstudiante(cedulaEstudiante, conn);

        System.out.println("\n1. Presente");
        System.out.println("2. Ausente");
        System.out.println("3. Tarde");
        System.out.println("4. Justificado");
        System.out.print("Estado: ");
        int opcionEstado = leerOpcion();

        String estado;
        switch (opcionEstado) {
            case 1: estado = "Presente"; break;
            case 2: estado = "Ausente"; break;
            case 3: estado = "Tarde"; break;
            case 4: estado = "Justificado"; break;
            default: estado = "Presente";
        }

        System.out.print("Observación: ");
        String observacion = scanner.nextLine();

        sql.insertarAsistencia(cedulaEstudiante, asignaturaId, cursoId, fecha, estado, observacion, conn);
    }

    private static void verAsistenciaEstudianteAdministrativo(int asignaturaId, Connection conn) {
        System.out.print("\nCédula del estudiante: ");
        String cedulaEstudiante = scanner.nextLine();

        sql.verAsistenciaEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);
    }

    private static void gestionarEstadoDocentes(Connection conn) {
        System.out.println("\n========================================");
        System.out.println("  GESTIONAR ESTADO DE DOCENTES");
        System.out.println("========================================");

        sql.verTodosDocentesConEstado(conn);

        System.out.print("\nCédula del docente: ");
        String cedulaDocente = scanner.nextLine();

        if (!sql.docenteExiste(cedulaDocente, conn)) {
            System.out.println("Error: El docente no existe.");
            return;
        }

        String estadoActual = sql.obtenerEstadoDocente(cedulaDocente, conn);
        System.out.println("Estado actual: " + estadoActual);

        System.out.println("\n1. Activo");
        System.out.println("2. Inactivo");
        System.out.println("3. Licencia");
        System.out.print("Nuevo estado: ");

        int opcion = leerOpcion();
        String nuevoEstado;

        switch (opcion) {
            case 1: nuevoEstado = "Activo"; break;
            case 2: nuevoEstado = "Inactivo"; break;
            case 3: nuevoEstado = "Licencia"; break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        sql.cambiarEstadoDocente(cedulaDocente, nuevoEstado, conn);
    }

    // ==================== CERRAR SESIÓN ====================

    private static void cerrarSesion() {
        System.out.println("\n========================================");
        System.out.println("Cerrando sesión...");
        System.out.println("========================================");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n========================================");
        System.out.println("✓ Sesión cerrada exitosamente");
        System.out.println("\n¡Hasta pronto!");
        System.out.println("========================================");

        cedulaActual = "";
        rolActual = "";
        sesionIniciada = false;
    }

    // ==================== UTILIDADES ====================

    private static int leerOpcion() {
        while (true) {
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                return opcion;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número: ");
            }
        }
    }

    private static double leerDouble() {
        while (true) {
            try {
                double valor = Double.parseDouble(scanner.nextLine());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número decimal: ");
            }
        }
    }

    private static long leerTelefono() {
        while (true) {
            try {
                long telefono = Long.parseLong(scanner.nextLine());
                return telefono;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número de teléfono válido: ");
            }
        }
    }

    private static void cerrarConexion(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}