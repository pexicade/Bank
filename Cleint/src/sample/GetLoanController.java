package sample;

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

public class GetLoanController {

    public User user;
    public ImageView toDashboard,accountBalanceIcon;
    public Label error;
    public TextField amount;
    public ChoiceBox<String> accNumber,dovre;
    public PasswordField accPassword;
    public TextArea desc;
    public void initialize(){
        accountBalanceIcon.setImage(new Image(new File("image/outline_account_balance_white_24dp.png").toURI().toString()));
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
        dovre.setValue(dovre.getItems().get(0));
        if(user !=null){
            for (Account acc: user.getAccounts()){
                if(acc.getActive().equals("true")){
                    accNumber.getItems().add(acc.getAccNumber());
                }
            }
            accNumber.setValue(accNumber.getItems().get(0));
        }else{
            System.out.println("ERROR");
        }
    }

    public void Clicked(MouseEvent event) {

        if (accPassword.getText().isEmpty() || accPassword.getText().isBlank()){
            error.setText("رمز را وارد کنید");
            error.setVisible(true);
            return;
        }
        if (amount.getText().isEmpty() || amount.getText().isBlank() ){
            error.setText("مبلغ را وارد کنید");
            error.setVisible(true);
            return;

        }

        if (Main.isConnected){
            try{
                loanRequest();
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
                loanRequest();
            } catch(IOException e) {
                System.out.println("ERR: Couldn't connect to server");
                Alert alert = new Alert(Alert.AlertType.NONE,"ERR: Couldn't connect to server", ButtonType.OK);
                alert.setTitle("Error");
                alert.show();
            }
        }

    }

    public void loanRequest() throws  IOException{
        int payment = Integer.parseInt(amount.getText())/((dovre.getItems().indexOf(dovre.getValue())+1)*6);
        float payment1 = Float.parseFloat(amount.getText())/((dovre.getItems().indexOf(dovre.getValue())+1)*6);
        if(payment1!=payment){
            error.setText("مبلغ وام را طوری انتخاب کنید که بر دوره پرداخت بخش پذیر باشد");
            error.setVisible(true);
            return;
        }
        System.out.println((dovre.getItems().indexOf(dovre.getValue())+1)*6);
        if (desc.getText().isBlank() || desc.getText().isEmpty())desc.setText("null");
        String req = "getLoan:"+accNumber.getValue()+":"+accPassword.getText()+":"
                +amount.getText()+":"+payment+":"+desc.getText();
        Main.connection.sendText(req);
        String res = Main.connection.getText();
        System.out.println("res: "+res);
        Alert alert;
        if (res.equals("درخواست وام شما تایید شد.")){
            alert = new Alert(Alert.AlertType.INFORMATION, res, ButtonType.OK);
            alert.setTitle("توجه");
        }else{
            alert = new Alert(Alert.AlertType.ERROR, res, ButtonType.OK);
            alert.setTitle("خطا");
        }
        alert.show();
    }


    public void getUser() throws IOException , ClassNotFoundException{
        Main.connection.sendText("getUser");
        String res = Main.connection.getText();
        System.out.println(res);
        if (res.equals("ok")){
            user = (User) Main.connection.getObject();
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
