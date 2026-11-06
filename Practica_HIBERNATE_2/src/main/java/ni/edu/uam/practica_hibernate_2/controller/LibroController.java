package ni.edu.uam.practica_hibernate_2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.practica_hibernate_2.dao.LibroDAO;
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
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, Integer> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colAnio_publicacion;
    @FXML
    private TableColumn<Libro, Double> colPrecio;

    private final LibroDAO libroDAO = new LibroDAO();

    private Long idSeleccionado = null;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio_publicacion.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
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

    }


    private void seleccionarLibro(Libro libro) {
        idSeleccionado = libro.getId();
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio_publicacion.setText(String.valueOf(libro.getAnio_publicacion()));
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        actualizarBotones();

    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Libro obtenerLibroFormulario() {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String anio_publicacion = txtAnio_publicacion.getText();
        BigDecimal precio = new BigDecimal(txtPrecio.getText());
        return new Libro(titulo, autor, anio_publicacion, precio);
    }


    @FXML
    private void guardarLibro() {
        try{
            Libro libro = obtenerLibroFormulario();
            libroDAO.guardar(libro);
            limpiarFormulario();
            cargarLibros();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void modificarLibro() {
        try{
            Libro libro = obtenerLibroFormulario();
            libro.setId(idSeleccionado);
            boolean modificado = libroDAO.modificar(libro);

            if (!modificado) {
            // Alert para indicar que hubo un error al nivel de transacción
            }
            limpiarFormulario();
            cargarLibros();

            // Alert para indicar que la modificación fue exitosa

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }


    @FXML
    private void eliminarLibro() {
        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación de eliminación de datos");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText("¿Está seguro de que desea eliminar este libro?");
            confirmacion.showAndWait();


            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if(resultado.isEmpty() || resultado.get() != ButtonType.OK){
                return;
            }

            boolean eliminado = libroDAO.eliminar(idSeleccionado);
            if (!eliminado) {
                // Alert para indicar que hubo un error al nivel de transacción
            }

            // Alert para indicar que la operación fue exitosa
            limpiarFormulario();
            cargarLibros();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listar();
            limpiarFormulario();
            tblLibros.getItems().addAll(FXCollections.observableArrayList(libros));
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
    }
}
