package sample;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Parser {
    private List<HashMap<String,String[]>> items = new ArrayList<>();
    private Scanner input;
    private boolean inside = false;
    private File file;
    public Parser(File file) throws FileNotFoundException,WrongFormat{
        if (!file.exists()) throw new FileNotFoundException("No such file exists");
        this.file = file;
        input = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while (input.hasNextLine()){
            lines.add(input.nextLine().trim());
        }
        parse(lines);
    }

    private void parse(List<String> lines) throws WrongFormat{
        int index1,index2, lineNumber=0;
        HashMap<String,String[]> map = new HashMap<>();
        for(String line: lines){
            lineNumber++;
            if( (index1=line.indexOf(":"))!=-1 ) {
                if ((index2 = line.indexOf(";")) != -1) {
                    String val = line.substring(index1 + 1, index2).trim();
                    String key = line.substring(0, index1).trim();
                    if (map.containsKey(key)){
                        int size =map.get(key).length;
                        String[] newVals = new String[size+1];
                        String[] oldVals = map.get(key);
                        for(int i=0; i<size; i++){
                            newVals[i] = oldVals[i];
                        }
                        newVals[size] = val;
                        map.replace(key,newVals);
                    }else{
                        map.put(key,new String[]{val});
                    }
                }else{
                    throw new WrongFormat("No semicolon found on line " + lineNumber);
                }

            }else if(line.equals("{")){
//                System.out.println("{: "+lineNumber);
                map = new HashMap<>();
            }else if(line.equals("}")){
//                System.out.println("}: "+lineNumber);
                items.add(map);
            }
        }
    }

    public void update() throws FileNotFoundException, WrongFormat {
        input = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while (input.hasNextLine()){
            lines.add(input.nextLine().trim());
        }
        items.clear();
        parse(lines);
    }

    public void toObject(Class cls) throws NoSuchMethodException {
        System.out.println("name: "+cls.getName());
        for(Constructor con: cls.getConstructors()){
            for(Parameter p: con.getParameters()){
                System.out.println(p.getName());
            }
//            con.get
        }
        for(Method m: cls.getMethods()){
            System.out.println(m.getName()+": ");
            for(Class c:  m.getParameterTypes()){
                System.out.println("\t"+c.getName());
            }
        }
    }

    public boolean simpleSearcher(String searchKey, String searchVal){
//        this is used for items with only one occurrence
        boolean keyNotFound = true;
        boolean res = false;
        List<String> values = simpleSearcher(searchKey);
        if(values==null){
            return false;
        }else{
            for(String str: values){
                if (str.equals(searchVal)){
                    res = true;
                    break;
                }
            }
            return res;
        }
    }

    public List<String> simpleSearcher(String searchKey){
        boolean keyNotFound = true;
        List<String> list = new ArrayList<>();
        for(HashMap<String, String[]> item: items) {
            if (item.containsKey(searchKey)) {
                keyNotFound = false;
                list.add(item.get(searchKey)[0]);
            }
        }

        if(keyNotFound){
            return null;
        }else{
            return list;
        }
    }

    public String findWhere(String key1, String value1, String key2){
        String res = "None";
        for(HashMap<String, String[]> item: items) {
            if(item.get(key1)[0].equals(value1)){
                res = item.get(key2)[0];
                break;
            }
        }
        return res;
    }

    public String updateWhere(String key1, String value1, String key2, String value2){
        String res = "None";
        System.out.println("UPDATE v1: "+value1+", v2: "+value2);
        int n=0,m=0;
        for(HashMap<String, String[]> item: items) {
            n++;
            if(item.get(key1)[0].equals(value1)){
                item.get(key2)[0] = value2;
//                if(Integer.parseInt(item.get("number_of_accounts")[0])>1) m = Integer.parseInt(item.get("number_of_accounts")[0])-1;
                res = "Done";
                break;
            }
        }

        //since only the accounts.bd file needs update we only need one formula to calculate the line where update in intended
        if(res.equals("None")) return res;

        if(!key2.equals("balance") && !key2.equals("active")){
//            int lineNumber = (n-1)*13+n+11;
//            if(n!=1) lineNumber += m;
            System.out.println("RESIDM");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String data="";
                String line="",line1;
                int lineNumber = 0;
                int index1 = 0;
                boolean pass = false,there = false,finished = false;
//                data += bufferedReader.readLine()+"\n";// we start from the second line
                while ( (line=bufferedReader.readLine())!=null ){
                    lineNumber++;
                    if (pass) {
                        data += line+"\n";
                        if(!finished && line.trim().equals("{")) pass = false;
                        continue;
                    }
                    if( (index1= line.strip().indexOf(":"))!=-1 ){
                        line1= line.strip();
                        if(there){
                            System.out.println(line1.substring(0,index1));
                            if(line1.substring(0,index1).equals(key2)){
                                System.out.println("line number: "+lineNumber);
                                if(key2.equals("acc_number")){
                                    System.out.println("a: "+line1.substring(index1+1).strip());
                                    if(line1.substring(index1+1).strip().equals("null;")){
                                        data += "\t"+key2+": "+value2+";\n";
                                    }else{
                                        data += "\t"+key2+": "+value2+";\n"+line+"\n";
                                    }
                                }else{
                                    data += "\t"+key2+": "+value2+";\n";
                                }
                                System.out.println("Peydash shod: "+lineNumber);
                                there = false;
                                finished = true;
                            }else{
                                data += line+"\n";
                            }
                        }else{
                            line1 = line.strip();
                            if(line1.substring(0,index1).equals(key1)){
//                                System.out.println("a: "+line1.substring(0,index1));
//                                System.out.println("b: "+line1.substring(index1+1,line1.indexOf(";")).strip());
                                if(line1.substring(index1+1,line1.indexOf(";")).strip().equals(value1)){
//                                    System.out.println("avvli :"+lineNumber);
                                    there = true;
                                    pass = false;
                                }else{
                                    pass = true;
                                }
                            }
                            data += line+"\n";
                        }
                    }else if(!pass){
                        data += line+"\n";
                    }
                }
                bufferedReader.close();
                try {
                    Main.fileSaver(data,"src/users.bd",false);
                    res = "Done";
                }catch (IOException e){
                    e.printStackTrace();
                    res = "Error in server/saving";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            int lineNumber = (n-1)*9+n+6;
            String data="";
            if (key2.equals("active")) lineNumber++;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                for (int i = 0; i < lineNumber; i++) {
                    data += bufferedReader.readLine()+"\n";
                }
                data += "\t"+key2+": "+value2+";\n";
                String line;
                bufferedReader.readLine();//to pass the line that we update
                while ((line=bufferedReader.readLine())!=null){
                    data += line+"\n";
                }
                bufferedReader.close();
                try {
                    Main.fileSaver(data,"src/accounts.bd",false);
                    res = "Done";
                }catch (IOException e){
                    e.printStackTrace();
                    res = "Error in server/saving";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                res = "Error in server/reading";
            }
        }


        return res;
    }



    public User getUser(String username,Parser accParser, Parser transactionParser){
        User user = null;
        for(HashMap<String, String[]> item: items) {
                if(item.get("username")[0].equals(username)){
                    user = new User( Integer.parseInt(item.get("id")[0]) ,item.get("first_name")[0],item.get("last_name")[0],item.get("username")[0],item.get("password")[0],
                            item.get("email")[0],item.get("date")[0],item.get("phone")[0],item.get("code")[0],item.get("number_of_accounts")[0]);
                    if (!item.get("number_of_accounts")[0].equals("0")){
                        for (int i = 0; i < Integer.parseInt(item.get("number_of_accounts")[0]); i++) {
                            user.addAccount(getAccount(item.get("acc_number")[i],accParser,transactionParser));
                        }
                    }

                    break;
                }
        }
        return user;
    }

    public Account getAccount(String accountNumber,Parser parser, Parser transactionParser){
        Account acc = null;
        for(HashMap<String, String[]> item: parser.getItems()) {
            if(item.get("acc_number")[0].equals(accountNumber)){
                acc = new Account(item.get("acc_number")[0],item.get("acc_pass")[0],item.get("type")[0],
                        item.get("date")[0],item.get("balance")[0],item.get("active")[0]);
                acc.addAllTransactions(getTransaction(accountNumber,transactionParser));
                break;
            }
        }
        return acc;
    }

    public List<Transaction> getTransaction(String accNumber, Parser parser){
        List<Transaction> list = new ArrayList<>();
        Transaction transaction = null;
        for(HashMap<String, String[]> item: parser.getItems()) {
            if(item.get("acc_number")[0].equals(accNumber)){
                transaction = new Transaction(item.get("type")[0],item.get("acc_number")[0],item.get("date")[0],item.get("amount")[0]);
                switch (item.get("type")[0]){
                    case "transfer":
                        if(item.containsKey("target_acc_number")){
                            transaction.addOthers("to",item.get("target_acc_number")[0],item.get("desc")[0]);
                        }else{
                            transaction.addOthers("from",item.get("from_acc_number")[0]);
                        }
                        break;
                    case "billing":
                        transaction.addOthers(item.get("bill_id")[0],item.get("pay_id")[0]);
                        break;
                    case "getLoan":
                        transaction.addOthers(item.get("payment")[0],item.get("desc")[0]);
                        break;
                }
                list.add(transaction);
            }
        }
        return list;
    }

    public List<HashMap<String,String[]>> getItems(){
        return items;
    }
    public int size(){
        return items.size();
    }
}

class WrongFormat extends Exception{
    public WrongFormat(String message){
        super(message);
    }
}