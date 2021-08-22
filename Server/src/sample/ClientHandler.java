package sample;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler extends Thread{
    private Socket socket;
    private Connection connection;
    private final int ID;
    private boolean loggedIn = false;
    public Hashtable<String, Integer> dastoors = new Hashtable<>();
    private boolean finished = false;
    private static File usersFile = new File("src/users.bd");
    private static Parser userParser;
    private static File accFile = new File("src/accounts.bd");
    private static Parser accParser;
    private static File transactionFile = new File("src/transactions.bd");
    private static Parser transactionParser;
    private static Lock lock = new ReentrantLock();
    private User user;

    public ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.ID = id;

    }

    @Override
    public void run(){
        populateDastoors();
        try {
            connection = new Connection(socket);
        } catch (IOException e) {
            System.out.println("#0 ERR: "+e.getMessage());
        }
        try{
            userParser = new Parser(usersFile);
            accParser = new Parser(accFile);
            transactionParser = new Parser(transactionFile);

        }catch (FileNotFoundException e){
            System.out.println("Err: File not found to parse"+e.getStackTrace()[0]);
            System.exit(0);
        }catch (WrongFormat e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        try {
            while(!finished){
                System.out.println("##################");
                getText();
                userParser.update();
            }
            System.out.println("Connection ended by client");
            socket.close();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("#1 msg: "+e.getMessage());
        } catch(WrongFormat e){
            System.out.println("#2 msg: "+e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("#3 msg: "+e.getMessage());
        } catch (IOException e) {
            System.out.println("#4 msg: "+e.getMessage());
        }
    }

    public void getText() throws IOException, NoSuchMethodException {
        String inputText = connection.getText();
        if (inputText.equals("finish")){
            connection.sendText("connection terminated");
            finished = true;
            return;
        }
        System.out.println(inputText);
        StringTokenizer st1 = new StringTokenizer(inputText,":",false);
        String dastoor;
        int paramCount;
        if (dastoors.containsKey(dastoor=st1.nextToken())){
            paramCount = dastoors.get(dastoor);
            System.out.println("Dastoor: "+dastoor);
            if(st1.countTokens()==paramCount){
                String res;
                switch (dastoor){
                    case "signup":
                        res = signup(st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),
                                st1.nextToken(),st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "login":
                        res = login(st1.nextToken(), st1.nextToken());
                        connection.sendText(res);
                        if(res.equals("Logged in")){
                            setUser(new StringTokenizer(inputText.substring(inputText.indexOf(":")+1),":",false).nextToken());
                        }
                        break;
                    case "logout":
                        res = logout(st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "getUser":
                        res = getUser();
                        connection.sendText(res);
                        if(res.equals("ok")){
//                            System.out.println("Number of active accounts: "+user.getActiveAccountCount());
                            connection.sendObject(user);
                        }
                        break;
                    case "addAccount":
                        res = addAccount(st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "getBalance":
                        res = getBalance(st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "transfer":
                        res = transfer(st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),100);
                        connection.sendText(res);
                        break;
                    case "payBill":
                        res = payBill(st1.nextToken(),st1.nextToken(),st1.nextToken(), st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "getLoan":
                        res = getLoan(st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                    case "deactivate":
                        res = deactivate(st1.nextToken(),st1.nextToken(),st1.nextToken());
                        connection.sendText(res);
                        break;
                }
            }else{
                System.out.println("Wrong Dastoor");
                connection.sendText("Wrong Dastoor");
            }
        }
    }

    public String login(String username,String password){
        String res;
        if(user != null){
            return "َشما از قبل وارد حساب خود شده اید.";
        }
        if(userParser.simpleSearcher("username",username)){
            if(userParser.findWhere("username",username,"password").equals(password)){
                System.out.println("Logged in successfully");
                res = "Logged in";
            }else{
                System.out.println("password is wrong: "+userParser.findWhere("username",username,"password"));
                res = "رمز عبور نادرست است!";
            }
        }else{
            System.out.println("username is wrong");
            res = "نام کاربری نادرست است!";
        }
        return res;
    }

    public String logout(String username){
        if(user!=null){
            if(user.getUsername().equals(username)){
                user = null;
                return "با موفقیت از حساب خود خارج شدید.";
            }else{
                return "نام کاربری اشتباه است!";
            }
        }else{
            return "شما وارد حسابی نشده اید.";
        }
    }

    public String signup(String email, String username,String password,String fName,String lName, String code,
                              String phone){
        String res = "";
        if(userParser.simpleSearcher("username",username)){
            System.out.println("ERR: This username is already taken");
            res = "ERR: This username is already taken";
        }else if(userParser.simpleSearcher("email",email)) {
            System.out.println("ERR: There is an existing account with this EMAIL");
            res = "ERR: There is an existing account with this EMAIL";
        }else if(userParser.simpleSearcher("code",code)){
            System.out.println("ERR:  There is an existing account with this CODE");
            res = "ERR: There is an existing account with this CODE";
        }else if(userParser.simpleSearcher("phone",phone)){
            System.out.println("ERR:  There is an existing account with this PHONE");
            res = "ERR: There is an existing account with this PHONE";
        }else{
            String data = "\n{\n\t";
            data += dataFormatter("id",Integer.toString(userParser.size()+1));
            data += dataFormatter("username",username);
            data += dataFormatter("email",email);
            data += dataFormatter("password",password);
            data += dataFormatter("date", LocalDate.now().toString());
            data += dataFormatter("first_name",fName);
            data += dataFormatter("last_name",lName);
            data += dataFormatter("code",code);
            data += dataFormatter("phone",phone);
            data += dataFormatter("number_of_accounts","0");
            data += "acc_number: null;\n";
            data += "}\n";
            try{
                Main.fileSaver(data,"src/users.bd",true);
                System.out.println("NEW user successfully signed up");
                res = "ثبت نام با موفقیت انجام شد.";
                userParser.update();
            } catch (IOException | WrongFormat e) {
                System.out.println("ERR: writing new sample.User data");
                e.printStackTrace();
                res = "خطایی در سرور هنگام نوشتن داده ها رخ داده است.";
                res += "\n"+e.getMessage();
            }
        }
        return res;
    }

    public String getUser(){
        String res;
        if(user!=null){
            res = "ok";
        }else{
            res = "not logged in";
        }
        return res;
    }

    public String getBalance(String accNumber,String accPassword){
        String res;
        Account acc = user.findAccount(accNumber);
        if(acc.getAccPassword().equals(accPassword)){
            res = Integer.toString(acc.getBalance());
        }else{
            res = "رمز عبور نادرست است";
        }
        return res;
    }
    public String addAccount(String password, String type){
        String res="";
        Account acc = new Account(password,type);
        user.addAccount(acc);
        String data = "\n{\n\t";
        data += dataFormatter("id",Integer.toString(accParser.size()+1));
        data += dataFormatter("acc_number",acc.getAccNumber());
        data += dataFormatter("acc_pass",password);
        data += dataFormatter("type",type);
        data += dataFormatter("date",acc.getDate());
        data += dataFormatter("balance","1000");
        data += "active: true;\n";
        data += "}\n";
        try {
            Main.fileSaver(data,"src/accounts.bd",true);
            res = "حساب کاربری با موفقیت اضاقه شد";
            System.out.println("حساب کاربری با موفقیت اضاقه شد.");
            userParser.updateWhere("username",user.getUsername(),"number_of_accounts",Integer.toString(user.getAccountCount()));
            userParser.updateWhere("username",user.getUsername(),"acc_number",acc.getAccNumber());
            try {
                accParser.update();
                userParser.update();
                setUser(user.getUsername());
            } catch (WrongFormat wrongFormat) {
                wrongFormat.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("ERR: writing new account data");
            e.printStackTrace();
            res = "خطایی در سرور هنگام نوشتن داده ها رخ داده است";
            res += "\n"+e.getMessage();
        }
        return res;
    }

    public String transfer(String fromCard, String pass, String toCard,String amount, String desc,int limit){
        lock.lock();
        String res,data;
        Integer amount1 = Integer.parseInt(amount);
        Account acc = user.findAccount(fromCard);
        if(acc==null){
            System.out.println("PROBLEM IN TRANSFER");
            lock.unlock();
            return "shit transfer";
        }
        int balance1 = acc.getBalance();//Integer.parseInt(userParser.findWhere("acc_number",fromCard,"balance"));
        if(!acc.getAccPassword().equals(pass)){
            lock.unlock();
            return "رمز کارت نادرست است";
        }
        if(accParser.findWhere("acc_number",toCard,"balance").equals("None")){
            lock.unlock();
            return "شماره کارت مقصد نادرست است";
        }
        if(accParser.findWhere("acc_number",toCard,"active").equals("false")){
            lock.unlock();
            return "شماره کارت مقصد غیرفعال شده است";
        }
        int balance2 = Integer.parseInt(accParser.findWhere("acc_number",toCard,"balance"));
        System.out.println("b1 : "+balance1+" b2: "+balance2+" a: "+amount1);
        if (balance1-amount1>=limit){
            String res1 = accParser.updateWhere("acc_number",fromCard,"balance",Integer.toString(balance1-amount1));
            String res2 = accParser.updateWhere("acc_number",toCard,"balance",Integer.toString(balance2+amount1));
            if(res1.equals("Done") && res2.equals("Done")){
                data = "\n{\n\t";
                data += dataFormatter("type","transfer");
                data += dataFormatter("acc_number",fromCard);
                data += dataFormatter("target_acc_number",toCard);
                data += dataFormatter("amount",amount);
                data += dataFormatter("date",(LocalDate.now().toString()));
                data += "desc: "+desc+";\n";
                data += "}\n";
                data += "\n{\n\t";
                data += dataFormatter("type","transfer");
                data += dataFormatter("acc_number",toCard);
                data += dataFormatter("from_acc_number",fromCard);
                data += dataFormatter("amount",amount);
                data += "date: "+(LocalDate.now().toString())+";\n";
                data += "}\n";
                try {
                    Main.fileSaver(data,"src/transactions.bd",true);
                    System.out.println("TRANSFER done successfully");
                    res = "انتقال وجه با موفقیت انجام شد.";
                    transactionParser.update();
                    accParser.update();
                    acc.setBalance(balance1-amount1);
                    setUser(user.getUsername());
                }catch (IOException | WrongFormat e){
                    System.out.println("ERR: writing transaction data ");
                    e.printStackTrace();
                    res = "خطایی در سرور هنگام نوشتن داده ها رخ داده است.";
                    res += "\n"+e.getMessage();
                }
            }else{
                res = "خطایی هنگام انجام عملیات رخ داد.";
            }
        }else{
            res= "موجودی کاقی نیست";
        }
        lock.unlock();
        return  res;
    }

    public String payBill(String accNumber, String accPassword,String billId, String payId, String amount){
        lock.lock();
        String res="",data;
        Account acc = user.findAccount(accNumber);
        int b=acc.getBalance()-Integer.parseInt(amount);
        if(acc.getAccPassword().equals(accPassword)) {
            if (b >0) {
                if(accParser.updateWhere("acc_number",accNumber,"balance",Integer.toString(b)).equals("Done")) {
                    data = "\n{\n\t";
                    data += dataFormatter("type", "billing");
                    data += dataFormatter("acc_number", accNumber);
                    data += dataFormatter("bill_id", billId);
                    data += dataFormatter("pay_id", payId);
                    data += dataFormatter("amount", amount);
                    data += "date: " + LocalDate.now().toString() + ";\n";
                    data += "}\n";
                    try {
                        Main.fileSaver(data, "src/transactions.bd", true);
                        System.out.println("Bill was paid successfully");
                        acc.setBalance(b);
                        accParser.update();
                        transactionParser.update();
                        setUser(user.getUsername());
                        res = "پرداخت قبض با موفقیت انجام شد.";
                    } catch (IOException | WrongFormat e) {
                        System.out.println("ERR: writing transaction data ");
                        e.printStackTrace();
                        res = "خطایی در سرور هنگام نوشتن داده ها رخ داده است.";
                        res += "\n" + e.getMessage();
                    }
                }else{
                    res = "خطایی هنگام انجام عملیات رخ داد.";
                }
            } else {
                res = "موجودی کافی نیست";
            }
        }else{
            res = "رمز کارت نادرست است.";
        }
        lock.unlock();
        return res;
    }

    public String getLoan(String accNumber,String accPass,String loanAmount,String payment,String desc){
        lock.lock();
        String res,data;
        Account acc = user.findAccount(accNumber);
        if(acc.getAccPassword().equals(accPass)){
            if(accParser.updateWhere("acc_number",accNumber,"balance",Integer.toString(acc.getBalance()+Integer.parseInt(loanAmount))).equals("Done")){
                data = "\n{\n\t";
                data += dataFormatter("type","getLoan");
                data += dataFormatter("acc_number",accNumber);
                data += dataFormatter("amount",loanAmount);
                data += dataFormatter("payment",payment);
                data += dataFormatter("month_count", Integer.toString(Integer.parseInt(loanAmount)/Integer.parseInt(payment)));
                data += dataFormatter("date",(LocalDate.now().toString()));
                data += "desc: "+desc+";\n";
                data += "}\n";
                try{
                    Main.fileSaver(data,"src/transactions.bd",true);
                    acc.setBalance(acc.getBalance()+Integer.parseInt(loanAmount));
                    System.out.println("The Loan was successfully given to the client");
                    accParser.update();
                    transactionParser.update();
                    setUser(user.getUsername());
                    res = "درخواست وام شما تایید شد.";
                }catch (IOException | WrongFormat e){
                    System.out.println("ERR: writing transaction data ");
                    e.printStackTrace();
                    res = "خطایی در سرور هنگام نوشتن داده ها رخ داده است.";
                    res += "\n" + e.getMessage();
                }
            }else{
                res = "خطایی هنگام انجام عملیات رخ داد.";
            }
        }else{
            res = "رمز کارت نادرست است.";
        }
        lock.unlock();
        return res;
    }

    public String deactivate(String accNumber, String accPassword,String targetCard){
        lock.lock();
        String res="",res1="";
        Account acc = user.findAccount(accNumber);
        if(acc.getAccPassword().equals(accPassword)){
            if (!targetCard.equals("0")){
                System.out.println("b: "+accParser.findWhere("acc_number",accNumber,"balance"));
                res1 = transfer(accNumber,accPassword,targetCard,accParser.findWhere("acc_number",accNumber,"balance")
                        ,"Transferring the money of account due to deactivation",0);
                if(res1.equals("انتقال وجه با موفقیت انجام شد.")){
                    if (accParser.updateWhere("acc_number",accNumber,"active","false").equals("Done")){
                        acc.deactivate();
                        System.out.println("a: "+acc.getActive()+" b: "+user.findAccount(accNumber).getActive());
                        System.out.println("count: "+user.getActiveAccountCount()+"");
                        try {
                            accParser.update();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (WrongFormat wrongFormat) {
                            wrongFormat.printStackTrace();
                        }finally {
                            lock.unlock();
                        }
                        setUser(user.getUsername());
                        res = "حساب شما با موفقیت مسدود شد";
                    }
                }else{
                    res = res1;
                }
            }else{
                if (accParser.updateWhere("acc_number",accNumber,"active","false").equals("Done")){
                    acc.deactivate();
                    System.out.println("a: "+acc.getActive()+" b: "+user.findAccount(accNumber).getActive());
                    System.out.println("count: "+user.getActiveAccountCount()+"");
                    try {
                        accParser.update();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        lock.unlock();
                    } catch (WrongFormat wrongFormat) {
                        wrongFormat.printStackTrace();
                        lock.unlock();
                    }
                    setUser(user.getUsername());
                    res = "حساب شما با موفقیت مسدود شد";
                }
            }
        }else{
            res = "رمز کارت نادرست است";
        }
        lock.unlock();
        return res;
    }

    private void setUser(String username){
        System.out.println("SET USER: "+username);
        user = userParser.getUser(username,accParser,transactionParser);
//        user.checkLoan();
    }

    private static String dataFormatter(String key, String value){
        return key+": "+value+";\n\t";
    }


    private void populateDastoors(){
        dastoors.put("signup",7);
        dastoors.put("login",2);
        dastoors.put("logout",1);
        dastoors.put("getUser",0);
        dastoors.put("addAccount",2);
        dastoors.put("getBalance",2);
        dastoors.put("transfer",5);
        dastoors.put("payBill",5);
        dastoors.put("getLoan",5);
        dastoors.put("payLoan",3);
        dastoors.put("deactivate",3);
    }
}
