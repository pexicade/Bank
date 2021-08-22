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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;

public class DashboardController {

    private User user;
    private Scene scene;
    public AnchorPane DashboardForm;
    private Parent root;
    public ImageView actors_black,score_black,card_off_black,qr_code_black,account_balance_black,payments_black,Purple_Wallpaper,lock_open_white;
    public Label nameLabel;

    public void initialize(){
        String res;
        if(!(res=getUser()).equals("ok")){
            Alert alert = new Alert(Alert.AlertType.ERROR,res,ButtonType.OK);
            alert.show();
        }

        lock_open_white.setImage(new Image(new File("image/outline_lock_open_white_24dp.png").toURI().toString()));
        Purple_Wallpaper.setImage(new Image(new File("image/Black-Purple-Wallpaper-55+-Group-Wallpapers.jpg").toURI().toString()));
        payments_black.setImage(new Image(new File("image/outline_payments_black_24dp.png").toURI().toString()));
        account_balance_black.setImage(new Image(new File("image/outline_account_balance_black_24dp.png").toURI().toString()));
        qr_code_black.setImage(new Image(new File("image/outline_qr_code_black_24dp.png").toURI().toString()));
        card_off_black.setImage(new Image(new File("image/outline_credit_card_off_black_24dp.png").toURI().toString()));
        score_black.setImage(new Image(new File("image/outline_credit_score_black_24dp.png").toURI().toString()));
        actors_black.setImage(new Image(new File("image/outline_recent_actors_black_24dp.png").toURI().toString()));

        if (user.getAccountCount() == 0 || user.getActiveAccountCount()==0){
            Pane msgPane = new Pane();
            msgPane.setPrefWidth(350);
            msgPane.setPrefHeight(190);
            Label titleLabel = new Label("توجه");
            Label contentLabel = new Label("شما هیچ حسابی ندارید برای ادامه باید ابتدا یک حساب بانکی ایجاد کنید");
            Button btn = new Button("باشه");
            titleLabel.setLayoutY(15);
            titleLabel.setLayoutX(15);
            titleLabel.setPrefWidth(310);
            titleLabel.setPrefHeight(20);
            titleLabel.setAlignment(Pos.CENTER_RIGHT);
            contentLabel.setLayoutY(45);
            contentLabel.setLayoutX(25);
            contentLabel.setPrefWidth(295);
            contentLabel.setPrefHeight(90);
            contentLabel.setAlignment(Pos.CENTER);
            contentLabel.setWrapText(true);
            btn.setLayoutY(145);
            btn.setLayoutX(15);
            btn.setPrefWidth(45);
            btn.setPrefHeight(35);
            btn.setAlignment(Pos.CENTER);
            btn.addEventFilter(ActionEvent.ANY, this::toNewAccount);
            msgPane.getChildren().addAll(titleLabel,contentLabel,btn);
            msgPane.setLayoutX((DashboardForm.getPrefWidth()-msgPane.getPrefWidth())/2);
            msgPane.setLayoutY((DashboardForm.getPrefHeight()-msgPane.getPrefHeight())/2);
            DropShadow dropShadow = new DropShadow();
            dropShadow.setHeight(5);
            dropShadow.setOffsetX(3);
            dropShadow.setOffsetY(4);
            dropShadow.setRadius(3);
            msgPane.setEffect(dropShadow);
            msgPane.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
            DashboardForm.getChildren().add(msgPane);
        }
        nameLabel.setText("خوش آمدید "+user.getfName()+" "+user.getlName());
        for(Node n: DashboardForm.getChildren()){
            if(n instanceof Button){
                setFont((Button)n,"B Nazanin.TTF",18.0);
            }
        }
    }

    public void setFont(Button btn,String fontName, Double size){
        fontName = "file:fonts/"+fontName;
        Font font = Font.loadFont(fontName,size);
        btn.setFont(font);
    }

    public void toNewAccount(ActionEvent event)  {
        changeScene((Stage) ((Node)event.getSource()).getScene().getWindow(),"NewAccount.fxml","ثبت حساب جدید");
    }

    public String getUser(){
        String res = "";
        try {
            Main.connection.sendText("getUser");
            String resp;
            System.out.println("respond: "+(resp = Main.connection.getText()));
            if(resp.equals("ok")){
                user = (User) Main.connection.getObject();
                res = "ok";
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            res = e.getMessage();
        }
        return res;
    }


    @FXML
    public void goToPayBills(MouseEvent event) {
        changeScene(getStage(event),"payBill.fxml","پرداخت قبض");
    }
    @FXML
    public void LoanReq(MouseEvent event) {
        changeScene(getStage(event),"getLoan.fxml","درخواست وام");
    }
    @FXML
    public void goToTransfer(MouseEvent event) {
        changeScene(getStage(event),"transfer.fxml","انتقال وجه");
    }
    @FXML
    public void goToNewAccount(MouseEvent event) {
        changeScene(getStage(event),"NewAccount.fxml","ثبت حساب جدید");
    }
    @FXML
    public void goToDeactivate(MouseEvent event) {
        changeScene(getStage(event),"Deactivate.fxml","غیرفعال کردن حساب");
    }
    @FXML
    public void toTransactionList(MouseEvent event) {
        changeScene(getStage(event),"TransactionList.fxml","لیست تراکنش ها");
    }

    public Stage getStage(MouseEvent event){
        return (Stage) ((Node)event.getSource()).getScene().getWindow();
    }

    public void changeScene(Stage stage, String fileName,String title){
        try{
            root = FXMLLoader.load(getClass().getResource(fileName));
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        }catch (IOException e){
            System.out.println("ERR: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void exitApp(MouseEvent event) {
        try {
            Main.connection.sendText("logout:"+user.getUsername());
            String res = Main.connection.getText();
            System.out.println(res);
            if(res.equals("با موفقیت از حساب خود خارج شدید.")){
                changeScene(getStage(event),"Login.fxml","ورود");
            }else{
                Alert alert = new Alert(Alert.AlertType.NONE,res,ButtonType.OK);
                alert.setTitle("توجه");
                alert.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,e.getMessage(),ButtonType.OK);
            alert.setTitle("خطا");
            alert.show();
        }
    }

}
