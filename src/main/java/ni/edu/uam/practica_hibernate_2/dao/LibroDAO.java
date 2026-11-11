package ni.edu.uam.practica_hibernate_2.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.practica_hibernate_2.database.JPAUtil;
import ni.edu.uam.practica_hibernate_2.model.Categoria;
import ni.edu.uam.practica_hibernate_2.model.Libro;

import java.util.List;

public class LibroDAO {


    private Categoria obetenerCategoria(EntityManager em, Libro libro) {

        // Validamos que el libro no sea null
        if(libro.getCategoria() == null || libro.getCategoria().getId() == null) {
            throw new RuntimeException("El libro no tiene una categoría válida.");
        }

        Long categoriaId = libro.getCategoria().getId();
        Categoria categoria = em.find(Categoria.class, categoriaId);
        if(categoria == null) {
            throw new RuntimeException("No se encontró la categoría con ID: " + categoriaId);
        }
        return categoria;
    }


    public void guardar(Libro libro){
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            //Iniciar la transaccio en PostgreSQL
            tx.begin();

            // Para obtener la categoría asociada al libro y asegurarnos de que existe en la base de datos
            Categoria categoria = obetenerCategoria(em, libro);
            libro.setCategoria(categoria);

            //Decirle a Hibernate que prepare la persistencia del objeto
            em.persist(libro);
            tx.commit();

        }catch (RuntimeException ex){
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        }finally {
            em.close();
        }
    }

    public List<Libro> listar(){
        EntityManager em = JPAUtil.getEntityManager();
        try{
            return em.createQuery("SELECT l FROM Libro l JOIN FETCH l.categoria ORDER BY l.id", Libro.class).getResultList();
        }
        finally {
            em.close();
        }
    }

    public boolean modificar(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, libro.getId());

            if (libroBD != null){
                tx.rollback();
                return false;
            }

            // Para obtener la categoría asociada al libro y asegurarnos de que existe en la base de datos
            Categoria categoria = obetenerCategoria(em, libro);
            libroBD.setCategoria(categoria);


            libroBD.setTitulo(libro.getTitulo());
            libroBD.setAutor(libro.getAutor());
            libroBD.setAnio_publicacion(libro.getAnio_publicacion());
            libroBD.setPrecio(libro.getPrecio());
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, id);

            if (libroBD == null) {
                tx.rollback();
                return false;
            }

            em.remove(libroBD);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
