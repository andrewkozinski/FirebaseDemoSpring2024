package aydin.firebasedemospring2024;

import javafx.fxml.FXML;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }



}
