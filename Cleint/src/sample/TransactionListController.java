package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;

public class TransactionListController {

    public ChoiceBox<String> accNumber;
    public User user;
    public AnchorPane pane;
    public Label balance;
    public ImageView toDashboard;

    TableView<Transaction> table = new TableView<>();

    public void initialize(){
        toDashboard.setImage(new Image(new File("image/outline_arrow_back_black_36dp.png").toURI().toString()));

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
        accNumber.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setTableItems(t1);
                balance.setText(Integer.toString(user.findAccount(t1).getBalance()));
            }
        });

        TableColumn<Transaction, String> typeColumn = new TableColumn<>("نوع تراکنش");
        typeColumn.setPrefWidth(75);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(setTableFontString());

        TableColumn<Transaction, String> accColumn = new TableColumn<>("شماره حساب");
        accColumn.setPrefWidth(110);
        accColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accColumn.setCellFactory(setTableFontString());

        TableColumn<Transaction, Integer> amountColumn = new TableColumn<>("مبلغ");
        amountColumn.setPrefWidth(65);
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(setTableFontInt());

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("تاریخ");
        dateColumn.setPrefWidth(110);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(setTableFontString());

        accNumber.setValue(accNumber.getItems().get(0));

        table.getColumns().addAll(dateColumn,amountColumn,accColumn,typeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setLayoutX(40);
        table.setLayoutY(100);
        table.setPrefWidth(400);
        pane.getChildren().add(table);
    }

    public Callback setTableFontString(){
        return new Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> () {
            @Override
            public TableCell<Transaction, String> call(TableColumn<Transaction, String> transactionStringTableColumn) {
                return new TableCell<Transaction, String>()
                {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setFont(new Font("file:fonts/B Koodak Bold.TTF",16));
                        setText(empty ? "" : item);
                        setAlignment(Pos.CENTER);
                    }
                };
            }
        };
    }
    public Callback setTableFontInt(){
        return new Callback<TableColumn<Transaction, Integer>, TableCell<Transaction, Integer>> () {
            @Override
            public TableCell<Transaction, Integer> call(TableColumn<Transaction, Integer> transactionStringTableColumn) {
                return new TableCell<Transaction, Integer>()
                {
                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setFont(new Font("file:fonts/B Koodak Bold.TTF",16));
                        setText(empty ? "" : Integer.toString(item));
                        setAlignment(Pos.CENTER);
                    }
                };
            }
        };
    }

    public void setTableItems(String accountNumber){
        table.setItems(getObservableTransaction(accountNumber));
        table.refresh();
    }

    public ObservableList<Transaction> getObservableTransaction(String accountNumber){
        ObservableList<Transaction> list = FXCollections.observableArrayList();
        for (Transaction t: user.findAccount(accountNumber).getTransactions()){
            list.add(t);
        }
        return list;
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
