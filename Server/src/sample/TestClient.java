package sample;

import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Connection connection = new Connection(8000);
//        connection.sendText("login:pexi:ramz");
//        System.out.println(connection.getText());
//        connection.sendText("getBalance:01234:sdv50lo");
//        System.out.println("before balance: "+connection.getText());
////        connection.sendText("transfer:01234:sdv50lo:5412361:2600:test dige");
////        System.out.println(connection.getText());
////        connection.sendText("transfer:01234:sdv50lo:147890:2600:test dige");
////        System.out.println(connection.getText());
////        connection.sendText("transfer:01234:sdv50lo:541236:2600:test dige");
////        System.out.println(connection.getText());
//        connection.sendText("transfer:01234:sdv50lo:541236:500:test dige");
//        connection.sendText("getBalance:01234:sdv50lo");
//        System.out.println(connection.getText());
//        System.out.println("after balance: "+connection.getText());
//        connection.sendText("logout:pexi");
//        System.out.println(connection.getText());
        connection.sendText("login:fuck:you");
        System.out.println(connection.getText());
//        connection.sendText("getBalance:541236:what");
//        System.out.println("new balance: "+connection.getText());

//        connection.sendText("signup:Reza@gmail.com:Theking:Asdfgh1:Reza:Behmanesh:0958762036:123015974");
//        System.out.println(connection.getText());
//        connection.sendText("login:Theking:Asdfgh1");
//        System.out.println(connection.getText());
//        connection.sendText("addAccount:khubi2:saving");
//        System.out.println(connection.getText());
        connection.sendText("getUser");
        String res = connection.getText();
        System.out.println(res);
        if(res.equals("ok")) {
            User user = (User) connection.getObject();
            res = "info[ " + user.getId() + ", " + user.getfName() + ", " + user.getlName() + ", " + user.getEmail() + ", "
                    + user.getUsername() + ", " + user.getPassword() + ", " + user.getPhoneNumber() + ", " + user.getCodeMelli() + " ]";
            res += " accounts:[ ";
            String a = "";
            for (int i = 0; i < user.getAccountCount(); i++) {
                Account acc = user.getAccounts().get(i);
                a = acc.getAccNumber();
                res += "( " + acc.getAccNumber() + ", " + acc.getAccPassword() + ", " + acc.getType() + ", " + acc.getBalance() + ", " + acc.getDate() + " ),";
                System.out.println("t s: "+acc.getTransactions().size());
                for (int j = 0; j < acc.getTransactions().size(); j++) {
                    Transaction tr = acc.getTransactions().get(j);
                    System.out.println("h");
                    res += "{ "+tr.getType()+", "+tr.getAccountNumber() + ", " +tr.getAmount()+", "+tr.getDate() +" }" ;
                }
            }
            res += " ]";
            System.out.println(res);
            Transaction t = user.getAccounts().get(0).findTransactionByAmount(75).get(0);
            System.out.println(t.getPay_id()+", "+t.getBill_id());
        }
//            connection.sendText("logout:"+"Theking");
////            System.out.println(connection.getText());
//            connection.sendText("login:alidehghani:zdrtfc159");
//            System.out.println(connection.getText());
//            connection.sendText("getBalance:147890:thefuck");
//            System.out.println("before balance: "+connection.getText());
//            connection.sendText("transfer:147890:thefuck:"+a+":210:velemon kon baaba");
//            System.out.println(connection.getText());
//            connection.sendText("getBalance:147890:thefuck");
//            System.out.println("after balance:"+connection.getText());
//            connection.sendText("logout:"+"alidehghani");
//            System.out.println(connection.getText());
//            connection.sendText("login:Theking:Asdfgh1");
//            System.out.println(connection.getText());
//            connection.sendText("getBalance:"+a+":khubi2");
//            System.out.println("balance:"+connection.getText());
//        }
//        connection.sendText("login:alidehghani:zdrtfc159");
//        System.out.println(connection.getText());
//        connection.sendText("payBill:147890:thefuck:1234:9876:55");
//        System.out.println(connection.getText());
//        connection.sendText("getUser");
//        String res = connection.getText();
//        System.out.println(res);
//        if(res.equals("ok")){
//            sample.User user = (sample.User) connection.getObject();
//            System.out.println("b: "+user.findAccount("147890").getBalance());
//        }
//        connection.sendText("getBalance:147890:thefuck");
////        System.out.println("balance: "+connection.getText());
//        connection.sendText("login:Theking:Asdfgh1");
//        System.out.println(connection.getText());
////        String b =
//        connection.sendText("deactivate:2066820993:khubi2:74026");
//        System.out.println(connection.getText());

//        connection.sendText("login:pexi:ramz");
//        System.out.println(connection.getText());
//        connection.sendText("getLoan:01234:5000:250:vam mikham dige ");
//        System.out.println(connection.getText());
//        connection.sendText("getBalance:01234:sdv50lo");
//        System.out.println(connection.getText());
        connection.sendText("finish");
        System.out.println(connection.getText());
//        testing("helllo","nooo","waht the duck");
    }

    public static void testing(String... args){
        for (String s:args) {
            System.out.println(s);
        }
    }
}
