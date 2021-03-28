import dao.DataSource;

import java.util.*;

public class BankMethods {
    Random rd = new Random();
    Scanner sc = new Scanner(System.in);
    DataSource dataSource = new DataSource();

    CreditCard currentCard;

    public String stdIn() {
        return sc.nextLine();
    }

    public CreditCard createAccount() {
        CreditCard creditCard = new CreditCard();
        String cardNumber = cardNumberGenerate();
        String pin = pinGenerate();

        creditCard.setCardNumber(cardNumber);
        creditCard.setPIN(pin);
        dataSource.persistCard(cardNumber, pin);

        System.out.println("\nYour card has been created");

        return creditCard;
    }

    public boolean logIn() {
        System.out.println("\nEnter your card number:");
        String cardNumber = stdIn();
        System.out.println("Enter your PIN:");
        String pin = stdIn();

        CreditCard creditCard = checkCredentials(cardNumber, pin);

        if (creditCard.getCardNumber() != null){
            System.out.println("\nYou have successfully logged in!\n");
            currentCard = creditCard;
            return true;
        }else {
            System.out.println("\nWrong card number or PIN!\n");
            return false;
        }
//        return true;
    }

    public void logOut() {
        currentCard = null;
    }

    public void closeAccount() {
        dataSource.removeCard(currentCard.getCardNumber());

    }

    public void exit() {
        dataSource.closeConnection();
    }

    public int getBalance() {
        String balance = dataSource.getBalance(currentCard.getCardNumber());

        return Integer.parseInt(balance);
    }

    public void topUpBalance() {
        System.out.println("Enter income:");
        dataSource.topUpBalance(currentCard.getCardNumber(), stdIn());
//        dataSource.topUpBalance("12345678", stdIn());
        System.out.println("Income was added\n");
    }

    private CreditCard checkCredentials(String cardNumber, String PIN) {
        Map<String, String> card = dataSource.getCardByNumberAndPIN(cardNumber, PIN);
        CreditCard creditCard = new CreditCard();
        if (!card.isEmpty()) {
            creditCard.setCardNumber(card.get("number"));
            creditCard.setPIN(card.get("pin"));
            creditCard.setBalance(Integer.parseInt(card.get("balance")));
        }

        return creditCard;

    }

    private String cardNumberGenerate() {
//        String mii; //Major Industry Identifier - 1 digit,  begin with 4
//        String bin; //Bank Identification Number - 6 digit  the BIN must be 400000
//        accountNumber; // customer account number - 7 digit, whole card number should be 16-digit length.
//        int checksum = 7;  // 1 last digit, now any digit
        StringBuilder cardNumber = new StringBuilder("400000");

        for (int i = 0; i < 9; i++) {
            cardNumber.append(rd.nextInt(9));
        }

        int checksum = checksumGenerate(cardNumber.toString());

        return cardNumber.append(checksum).toString();
    }

    private String pinGenerate() {
        StringBuilder PIN = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            PIN.append(rd.nextInt(9));
        }
        return PIN.toString();
    }

    private int checksumGenerate(String cardNumber) {
        String[] split = cardNumber.split("");
        int sum = 0;
        int tempInt;
        int multiply = split.length - 1;
        for (int i = split.length - 1; i >= 0; i--) {
            if (multiply == i) {
                tempInt = Integer.parseInt(split[i]) * 2;
                if (tempInt > 9) {
                    tempInt -= 9;
                }
                sum += tempInt;
                multiply -= 2;
            } else
                sum += Integer.parseInt(split[i]);
        }
        return (sum * 9) % 10;
    }

    private boolean checkEnoughMoney(String sum) {
        return Integer.parseInt(sum) < getBalance();
    }

    private boolean checkExistCard(String cardNumber) {
        Map<String, String> cardFromDB = dataSource.getCardByNumber(cardNumber);
        if (!cardFromDB.isEmpty())
            return cardFromDB.get("number").equals(cardNumber);

        return false;
    }

    private boolean checkSameCard(String cardNumber){
        return currentCard.getCardNumber().equals(cardNumber);
    }

    private boolean checkAlgorithmLuhn(String cardNUmber) {
        return cardNUmber.substring(15,16).equals(Integer.valueOf(checksumGenerate(cardNUmber.substring(0,15))).toString());
    }

    public void transfer() {
        System.out.println("Transfer\n" +
                "Enter card number:");
        String recipientCard = stdIn();
        String sum = "0";
        if (!checkAlgorithmLuhn(recipientCard))
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
        else if (checkSameCard(recipientCard))
            System.out.println("You can't transfer money to the same account!\n");
        else if (!checkExistCard(recipientCard))
            System.out.println("Such a card does not exist.\n");
        else{
            System.out.println("Enter how much money you want to transfer:");
            sum = stdIn();
        }
        if (!checkEnoughMoney(sum))
            System.out.println("Not enough money!\n");
        else {
            dataSource.transfer(currentCard.getCardNumber(), recipientCard, sum);
            System.out.println("Success");
        }
    }


}
