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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PayBillController {

    public ChoiceBox<String> accNumber;
    public ImageView toDashboard,qrCodeIcon;
    public User user;
    public PasswordField accPassword;
    public TextField amount,billId,payId;
    public Label error;

    public void initialize(){
        toDashboard.setImage(new Image(new File("image/outline_arrow_back_white_48dp.png").toURI().toString()));
        qrCodeIcon.setImage(new Image(new File("image/outline_qr_code_white_48dp.png").toURI().toString()));

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
        if(user!=null){
            for (Account acc: user.getAccounts()){
                if(acc.getActive().equals("true")){
                    accNumber.getItems().add(acc.getAccNumber());
                }
            }
        }else{
            System.out.println("ERROR");
        }
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

    public void submit(MouseEvent event){
        System.out.println("acc: "+accNumber.getValue());
        if (accNumber.getValue().isEmpty() || accNumber.getValue().isBlank() || accNumber.getValue().length()<10){
            error.setText("حساب مبدا نامعتبر است");
            error.setVisible(true);
        }else if (accPassword.getText().isEmpty() || accPassword.getText().isBlank()){
            error.setText("رمز کارت را وارد کنید");
            error.setVisible(true);
        }else if (billId.getText().isEmpty() || billId.getText().isBlank()){
            error.setText("شناسانه قبض را وارد کنید");
            error.setVisible(true);
        }else if (payId.getText().isEmpty() || payId.getText().isBlank()){
            error.setText("شناسه پرداخت را وارد کنید!");
            error.setVisible(true);
        }else if (amount.getText().isEmpty() || amount.getText().isBlank()){
            error.setText("مبلغ را وارد کنید");
            error.setVisible(true);
        }else{
            if (Main.isConnected){
                try{
                    payBillRequest();
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
                    payBillRequest();
                } catch(IOException e) {
                    System.out.println("ERR: Couldn't connect to server");
                    Alert alert = new Alert(Alert.AlertType.NONE,"ERR: Couldn't connect to server", ButtonType.OK);
                    alert.setTitle("Error");
                    alert.show();
                }
            }
        }
    }

    public void payBillRequest() throws IOException{
        String req = "payBill:"+accNumber.getValue()+":"+accPassword.getText()+":"+billId.getText()
                +":"+payId.getText()+":"+amount.getText();
        Main.connection.sendText(req);
        String res = Main.connection.getText();
        System.out.println("res: "+res);
        Alert alert;
        if (res.equals("پرداخت قبض با موفقیت انجام شد.")){
            alert = new Alert(Alert.AlertType.INFORMATION, res, ButtonType.OK);
            alert.setTitle("توجه");
        }else{
            alert = new Alert(Alert.AlertType.ERROR, res, ButtonType.OK);
            alert.setTitle("خطا");
        }
        alert.show();
    }

    @FXML
    public void toDashboardPage(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
