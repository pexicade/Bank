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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegistrationController {

    public ImageView bigPic;
    private Stage stage;
    private Scene scene;
    public AnchorPane signupForm;
    public Label errorLabel;
    public TextField fNameText,lNameText,emailText,usernameText,passwordText,codeText,phoneText;
    Pattern emailPattern, passwordPattern, usernamePattern;
    public void initialize() {
        bigPic.setImage(new Image(new File("image/bigPic.jpg").toURI().toString()));
        errorLabel.setVisible(false);
        for(Node n: signupForm.getChildren()){
            if (n instanceof Label){
                setFont((Label)n,"B Koodak Bold.ttf",17.0);
            }else if(n instanceof TextField){
                setFont((TextField) n,"palanquindark-regualr.ttf",22.0);
            }else if(n instanceof Button) {
                setFont((Button) n, "B Nazanin.ttf", 27.0);
            }
        }
        emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        usernamePattern = Pattern.compile("^(?=.{5,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$\n");
    }

    public void setFont(Label lbl, String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        Font f = Font.font(fontName, FontWeight.BOLD,size);
        lbl.setFont(font);
    }
    public void setFont(TextField text, String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        text.setFont(font);
    }

    public void setFont(Button btn, String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        btn.setFont(font);
    }

    @FXML
    public void toLoginPage(MouseEvent event){
        errorLabel.setVisible(false);
        for(Node node: signupForm.getChildren()){
            if(node instanceof TextField){
                if (checkError((TextField) node,node)){
                    System.out.println("yes");
                    break;
                }
            }
        }
        if(!errorLabel.isVisible()){
            if(Main.isConnected){
                try {
                    registerRequest();
                }catch (IOException e){
                    System.out.println("couldn't send the msg: "+e.getMessage());
                    setError("?????? ???? ?????????? ?????????????? ???? ????????");
                }
            }else{
                try {
                    Main.connection = new Connection(8000);
                    System.out.println("CONNECTED");
                    Main.isConnected = true;
                    registerRequest();
                } catch(IOException e) {
                    System.out.println("ERR: Couldn't connect to server");
                    setError("?????????? ???? ???????? ???????? ????????!");
                }
            }
        }
    }

    private void registerRequest() throws IOException{
        String data = "signup:"+emailText.getText()+":"+usernameText.getText()+":"+passwordText.getText()+":"
            + fNameText.getText()+":"+lNameText.getText()+":"+codeText.getText()+":"+phoneText.getText();
        Main.connection.sendText(data);
        String res = Main.connection.getText();
        System.out.println("result: "+res);
        if(res.equals("?????? ?????? ???? ???????????? ?????????? ????.")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,res,ButtonType.OK);
            alert.setTitle("????????");
            alert.show();
        }
    }

    private boolean checkError(TextField field, Node node) {
        Label label = (Label) signupForm.getChildren().get(signupForm.getChildren().indexOf(node)-1);
        String msg, txt = field.getText(), lblText = label.getText();

        if(txt.isEmpty() || txt.isBlank()){
            msg =  lblText;
            msg += " ?????????? ???????? ????????";
            setError(msg);
            return true;
        }
        if(txt.length()<3){
            setError(lblText + " ???????? ?????????? ?????????? 3 ?????????????? ????????");
            return true;
        }
        if(lblText.equals("???? ??????")){
            if(txt.length()!=10){
                setError("???? ?????? ???????? ?????? ???????????? ??????");
                return true;
            }
        }

        if(lblText.equals("??????????")){
            if(!emailPattern.matcher(txt).matches()){
                setError("???????? ?????????? ???????????? ??????");
                return true;
            }
        }

//        if(lblText.equals("?????? ????????????")){
////            if(txt.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-z]{4,}$")){
//            if(!usernamePattern.matcher(txt).matches()){
//                msg = " ?????? ???????????? ???????? ?????????? ?????????? 5 ?????????????? ???????? ???????? ?????????????? ?? ?????????? ????????";
//                setError(msg);
//                return true;
//            }
//        }

        if(lblText.equals("?????? ????????")){
            if(!passwordPattern.matcher(txt).matches()){
                msg = "?????? ???????? ???????? ?????????? ?????????? 8 ?????????????? ???????? ?????????? ?????? ???? ???????? ?????????????? ???????? ?? ?????????? ???? ?????? ?? ?????? ???? ?????????????? ?????? @ $ ! % * ? & ????????";
                setError(msg);
                return true;
            }
        }

        if (lblText.equals("?????????? ????????")){
            if(!txt.matches("//d+")){
                setError("?????????? ???????? ???????????? ???? ????????.");
                return true;
            }
        }

        return false;
    }

    public void setError(String msg){
//        errorLabel.setText(msg);
//        errorLabel.setVisible(true);
        Alert alert = new Alert(Alert.AlertType.ERROR,msg,ButtonType.OK);
        alert.setTitle("??????!");
        alert.show();
    }

    @FXML
    public void goToLogin(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
