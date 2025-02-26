module com.example.su_feeds {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.chudasama.sufeeds to javafx.fxml;
    exports com.chudasama.sufeeds;
}
