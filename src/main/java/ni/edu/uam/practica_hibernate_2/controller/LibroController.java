package ni.edu.uam.practica_hibernate_2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.practica_hibernate_2.dao.CategoriaDAO;
import ni.edu.uam.practica_hibernate_2.dao.LibroDAO;
import ni.edu.uam.practica_hibernate_2.model.Categoria;
import ni.edu.uam.practica_hibernate_2.model.Libro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibroController {


    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtAnio_publicacion;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;

    @FXML
    private ComboBox<Categoria> cmbCategorias;


    @FXML
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, Long> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, Integer> colAnio_publicacion;
    @FXML
    private TableColumn<Libro, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Libro, String> colCategoria;


    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private Long idSeleccionado = null;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio_publicacion.setCellValueFactory(new PropertyValueFactory<>("anio_publicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCategoria.setCellValueFactory(data -> {

            Categoria categoria = data.getValue().getCategoria();

            String nombre = categoria != null
                    ? categoria.getNombre()
                    : "Sin categoría";
            return new SimpleStringProperty(nombre);
        });
        tblLibros.getSelectionModel().selectedItemProperty().addListener((
                obs, oldValue, newValue) -> {
            if (newValue != null) {
                seleccionarLibro(newValue);
            } else {
                idSeleccionado = null;
                actualizarBotones();
            }
        });

        actualizarBotones();
        cargarLibros();

        // Cargar categorías en el ComboBox
        cargarCategorias();

    }


    private void seleccionarLibro(Libro libro) {
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio_publicacion.setText(String.valueOf(libro.getAnio_publicacion()));
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        Categoria categoria = libro.getCategoria();
        if (categoria != null) {
            cmbCategorias.getSelectionModel().select(categoria);
        } else {
            cmbCategorias.getSelectionModel().clearSelection();
        }
        actualizarBotones();

    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Libro obtenerLibroFormulario() {
        String titulo = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        String autor = txtAutor.getText() == null ? "" : txtAutor.getText().trim();
        String textoAnio = txtAnio_publicacion.getText() == null
                ? "" : txtAnio_publicacion.getText().trim();
        String textoPrecio = txtPrecio.getText() == null
                ? "" : txtPrecio.getText().trim();

        if (titulo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Falta el título",
                    "Escribe el título del libro.");
            txtTitulo.requestFocus();
            return null;
        }

        if (titulo.length() > 150) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Título demasiado largo",
                    "El título no puede superar los 150 caracteres.");
            txtTitulo.requestFocus();
            return null;
        }

        if (autor.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Falta el autor",
                    "Escribe el nombre del autor.");
            txtAutor.requestFocus();
            return null;
        }

        if (autor.length() > 100) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Autor demasiado largo",
                    "El nombre del autor no puede superar los 100 caracteres.");
            txtAutor.requestFocus();
            return null;
        }

        if (!textoAnio.matches("\\d+")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Año inválido",
                    "El año debe contener solo números enteros.");
            txtAnio_publicacion.requestFocus();
            return null;
        }

        int anio;
        try {
            anio = Integer.parseInt(textoAnio);
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Año inválido",
                    "El año está fuera del rango permitido.");
            txtAnio_publicacion.requestFocus();
            return null;
        }

        if (anio < 1450 || anio > 21000) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Año fuera de rango",
                    "El año debe estar entre 1450 y 21000.");
            txtAnio_publicacion.requestFocus();
            return null;
        }

        // Acepta un punto decimal y hasta dos cifras decimales.
        // No acepta letras, signos, comas ni más de 10 dígitos enteros.
        if (!textoPrecio.matches("\\d{1,10}(\\.\\d{1,2})?")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Precio inválido",
                    "Escribe un precio no negativo, con hasta 10 dígitos enteros "
                            + "y máximo 2 decimales. Usa punto para los decimales; "
                            + "por ejemplo: 19.99.");
            txtPrecio.requestFocus();
            return null;
        }

        BigDecimal precio = new BigDecimal(textoPrecio);

        Categoria categoria = cmbCategorias.getSelectionModel().getSelectedItem();
        if (categoria == null || categoria.getId() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Categoría inválida",
                    "Selecciona una categoría válida.");
            cmbCategorias.requestFocus();
            return null;
        }

        return new Libro(titulo, autor, anio, precio, categoria);
    }


    @FXML
    private void guardarLibro() {
        try{
            Libro libro = obtenerLibroFormulario();
            libroDAO.guardar(libro);
            limpiarFormulario();
            cargarLibros();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Libro guardado",
                    "Operación completada", "El libro se guardó correctamente.");
        }catch (Exception ex){
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el libro",
                    "Revisa la conexión con la base de datos y los datos ingresados.");
        }
    }

    @FXML
    private void modificarLibro() {
        try{
            Libro libro = obtenerLibroFormulario();
            libro.setId(idSeleccionado);
            boolean modificado = libroDAO.modificar(libro);

            // Validación para verificar si se seleccionó un libro y si la modificación fue exitosa
            if (idSeleccionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                        "Selecciona un libro", "Debes seleccionar un libro para modificarlo.");
                return;
            }


            if (!modificado) {
                // Alert para indicar que no se encontró el libro en la base de datos
                mostrarAlerta(Alert.AlertType.WARNING, "No se modificó",
                        "Libro no encontrado",
                        "No se encontró el libro seleccionado en la base de datos.");

                return;
            }
            limpiarFormulario();
            cargarLibros();

            // Alert para indicar que la modificación fue exitosa
            mostrarAlerta(Alert.AlertType.INFORMATION, "Libro modificado",
                    "Operación completada", "El libro se modificó correctamente.");

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al modificar",
                    "No se pudo modificar el libro",
                    "Revisa la conexión con la base de datos y los datos ingresados.");
        }
    }


    @FXML
    private void eliminarLibro() {

        // Validación para verificar si se seleccionó un libro antes de intentar eliminarlo
        if (idSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección",
                    "Selecciona un libro", "Debes seleccionar un libro para eliminarlo.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar libro");
        confirmacion.setContentText("¿Seguro que deseas eliminar el libro seleccionado?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }


        try {
            // Intentar eliminar el libro seleccionado en caso de que no se encuentre en la base de datos, mostrar un mensaje de advertencia
            if (!libroDAO.eliminar(idSeleccionado)) {
                mostrarAlerta(Alert.AlertType.WARNING, "No se eliminó",
                        "Libro no encontrado",
                        "No se encontró el libro seleccionado en la base de datos.");
                return;
            }


            limpiarFormulario();
            cargarLibros();

            // Si la eliminación fue exitosa, limpiar el formulario y recargar la lista de libros
            mostrarAlerta(Alert.AlertType.INFORMATION, "Libro eliminado",
                    "Operación completada", "El libro se eliminó correctamente.");

        // Validación para verificar si se seleccionó un libro antes de intentar eliminarlo
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar",
                    "No se pudo eliminar el libro",
                    "Revisa la conexión con la base de datos.");
        }
    }

    @FXML
    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listar();
            limpiarFormulario();
            tblLibros.setItems(FXCollections.observableArrayList(libros));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    private void cargarCategorias() {
        try {
            List<Categoria> categoria = categoriaDAO.listar();
            cmbCategorias.setItems(FXCollections.observableArrayList(categoria));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormulario() {
        tblLibros.getSelectionModel().clearSelection();
        idSeleccionado = null;
        txtTitulo.clear();
        txtAutor.clear();
        txtAnio_publicacion.clear();
        txtPrecio.clear();
        cmbCategorias.getSelectionModel().clearSelection();
        actualizarBotones();
    }

    // Método para mostrar alertas
    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String encabezado,
            String mensaje
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
