module ni.edu.uam.practica_hibernate_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;


    opens ni.edu.uam.practica_hibernate_2 to javafx.fxml;
    exports ni.edu.uam.practica_hibernate_2;
}