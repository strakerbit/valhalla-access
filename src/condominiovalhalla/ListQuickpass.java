package condominiovalhalla;

import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Andrew
 * @author Kendall
 * @author Janaikel
 */
public class ListQuickpass {

    private Quickpass[] lista;
    private int cupo;

    // Constructor
    public ListQuickpass(int tamanno) {
        lista = new Quickpass[tamanno];
        cupo = tamanno;
    }

    public void agregarQuickpass() {
        if (cupo > 0) {
            String filial;
            do {
                filial = JOptionPane.showInputDialog("Ingrese la filial:");
                if (!Quickpass.validacionFilial(filial)) {
                    JOptionPane.showMessageDialog(null, "ERROR: La filial no puede estar vacía.");
                }
            } while (!Quickpass.validacionFilial(filial));

            String codigo;
            do {
                codigo = JOptionPane.showInputDialog("Ingrese el código (Debe comenzar con '101' y tener 10 dígitos):");
                if (!Quickpass.validarCodigo(codigo)) {
                    JOptionPane.showMessageDialog(null, "ERROR: El código debe tener exactamente 10 dígitos y comenzar con '101'.");
                }
            } while (!Quickpass.validarCodigo(codigo));

            String placa;
            do {
                placa = JOptionPane.showInputDialog("Ingrese la placa:");
                if (!Quickpass.validarPlaca(placa)) {
                    JOptionPane.showMessageDialog(null, "ERROR: La placa no puede estar vacía.");
                }
            } while (!Quickpass.validarPlaca(placa));

            Quickpass nuevoQuickpass = new Quickpass(filial, codigo, placa, Estado.Activo);
            for (int i = 0; i < lista.length; i++) {
                if (lista[i] == null) {
                    lista[i] = nuevoQuickpass;
                    cupo--;
                    JOptionPane.showMessageDialog(null, "Quickpass agregado con éxito.");

                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay espacio disponible para más Quickpass.");
        }
    }

    // Mostrar la lista completa
    public void mostrarQuickpass() {
        StringBuilder listado = new StringBuilder("Listado de Quickpass:\n");
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] != null) {
                listado.append((i + 1)).append(") ").append(lista[i].toString()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, listado.toString());
    }

    // Método para buscar Quickpass por código
    public Quickpass buscarQuickpass(String codigo) {
        for (Quickpass quickpass : lista) {
            if (quickpass != null && quickpass.getCodigo().equals(codigo)) {
                return quickpass;
            }
        }
        JOptionPane.showMessageDialog(null, "No se encontró un Quickpass con el código proporcionado.");
        return null;
    }

    public String validarAcceso(String codigo) {
        Quickpass quickpass = buscarQuickpass(codigo);
        String condicion;

        if (quickpass != null && quickpass.getEstado() == Estado.Activo) {
            condicion = "Aceptado";
        } else {
            condicion = "Rechazado";
        }

        registrarEnArchivo(quickpass, condicion);

        return condicion;
    }

    public void registrarEnArchivo(Quickpass quickpass, String condicion) {
        String archivo = "Historial.txt";
        LocalDateTime fechaHora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String registro = String.format("Código: %s; Placa: %s; Filial: %s; Condición: %s; Fecha: %s\n",
                quickpass.getCodigo(), quickpass.getPlaca(), quickpass.getFilial(), condicion, fechaHora.format(formato));

        try (FileWriter writer = new FileWriter(archivo, true)) {
            writer.write(registro);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar en el archivo: " + e.getMessage());
        }
    }

    public void eliminarPorCodigo(String codigo, ListQuickpass eliminados) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] != null && lista[i].getCodigo().equals(codigo)) {
                eliminados.agregarDesde(lista[i]); // Mover a eliminados
                lista[i] = null; // Eliminar del arreglo actual
                cupo++;
                JOptionPane.showMessageDialog(null, "Quickpass eliminado exitosamente.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No se encontró un Quickpass con el código proporcionado.");
    }

    public void eliminarPorPlaca(String placa, ListQuickpass eliminados) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] != null && lista[i].getPlaca().equals(placa)) {
                eliminados.agregarDesde(lista[i]); // Mover a eliminados
                lista[i] = null; // Eliminar del arreglo actual
                cupo++;
                JOptionPane.showMessageDialog(null, "Quickpass eliminado exitosamente.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No se encontró un Quickpass con la placa proporcionada.");
    }

    public void agregarDesde(Quickpass quickpass) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i] == null) {
                lista[i] = quickpass;
                cupo--;
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "No hay espacio disponible para agregar el Quickpass.");
    }

    public void inactivarQuickpass(String codigo) {
        Quickpass quickpass = buscarQuickpass(codigo);
        if (quickpass != null && quickpass.getEstado() == Estado.Activo) {
            quickpass.setEstado(Estado.Inactivo);
            JOptionPane.showMessageDialog(null, "Quickpass inactivado con éxito.");
        } else {
            JOptionPane.showMessageDialog(null, "Quickpass no encontrado o ya inactivo.");
        }
    }

    public void activarQuickpass(String codigo) {
        Quickpass quickpass = buscarQuickpass(codigo);
        if (quickpass != null && quickpass.getEstado() == Estado.Inactivo) {
            quickpass.setEstado(Estado.Activo);
            JOptionPane.showMessageDialog(null, "Quickpass Activado con éxito.");
        } else {
            JOptionPane.showMessageDialog(null, "Quickpass no encontrado o ya Activo.");
        }
    }

    public void mostrarQuickpassPorFilial(String filial) {
        StringBuilder listado = new StringBuilder("Listado de Quickpass para la filial: " + filial + "\n");
        boolean encontrado = false;

        for (int i = 0; i < lista.length; i++) { // Iteración clásica sobre el arreglo
            if (lista[i] != null && lista[i].getFilial().equalsIgnoreCase(filial)) {
                listado.append(lista[i].toString()).append("\n");
                encontrado = true;
            }
        }

        if (encontrado) {
            JOptionPane.showMessageDialog(null, listado.toString());
        } else {
            JOptionPane.showMessageDialog(null, "No se encontraron Quickpass para la filial especificada.");
        }
    }
    
    //#3
    
        public int obtenerTotalAccesos() {
        int totalAccesos = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("Historial.txt"))) {
            while (br.readLine() != null) {
                totalAccesos++;
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return totalAccesos;
    }

 
    public int obtenerTotalAccesosPorFilial(String filial) {
        int totalAccesosFilial = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("Historial.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Filial: " + filial)) {
                    totalAccesosFilial++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return totalAccesosFilial;
    }


    public int obtenerTotalQuickpass() {
        int totalQuickpass = 0;
        for (Quickpass q : lista) {
            if (q != null) {
                totalQuickpass++;
            }
        }
        return totalQuickpass;
    }

    
    public int[] obtenerQuickpassPorEstado() {
        int activos = 0, inactivos = 0;
        for (Quickpass q : lista) {
            if (q != null) {
                if (q.getEstado() == Estado.Activo) {
                    activos++;
                } else if (q.getEstado() == Estado.Inactivo) {
                    inactivos++;
                }
            }
        }
        return new int[]{activos, inactivos};
    }


    public int obtenerTotalQuickpassEliminados(ListQuickpass eliminados) {
        return eliminados.obtenerTotalQuickpass();
    }


}
