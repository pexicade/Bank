package sample;

import com.sun.javafx.scene.shape.ObservableFaceArrayImpl;
import javafx.collections.ObservableArray;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.scene.image.ImageView ;

import java.io.File;
import java.io.IOException;

public class TransferController {
    private Stage stage;
    private Scene scene;
    public ImageView exitPic,paymentIcon, toDashboard;
    public Label error;
    public TextField amount,targetAccNumber;
    public PasswordField accPassword;
    public ChoiceBox<String> accNumber;
    public TextArea desc;
    private User user;

    public void initialize(){
        paymentIcon.setImage(new Image(new File("image/outline_payments_white_24dp.png").toURI().toString()));
        toDashboard.setImage(new Image(new File("image/outline_arrow_back_white_48dp.png").toURI().toString()));
        error.setVisible(false);
        if(Main.isConnected){
            try {
                getUser();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Main.connection = new Connection(8000);
                System.out.println("CONNECTED");
                Main.isConnected = true;
                getUser();
            }catch(IOException | ClassNotFoundException e) {
                System.out.println("ERR: Couldn't connect to server or get User Object");
                Alert alert = new Alert(Alert.AlertType.NONE,e.getMessage(), ButtonType.OK);
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
        if(accNumber.getItems().size()==0){
            System.out.println("no account");
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

    public void goToDashboard(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void Clicked(MouseEvent event) {
        System.out.println("acc: "+accNumber.getValue());
        if (accNumber.getValue().isEmpty() || accNumber.getValue().isBlank()){
            error.setText("حساب مبدا نامعتبر است");
            error.setVisible(true);
        }else if (accPassword.getText().isEmpty() || accPassword.getText().isBlank()){
            error.setText("رمز کارت را وارد کنید");
            error.setVisible(true);
        }else if (targetAccNumber.getText().isEmpty() || targetAccNumber.getText().isBlank()){
            error.setText("حساب مقصد نامعتبر است");
            error.setVisible(true);
        }else if (amount.getText().isEmpty() || amount.getText().isBlank()){
            error.setText("مبلغ را وارد کنید");
            error.setVisible(true);
        }else{
            if (Main.isConnected){
                try{
                    transferRequest();
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
                    transferRequest();
                } catch(IOException e) {
                    System.out.println("ERR: Couldn't connect to server");
                    Alert alert = new Alert(Alert.AlertType.NONE,"ERR: Couldn't connect to server", ButtonType.OK);
                    alert.setTitle("Error");
                    alert.show();
                }
            }

        }
    }

    public void transferRequest()throws  IOException{
        if (desc.getText().isBlank() || desc.getText().isEmpty())desc.setText("null");
        String req = "transfer:"+accNumber.getValue()+":"+accPassword.getText()+":"+targetAccNumber.getText()
                +":"+amount.getText()+":"+desc.getText();
        Main.connection.sendText(req);
        String res = Main.connection.getText();
        System.out.println("res: "+res);
        if (res.equals("انتقال وجه با موفقیت انجام شد.")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,res, ButtonType.OK);
            alert.setTitle("توجه");
            alert.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR,res,ButtonType.OK);
            alert.setTitle("خطا");
            alert.show();
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
}
