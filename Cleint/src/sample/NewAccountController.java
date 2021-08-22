package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class NewAccountController {
    public ImageView Background,outline_credit_score_white,toDashboard;
    public PasswordField passwordText,passwordConfirm;
    public Label error;
    public Button submit;
    public ChoiceBox<String> accType;

    public void initialize() {
        error.setVisible(false);
        Background.setImage(new Image(new File("image/Black-Purple-Wallpaper-55+-Group-Wallpapers.jpg").toURI().toString()));
        outline_credit_score_white.setImage(new Image(new File("image/outline_credit_score_white_24dp.png").toURI().toString()));
        toDashboard.setImage(new Image(new File("image/outline_arrow_back_white_48dp.png").toURI().toString()));
    }

    public void createNewAccount(MouseEvent event) {
        error.setVisible(false);
        if(passwordText.getText().isBlank() || passwordText.getText().isEmpty() || passwordConfirm.getText().isBlank() || passwordConfirm.getText().isEmpty()){
            error.setText("خطا: لطفا رمز خود را وارد کنید.");
            error.setVisible(true);
            return;
        }
        if(!passwordText.getText().equals(passwordConfirm.getText())){
            error.setText("خطا: رمز ها یکسان نیستند!");
            error.setVisible(true);
            return;
        }

        if(passwordText.getText().length()<4){
            error.setText("خطا: رمز باید حداقل دارای 4 کاراکتر باشد!");
            error.setVisible(true);
            return;
        }

        if (Main.isConnected){
            try{
                NewAccountRequest();
            }catch (IOException e){
                System.out.println("ERR: "+e.getMessage());
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.NONE,e.getMessage(),ButtonType.OK);
                alert.setTitle("Error1");
                Main.isConnected = false;
                alert.show();
            }
        }else{
            try {
                Main.connection = new Connection(8000);
                System.out.println("CONNECTED");
                Main.isConnected = true;
                NewAccountRequest();
            } catch(IOException e) {
                System.out.println("ERR: Couldn't connect to server");
                Alert alert = new Alert(Alert.AlertType.NONE,"ERR: Couldn't connect to server", ButtonType.OK);
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    public void NewAccountRequest() throws  IOException{
        String req = "addAccount:"+passwordText.getText()+":"+getType();
        Main.connection.sendText(req);
        String res = Main.connection.getText();
        System.out.println("res: "+res);
        Alert alert;
        if (res.equals("حساب کاربری با موفقیت اضاقه شد")){
            alert = new Alert(Alert.AlertType.INFORMATION, res, ButtonType.OK);
            alert.setTitle("توجه");
        }else{
            alert = new Alert(Alert.AlertType.ERROR, res, ButtonType.OK);
            alert.setTitle("خطا");
        }
        alert.show();

    }

    public String getType(){
        System.out.println("val: "+accType.getValue());
        switch (accType.getValue()){
            case "حساب سپرده بلند مدت" -> {
                return "long-term saving";
            }
            case "حساب سپرده کوتاه مدت" ->{
                return "short-term saving";
            }
            case "حساب جاری"-> {
                return "checking";
            }
            default -> {return "";}
        }
    }

    @FXML
    public void toDashboardPage(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


//    @FXML
//    public void goToLogin(javafx.scene.input.MouseEvent event) throws IOException {
//        Parent root1 = FXMLLoader.load(getClass().getResource("Login.fxml"));
//        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root1);
//        stage.setScene(scene);
//        stage.show();
//    }
}

