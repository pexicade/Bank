package sample;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Account implements Serializable {

    private String accNumber, accPassword,date, type,active;
    private int balance;
    private List<Transaction> transactions;
    public Account(String accNumber, String accPassword,String type, String date,String balance,String active) {
        this.accNumber = accNumber;
        this.accPassword = accPassword;
        this.type = type;
        this.date = date;
        this.balance = Integer.parseInt(balance);
        this.active = active;
        transactions = new ArrayList<>();
    }

    public Account(String accPassword,String type){
        int a = (int)(Math.random()*10000)+15847;
        int b = (int)(Math.random()*10000)+15846;
        this.accNumber = a +Integer.toString(b);
        this.accPassword = accPassword;
        this.type = type;
        this.date = LocalDate.now().toString();
        this.balance = 1000;
        this.active = "active";
    }

    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public void addAllTransactions(List<Transaction> t){
        transactions.addAll(t);
    }

    public List<Transaction> getTransactions(){
        return transactions;
    }



    public List<Transaction> findTransactionByDate(String value){
        List<Transaction> tr = new ArrayList<>();
        for (Transaction t: transactions) {
            if(t.getDate().equals(value)){
                tr.add(t);
            }
        }
        return tr;
    }

    public List<Transaction> findTransactionByType(String value){
        List<Transaction> tr = new ArrayList<>();
        for (Transaction t: transactions) {
            if(t.getType().equals(value)){
                tr.add(t);
            }
        }
        return tr;
    }

    public List<Transaction> findTransactionByAmount(int value){
        List<Transaction> tr = new ArrayList<>();
        for (Transaction t: transactions) {
            if(t.getAmount()==value){
                tr.add(t);
            }
        }
        return tr;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public String getAccPassword() {
        return accPassword;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public int getBalance() {
        return balance;
    }

    public String getActive() {
        return active;
    }

    public void setBalance(int amount){
        balance = amount;
    }

    public void deactivate(){
        active = "false";
    }
}
