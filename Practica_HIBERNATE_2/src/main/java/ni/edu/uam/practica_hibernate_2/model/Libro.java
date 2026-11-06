package ni.edu.uam.practica_hibernate_2.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "libros", schema = "catalogos")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "autor", nullable = false, length = 100)
    private String autor;

    @Column(name = "anio_publicacion", nullable = false)
    private Integer anio_publicacion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;


    public Libro() {

    }

    public Libro(Long id) {
        this.id = id;
    }

    public Libro(String titulo, String autor, Integer anio_publicacion, BigDecimal precio) {
        this.titulo = titulo;
        this.autor = autor;
        this.anio_publicacion = anio_publicacion;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAnio_publicacion() {
        return anio_publicacion;
    }

    public void setAnio_publicacion(Integer anio_publicacion) {
        this.anio_publicacion = anio_publicacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
