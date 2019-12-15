module org.lisoft.lsml {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires javax.inject;
    
    requires java.desktop;

    exports org.lisoft.lsml.view_fx;

    opens org.lisoft.lsml.view_fx.controllers to javafx.fxml;
}
