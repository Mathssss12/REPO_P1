package udla.adminus.mmunoz;

import java.sql.*;

public class SQL {

    // ==================== CONEXIÓN ====================

    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/adminus";
        String user = "root";
        String passwd = "sasa";

        try {
            return DriverManager.getConnection(url, user, passwd);
        } catch (SQLException ex) {
            System.out.println("Error al conectar con la base de datos:");
            ex.printStackTrace();
        }
        return null;
    }

    // ==================== INSERTAR DATOS PERSONA ====================

    public void insertarDatos(String cedula, String nombreCompleto, int edad, String genero,
                              String direccion, long telefono, String email, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO persona (cedula, nombre_completo, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, nombreCompleto);
            ps.setInt(3, edad);
            ps.setString(4, genero);
            ps.setString(5, direccion);
            ps.setLong(6, telefono);
            ps.setString(7, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar datos personales: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== INSERTAR ESTUDIANTE ====================

    public void insertarEstudiante(String cedula, String nivelEducativo, String paralelo,
                                   String representante, int cursoId, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO estudiante (cedula, nivel_educativo, paralelo, representante, curso_id) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, nivelEducativo);
            ps.setString(3, paralelo);
            ps.setString(4, representante);
            ps.setInt(5, cursoId);
            ps.executeUpdate();

            insertarRolUsuario(cedula, 3, conn);
        } catch (SQLException e) {
            System.out.println("Error al insertar estudiante: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== INSERTAR DOCENTE ====================

    public void insertarDocente(String cedula, String nombreCompleto, int edad, String genero,
                               String direccion, long telefono, String email, String especialidad,
                               String tituloAcademico, String jornadaLaboral, double sueldoMensual,
                               int cargaHoraria, String horarioClases, String nombreAsignatura,
                               String codigoAsignatura, int horasSemanales, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            insertarDatos(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);

            String sqlAsignatura = "INSERT INTO asignatura (nombre, codigo, horas_semanales) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sqlAsignatura, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombreAsignatura);
            ps.setString(2, codigoAsignatura);
            ps.setInt(3, horasSemanales);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int asignaturaId = 0;
            if (rs.next()) {
                asignaturaId = rs.getInt(1);
            }

            String sqlDocente = "INSERT INTO docente (cedula, especialidad, titulo_academico, jornada_laboral, sueldo_mensual, carga_horaria, horario_clases, asignatura_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sqlDocente);
            ps.setString(1, cedula);
            ps.setString(2, especialidad);
            ps.setString(3, tituloAcademico);
            ps.setString(4, jornadaLaboral);
            ps.setDouble(5, sueldoMensual);
            ps.setInt(6, cargaHoraria);
            ps.setString(7, horarioClases);
            ps.setInt(8, asignaturaId);
            ps.executeUpdate();

            insertarRolUsuario(cedula, 2, conn);
        } catch (SQLException e) {
            System.out.println("Error al insertar docente: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== INSERTAR ADMINISTRATIVO ====================

    public void insertarAdministrativo(String cedula, String nombreCompleto, int edad, String genero,
                                       String direccion, long telefono, String email, String cargo,
                                       String area, String jornadaLaboral, int horasTrabajadas,
                                       double sueldo, Connection conn) {
        PreparedStatement ps = null;
        try {
            insertarDatos(cedula, nombreCompleto, edad, genero, direccion, telefono, email, conn);

            String sql = "INSERT INTO administrativo (cedula, cargo, area, jornada_laboral, horas_trabajadas, sueldo) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, cargo);
            ps.setString(3, area);
            ps.setString(4, jornadaLaboral);
            ps.setInt(5, horasTrabajadas);
            ps.setDouble(6, sueldo);
            ps.executeUpdate();

            insertarRolUsuario(cedula, 1, conn);
        } catch (SQLException e) {
            System.out.println("Error al insertar administrativo: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== ASIGNAR ROL ====================

    private void insertarRolUsuario(String cedula, int rolId, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO rolusuario (usuario_cedula, rol_id) VALUES (?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setInt(2, rolId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al asignar rol: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== VERIFICAR CREDENCIALES ====================

    public String verificarCredenciales(String cedula, String email, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT r.nombre_rol FROM persona p INNER JOIN rolusuario ru ON p.cedula = ru.usuario_cedula INNER JOIN rol r ON ru.rol_id = r.id_rol WHERE p.cedula = ? AND p.email = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre_rol");
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar credenciales: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return null;
    }

    // ==================== VERIFICAR EXISTENCIA ====================

    public boolean estudianteExiste(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM estudiante WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    public boolean docenteExiste(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    public boolean asignaturaExiste(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM asignatura WHERE id_asignatura = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    public boolean estudianteInscritoEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM inscripcion WHERE estudiante_cedula = ? AND asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    public boolean notaExiste(String cedulaEstudiante, int asignaturaId, String parcial, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM notas WHERE estudiante_cedula = ? AND asignatura_id = ? AND parcial = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setString(3, parcial);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    public boolean asistenciaExiste(String cedulaEstudiante, int asignaturaId, String fecha, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM asistencia WHERE estudiante_cedula = ? AND asignatura_id = ? AND fecha = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setString(3, fecha);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return false;
    }

    // ==================== OBTENER INFORMACIÓN ====================

    public int obtenerAsignaturaDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT asignatura_id FROM docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("asignatura_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return -1;
    }

    public String[] obtenerDatosAsignatura(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT nombre, codigo FROM asignatura WHERE id_asignatura = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString("nombre"), rs.getString("codigo")};
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return new String[]{"", ""};
    }

    public int obtenerCursoIdEstudiante(String cedulaEstudiante, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT curso_id FROM estudiante WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("curso_id");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return -1;
    }

    public String obtenerNombreEstudiante(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT nombre_completo FROM persona WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return "";
    }

    public String obtenerEstadoDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT estado FROM estadoempleabilidad WHERE cedula = ? ORDER BY fecha_actualizacion DESC LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("estado");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
        return "No registrado";
    }

    // ==================== INSERTAR NOTAS ====================

    public void insertarNota(String cedulaEstudiante, int asignaturaId, String parcial,
                            double nota, String observaciones, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO notas (estudiante_cedula, asignatura_id, parcial, nota, fecha_registro, observacion) VALUES (?, ?, ?, ?, CURDATE(), ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setString(3, parcial);
            ps.setDouble(4, nota);
            ps.setString(5, observaciones);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar nota: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== INSERTAR ASISTENCIA ====================

    public void insertarAsistencia(String cedulaEstudiante, int asignaturaId, int cursoId,
                                   String fecha, String estado, String observacion, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO asistencia (estudiante_cedula, asignatura_id, curso_id, fecha, estado, observacion) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setInt(3, cursoId);
            ps.setString(4, fecha);
            ps.setString(5, estado);
            ps.setString(6, observacion);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar asistencia: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== INSCRIBIR ESTUDIANTE ====================

    public void inscribirEstudianteEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO inscripcion (estudiante_cedula, asignatura_id, fecha_inscripcion) VALUES (?, ?, CURDATE())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.executeUpdate();
            System.out.println("✓ Estudiante inscrito exitosamente");
        } catch (SQLException e) {
            System.out.println("Error al inscribir estudiante: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== CAMBIAR ESTADO DOCENTE ====================

    public void cambiarEstadoDocente(String cedulaDocente, String nuevoEstado, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO estadoempleabilidad (cedula, estado, fecha_actualizacion) VALUES (?, ?, CURDATE())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            ps.setString(2, nuevoEstado);
            ps.executeUpdate();
            System.out.println("✓ Estado actualizado exitosamente");
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== ACTUALIZAR INFORMACIÓN ESTUDIANTE ====================

    public void actualizarInformacionEstudiante(String cedula, String email, long telefono,
                                               String direccion, Connection conn) {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE persona SET email = ?, telefono = ?, direccion = ? WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setLong(2, telefono);
            ps.setString(3, direccion);
            ps.setString(4, cedula);
            ps.executeUpdate();
            System.out.println("✓ Información actualizada exitosamente");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== LISTAR ESTUDIANTES ====================

    public void listarEstudiantesInscritosEnMateria(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.cedula, p.nombre_completo FROM persona p INNER JOIN inscripcion i ON p.cedula = i.estudiante_cedula WHERE i.asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n--- ESTUDIANTES INSCRITOS ---");
            while (rs.next()) {
                System.out.println(rs.getString("cedula") + " - " + rs.getString("nombre_completo"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void listarTodosEstudiantes(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.cedula, p.nombre_completo, e.nivel_educativo, e.paralelo FROM persona p INNER JOIN estudiante e ON p.cedula = e.cedula";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n--- TODOS LOS ESTUDIANTES ---");
            while (rs.next()) {
                System.out.println(rs.getString("cedula") + " - " + rs.getString("nombre_completo") +
                                 " | " + rs.getString("nivel_educativo") + " " + rs.getString("paralelo"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== LISTAR ASIGNATURAS ====================

    public void listarTodasAsignaturas(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM asignatura";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n--- ASIGNATURAS DISPONIBLES ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id_asignatura") + " - " + rs.getString("nombre") +
                                 " (" + rs.getString("codigo") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== VER NOTAS ====================

    public void verNotasEstudiante(String cedulaEstudiante, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT a.nombre, n.parcial, n.nota, n.observacion FROM notas n INNER JOIN asignatura a ON n.asignatura_id = a.id_asignatura WHERE n.estudiante_cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            rs = ps.executeQuery();

            System.out.println("\n--- MIS NOTAS ---");
            while (rs.next()) {
                System.out.println(rs.getString("nombre") + " | " + rs.getString("parcial") +
                                 " | Nota: " + rs.getDouble("nota") + " | " + rs.getString("observacion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verNotasEstudianteEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT parcial, nota, fecha_registro, observacion FROM notas WHERE estudiante_cedula = ? AND asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n--- NOTAS DEL ESTUDIANTE ---");
            while (rs.next()) {
                System.out.println(rs.getString("parcial") + " | Nota: " + rs.getDouble("nota") +
                                 " | Fecha: " + rs.getDate("fecha_registro") + " | " + rs.getString("observacion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verTodasNotasAsignatura(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.nombre_completo, n.parcial, n.nota FROM notas n INNER JOIN persona p ON n.estudiante_cedula = p.cedula WHERE n.asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n--- TODAS LAS NOTAS ---");
            while (rs.next()) {
                System.out.println(rs.getString("nombre_completo") + " | " + rs.getString("parcial") +
                                 " | Nota: " + rs.getDouble("nota"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== VER ASISTENCIA ====================

    public void verAsistenciaEstudiante(String cedulaEstudiante, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT a.nombre, asi.fecha, asi.estado, asi.observacion FROM asistencia asi INNER JOIN asignatura a ON asi.asignatura_id = a.id_asignatura WHERE asi.estudiante_cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            rs = ps.executeQuery();

            System.out.println("\n--- MI ASISTENCIA ---");
            while (rs.next()) {
                System.out.println(rs.getString("nombre") + " | " + rs.getDate("fecha") +
                                 " | " + rs.getString("estado") + " | " + rs.getString("observacion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verAsistenciaEstudianteEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT fecha, estado, observacion FROM asistencia WHERE estudiante_cedula = ? AND asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n--- ASISTENCIA DEL ESTUDIANTE ---");
            while (rs.next()) {
                System.out.println(rs.getDate("fecha") + " | " + rs.getString("estado") + " | " + rs.getString("observacion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verRegistroCompletoAsistencia(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT a.fecha, p.cedula, p.nombre_completo, a.estado, a.observacion " +
                         "FROM asistencia a " +
                         "INNER JOIN persona p ON a.estudiante_cedula = p.cedula " +
                         "WHERE a.asignatura_id = ? " +
                         "ORDER BY a.fecha DESC, p.nombre_completo";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n================================================================================");
            System.out.println("                    REGISTRO COMPLETO DE ASISTENCIA");
            System.out.println("================================================================================");
            System.out.printf("%-12s | %-25s | %-12s | %-15s | %-20s%n",
                             "FECHA", "ESTUDIANTE", "CÉDULA", "ESTADO", "OBSERVACIÓN");
            System.out.println("-------------|---------------------------|--------------|-----------------|---------------------");

            boolean hayRegistros = false;
            while (rs.next()) {
                hayRegistros = true;
                System.out.printf("%-12s | %-25s | %-12s | %-15s | %-20s%n",
                        rs.getString("fecha"),
                        rs.getString("nombre_completo"),
                        rs.getString("cedula"),
                        rs.getString("estado"),
                        rs.getString("observacion") != null ? rs.getString("observacion") : "");
            }

            if (!hayRegistros) {
                System.out.println("No hay registros de asistencia");
            }

            System.out.println("================================================================================");
        } catch (SQLException e) {
            System.out.println("Error al ver registro de asistencia: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    // ==================== VER INFORMACIÓN ====================

    public void verInformacionDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.*, d.especialidad, d.titulo_academico, d.sueldo_mensual, a.nombre as asignatura FROM persona p INNER JOIN docente d ON p.cedula = d.cedula INNER JOIN asignatura a ON d.asignatura_id = a.id_asignatura WHERE p.cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- MI INFORMACIÓN ---");
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Cédula: " + rs.getString("cedula"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Teléfono: " + rs.getLong("telefono"));
                System.out.println("Especialidad: " + rs.getString("especialidad"));
                System.out.println("Título: " + rs.getString("titulo_academico"));
                System.out.println("Asignatura: " + rs.getString("asignatura"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verInformacionAdministrativo(String cedulaAdministrativo, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.*, a.cargo, a.area, a.sueldo FROM persona p INNER JOIN administrativo a ON p.cedula = a.cedula WHERE p.cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaAdministrativo);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- MI INFORMACIÓN ---");
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Cédula: " + rs.getString("cedula"));
                System.out.println("Cargo: " + rs.getString("cargo"));
                System.out.println("Área: " + rs.getString("area"));
                System.out.println("Email: " + rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verEstudiantesInscritosDetallado(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.cedula, p.nombre_completo, e.nivel_educativo, e.paralelo, i.fecha_inscripcion FROM persona p INNER JOIN estudiante e ON p.cedula = e.cedula INNER JOIN inscripcion i ON p.cedula = i.estudiante_cedula WHERE i.asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System.out.println("\n--- ESTUDIANTES INSCRITOS ---");
            while (rs.next()) {
                System.out.println(rs.getString("cedula") + " - " + rs.getString("nombre_completo") +
                                 " | " + rs.getString("nivel_educativo") + " " + rs.getString("paralelo") +
                                 " | Inscrito: " + rs.getDate("fecha_inscripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }

    public void verTodosDocentesConEstado(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT p.cedula, p.nombre_completo, d.especialidad, d.titulo_academico, COALESCE(e.estado, 'No registrado') as estado FROM persona p INNER JOIN docente d ON p.cedula = d.cedula LEFT JOIN (SELECT cedula, estado FROM estadoempleabilidad WHERE (cedula, fecha_actualizacion) IN (SELECT cedula, MAX(fecha_actualizacion) FROM estadoempleabilidad GROUP BY cedula)) e ON p.cedula = e.cedula ORDER BY p.nombre_completo";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n================================================================================");
            System.out.println("                          LISTADO DE DOCENTES");
            System.out.println("================================================================================");
            System.out.printf("%-12s | %-25s | %-20s | %-15s%n", "CÉDULA", "NOMBRE", "ESPECIALIDAD", "ESTADO");
            System.out.println("-------------|-----------------------------|----------------------|----------------");

            boolean hayDocentes = false;
            while (rs.next()) {
                hayDocentes = true;
                System.out.printf("%-12s | %-25s | %-20s | %-15s%n",
                        rs.getString("cedula"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad"),
                        rs.getString("estado"));
            }

            if (!hayDocentes) {
                System.out.println("No hay docentes registrados en el sistema");
            }

            System.out.println("================================================================================");
        } catch (SQLException e) {
            System.out.println("Error al ver docentes: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException e) {}
        }
    }
}