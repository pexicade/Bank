package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class LoginController {


    public Pane root, loginForm;
    public ImageView bigPic;
    public Label usernameLabel,passwordLabel, loginLabel;
    public TextField usernameText, passwordText;
    private Stage stage;
    private Scene scene;

    public void initialize() {
        bigPic.setImage(new Image(new File("image/bigPic.jpg").toURI().toString()));
        for(Node n: loginForm.getChildren()){
            if (n instanceof Label){
                ((Label)n).setFont(getFont("B Koodak Bold.ttf",16.0));
//                setFont((Label)n,"B Koodak Bold.ttf",16.0);
            }else if(n instanceof TextField){
                ((TextField) n).setFont(getFont("palanquindark-regualr.ttf",18.0));
//                setFont((TextField) n,"palanquindark-regualr.ttf",18.0);
            }else if(n instanceof Button) {
                ((Button) n).setFont(getFont("B Nazanin.ttf", 22.0));
//                setFont((Button) n, "B Nazanin.ttf", 22.0);
            }
        }
    }
    public Font getFont(String fontName,Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        return font;
    }

    public void setFont(Label lbl,String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        lbl.setFont(font);
    }
    public void setFont(TextField text,String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        text.setFont(font);
    }
    public void setFont(Button btn,String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        btn.setFont(font);
    }

    @FXML
    public void login(MouseEvent event) {
        System.out.println("clicked");
        if(usernameText.getText().isEmpty() || usernameText.getText().isBlank()){
            System.out.println("username should be filled");
            Alert alert = new Alert(Alert.AlertType.ERROR,"Username should be filled",ButtonType.OK);
            alert.setTitle("خطا!");
            alert.show();
            return;
        }else if(passwordText.getText().isEmpty() || passwordText.getText().isBlank()){
            System.out.println("password should be filled");
            Alert alert = new Alert(Alert.AlertType.ERROR,"Password should be filled",ButtonType.OK);
            alert.setTitle("خطا!");
            alert.show();
            return;
        }
        if(Main.isConnected){
            try{
                loginRequest(event);
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
                loginRequest(event);
            } catch(IOException e) {
                System.out.println("ERR: Couldn't connect to server");
                Alert alert = new Alert(Alert.AlertType.NONE,"ERR: Couldn't connect to server",ButtonType.OK);
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    public void loginRequest(MouseEvent event) throws IOException {
        String data = "login:"+usernameText.getText()+":"+passwordText.getText();
        Main.connection.sendText(data);
        String res = Main.connection.getText();
        System.out.println(res);
        if(res.equals("Logged in")){
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("پنل کاربری");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.NONE,res,ButtonType.OK);
            alert.setTitle("توجه");
            alert.show();
        }
    }

    public void showDialog(String title,String msg, String buttonText){
        Dialog<String> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(245,248,249);");
        Label titleLabel = new Label(title);
        Label msgLabel = new Label(msg);
        titleLabel.setLayoutX(20);
        titleLabel.setPrefWidth(310);
        titleLabel.setLayoutY(15);
        titleLabel.setPrefHeight(25);
        titleLabel.setAlignment(Pos.CENTER_RIGHT);
        msgLabel.setLayoutX(30);
        msgLabel.setPrefWidth(290);
        msgLabel.setLayoutY(55);
        msgLabel.setPrefHeight(85);
        msgLabel.setAlignment(Pos.CENTER_RIGHT);
        Button btn = new Button(buttonText);
        btn.setLayoutX(15);
        btn.setLayoutY(145);
        btn.setPrefHeight(30);
        btn.setStyle("-fx-background-color: slateblue; -fx-text-fill: white;");
        btn.addEventFilter(ActionEvent.ANY, this::close);
        setFont(titleLabel,"B Koodak Bold.TTF",19.0);
        setFont(msgLabel,"B Koodak Outline.TTF",15.0);
        setFont(btn,"Yekan.TTF",16.0);
        msgLabel.autosize();
        msgLabel.setWrapText(true);
        pane.getChildren().addAll(titleLabel,msgLabel,btn);
        pane.setPrefWidth(350);
        pane.setPrefHeight(190);
        dialog.getDialogPane().setContent(pane);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setHeight(2);
        dropShadow.setColor(Color.BLACK);
//        dialog.getDialogPane().setEffect(dropShadow);
        dialog.getDialogPane().setStyle(
                        "-fx-background-color: rgb(245,248,249); " +
                        "-fx-background-insets: 10; " +
                        "-fx-background-radius: 2; " +
                        "-fx-effect: dropshadow(three-pass-box, purple, 10, 0, 0, 0);");

        dialog.getDialogPane().setPrefWidth(350);
        dialog.getDialogPane().setPrefHeight(190);
        dialog.setWidth(350);
        dialog.setHeight(190);
        dialog.showAndWait();
    }

    public void close(ActionEvent event){
        System.out.println("here");
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

//    private Parent root1;
    @FXML
    public void goToRegistration(MouseEvent event) throws IOException {
        System.out.println("address: "+(getClass().getResource("Registration.fxml")));
        Parent root1 = FXMLLoader.load(getClass().getResource("Registration.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root1);
        stage.setScene(scene);
        stage.setTitle("نام نویسی");
        stage.show();
    }


}
