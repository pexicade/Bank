package sample;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String accountNumber,type,date;
    private int amount,payment;
    private String target_acc_number, from_acc_number, bill_id, pay_id, desc;
    private String currentMonth, currentDay;

    public Transaction(String type, String accountNumber, String date, String amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.date = date;
        this.amount = Integer.parseInt(amount);
    }

    public void addOthers(String... args){
        switch (type) {
            case "transfer" -> {
                if (args[0] == "to") {
                    target_acc_number = args[1];
                    desc = args[2];
                } else {
                    from_acc_number = args[1];
                }
            }
            case "billing" -> {
                bill_id = args[0];
                pay_id = args[1];
            }
            case "getLoan" -> {
                payment = Integer.parseInt(args[0]);
                desc = args[1];
            }
//            case "payLoan":
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public String getTarget_acc_number() {
        return target_acc_number;
    }

    public String getFrom_acc_number() {
        return from_acc_number;
    }

    public String getBill_id() {
        return bill_id;
    }

    public String getPay_id() {
        return pay_id;
    }

    public int getPayment() {
        return payment;
    }

    public String getDesc() {
        return desc;
    }

}
