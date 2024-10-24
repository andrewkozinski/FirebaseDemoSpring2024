package aydin.firebasedemospring2024;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WelcomeController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

    public boolean registerUser() {

        //String email = "user222@example.com";
        //String password = "secretPassword";
        String email = emailField.getText();
        String password = passwordField.getText();

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            //Add data to Firestore
            addData(email, password);
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }

    @FXML
    void handleLoginButton() {

        String email = emailField.getText();
        String password = passwordField.getText();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        try {
            UserRecord user = auth.getUserByEmail(email);
            if(user != null) {

                if(checkUser(email, password)) {
                    //Correct email & password, let user in
                    System.out.println("Successfully logged in");
                    switchToPrimary();
                }
                else {
                    System.out.println("Invalid email or password. Please try again.");
                }

            }
        } catch (FirebaseAuthException e) {
            System.out.println("Please register before logging in");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password. Please try again.");
            alert.showAndWait();
            //throw new RuntimeException(e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addData(String email, String password) {
        DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Email", email);
        data.put("Password", password);

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }


    public boolean checkUser(String username, String password)
    {
        boolean correctPassword = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Users").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                for (QueryDocumentSnapshot document : documents)
                {
                    if(document.getData().get("Email").equals(username) && document.getData().get("Password").equals(password)) {
                        correctPassword = true;
                        break;
                    }
                }
            }
            else
            {
                System.out.println("No data");
            }
            correctPassword =true;

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return correctPassword;
    }




}
