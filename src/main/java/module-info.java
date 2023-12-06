module com.abp.clock {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.abp.clock to javafx.fxml;

    exports com.abp.clock;
}