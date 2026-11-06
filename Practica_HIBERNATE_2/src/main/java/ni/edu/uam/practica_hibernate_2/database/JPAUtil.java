package ni.edu.uam.practica_hibernate_2.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {
    private static EntityManagerFactory entityManagerFactory;

    public static synchronized void iniciar() {
        if (entityManagerFactory == null) {
            String password = System.getenv("DB_PASSWORD");

            if (password == null || password.isEmpty()) {
                throw new RuntimeException(
                        "La variable de entorno DB_PASSWORD no está configurada."
                );
            }

            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.password", password);

            entityManagerFactory =
                    Persistence.createEntityManagerFactory("bibliotecaPU", properties);
        }
    }

    public static EntityManager getEntityManager() {
        iniciar();
        return entityManagerFactory.createEntityManager();
    }

    public static synchronized void cerrar() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
