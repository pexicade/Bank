package sample;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class User implements Serializable {

    private String fName,lName,username,password,email,date,phoneNumber, codeMelli;
    private int id,accountCount;
    private List<Account> accounts;

    public User(int id,String fName, String lName, String username, String password, String email,
                String date, String phoneNumber, String codeMelli,String accountCount) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.codeMelli = codeMelli;
        this.accountCount = Integer.parseInt(accountCount);
        accounts = new ArrayList<>();
    }


    public void checkLoan(){
        StringTokenizer st = new StringTokenizer(LocalDate.now().toString(),"-",false);
        st.nextToken();//pass the year
        int currentMonth =  Integer.parseInt(st.nextToken());
        int currentDay =  Integer.parseInt(st.nextToken());
        int day=0,month=0;
        int payment=0,amount=0;
        List<Integer> months = new ArrayList<>();
        List<Integer> monthsLeft = new ArrayList<>();
        for (Account acc: getAccounts()) {
            for (Transaction t: acc.getTransactions()) {
                if(t.getType().equals("payLoan")){
                    StringTokenizer st1 = new StringTokenizer(t.getDate(),"-",false);
                    st.nextToken();
                    months.add(Integer.parseInt(st1.nextToken()));
                }else if(t.getType().equals("getLoan")){
                    StringTokenizer st1 = new StringTokenizer(t.getDate(),"-",false);
                    st1.nextToken();//pass the year
                     month = Integer.parseInt(st1.nextToken());
                     day = Integer.parseInt(st1.nextToken());
                     payment = t.getPayment();
                     amount = t.getAmount();
                }
            }
        }
        if(month!=0){
            int monthsToPay = amount/payment;
            if( currentDay>= day) monthsLeft.add(currentMonth);
            for (int i = month; i < currentMonth; i++) {
                if(!months.contains(i)){
                    monthsLeft.add(i);
                }
            }

        }
    }

    public int getId(){return id;}

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String  getCodeMelli() {
        return codeMelli;
    }

    public int getAccountCount() {
        return accountCount;
    }

    public int getActiveAccountCount(){
        int n=0;
        for(Account acc: accounts){
            if(acc.getActive().equals("true")){
                n++;
            }
        }
        return n;
    }

    public void addAccount(Account acc){
        if (accounts.size()<5){
            accounts.add(acc);
            if(accountCount<accounts.size()){
                accountCount = accounts.size();
            }
        }else{
            System.out.println("you have reached the maximum number of accounts");
        }
    }

    public Account findAccount(String accNumber){
        for(Account acc: accounts){
            if(acc.getAccNumber().equals(accNumber)){
                return acc;
            }
        }
        return null;
    }

    public List<Account> getAccounts(){
        return accounts;
    }
}