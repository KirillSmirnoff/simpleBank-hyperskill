public class CreditCard {
    private String cardNumber ;
    private String PIN;
    private int balance = 0;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Your card number:\n" + cardNumber + "\nYour card PIN:\n" + PIN +"\n";
    }
}
