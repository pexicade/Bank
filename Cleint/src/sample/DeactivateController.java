package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.image.ImageView ;
import java.io.File;
import java.io.IOException;

public class DeactivateController {

    public BorderPane pane;
    public ImageView img, toDashboard;
    public PasswordField accPassword;
    public Label error, balance,balanceLabel, targetAccLabel;
    public ChoiceBox<String> accNumber;
    public TextField targetAccNumber;
    private User user;
    private String amount;

    public void initialize(){
        img.setImage(new Image(new File("image/outline_credit_card_off_white_24dp.png").toURI().toString()));
        toDashboard.setImage(new Image(new File("image/outline_arrow_back_white_48dp.png").toURI().toString()));
        error.setVisible(false);
        targetAccNumber.setDisable(true);
        targetAccLabel.setDisable(true);
        if(Main.isConnected){
            try {
                getUser();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            try{
                Main.connection = new Connection(8000);
                System.out.println("CONNECTED");
                Main.isConnected = true;
                getUser();
            }catch(IOException | ClassNotFoundException e) {
                System.out.println("ERR: Couldn't connect to server or get User Object");
                Alert alert = new Alert(Alert.AlertType.NONE,"حطایی در ارتباط به سرور رخ داده است.\nلطفن از اتصال خود به سرور مطمئن شوید", ButtonType.OK);
                alert.setTitle("Error");
                alert.show();
            }
        }
        if(user !=null){
            for (Account acc: user.getAccounts()){
                if(acc.getActive().equals("true")){
                    accNumber.getItems().add(acc.getAccNumber());
                }
            }
        }else{
            System.out.println("ERROR");
        }

        accNumber.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String newValue) {
                if(accNumber.getItems().size()>0){
                    Account acc = user.findAccount(newValue);
                    balance.setText(Integer.toString(acc.getBalance()));
                    amount = balance.getText();
                    if(acc.getBalance()>0){
                        targetAccLabel.setDisable(false);
                        targetAccNumber.setDisable(false);
                    }else{
                        targetAccLabel.setDisable(true);
                        targetAccNumber.setDisable(true);
                    }
                }else{
//                    showMsg();
                }
            }
        });
        accNumber.setValue(accNumber.getItems().get(0));
    }

    public void getUser() throws IOException , ClassNotFoundException{
        Main.connection.sendText("getUser");
        String res = Main.connection.getText();
        System.out.println(res);
        if (res.equals("ok")){
            user = (User) Main.connection.getObject();
        }
    }

    public void Clicked(MouseEvent event) {
        if(Main.connection !=null && Main.connection.isConnected()){
            if (accNumber.getValue().isEmpty()|| accNumber.getValue().isBlank()){
                error.setText("حسابی را انتخاب کنید");
                error.setVisible(true);
            }

            else if (accPassword.getText().isEmpty() || accPassword.getText().isBlank()){
                error.setText("رمز کارت را وارد کنید");
                error.setVisible(true);
            }

            else{
                try {
                    Deactivate(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void Deactivate(MouseEvent event) throws IOException {
        String req;
        if(amount.equals("0")){
            req = "deactivate:"+accNumber.getValue()+":"+accPassword.getText()+":0";
        }else{
            req = "deactivate:"+accNumber.getValue()+":"+accPassword.getText()+":"+targetAccNumber.getText();
        }
        Main.connection.sendText(req);
        String res = Main.connection.getText();
        System.out.println(res);
        Alert alert = new Alert(Alert.AlertType.NONE,res,ButtonType.OK);
        if(res.equals("حساب شما با موفقیت مسدود شد")){
            accNumber.getItems().remove(accNumber.getValue());
            if(accNumber.getItems().size()>0){
                accNumber.setValue(accNumber.getItems().get(0));
            }else{
                try {
                    toDashboardPage(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        alert.setTitle("توجه");
        alert.show();
    }

    @FXML
    public void toDashboardPage(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
