public class InteractiveMenu {
    private final BankMethods bankMethods = new BankMethods();

    public void mainMenu() {
        int choose;

        do {
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            System.out.print("> ");
            choose = Integer.parseInt(bankMethods.stdIn());

            switch (choose) {
                case 1:
                    System.out.println(bankMethods.createAccount());
                    break;
                case 2:
                    if (bankMethods.logIn()) {
                        accountMenu();
                    }
                    break;
                case 0:
                    System.out.println("\nBye!");
                    System.exit(0);
            }
        } while (true);
    }

    public void accountMenu() {
        int choose;
        do {
            System.out.println("1. Balance\n" +
                    "2. Add income\n"+
                    "3. Do transfer\n"+
                    "4. Close account\n"+
                    "5. Log out\n" +
                    "0. Exit");
            System.out.print("> ");
            choose = Integer.parseInt(bankMethods.stdIn());

            switch (choose) {
                case 1:
                    bankMethods.getBalance();
                    System.out.println("\nBalance: " + bankMethods.getBalance() + "\n");
                    break;
                case 2:
                    bankMethods.topUpBalance();
                    break;
                case 3:
                    bankMethods.transfer();
                    break;
                case 4:
                    bankMethods.closeAccount();
                    System.out.println("The account has been closed!\n");
                    choose=5;
                    break;
                case 5:
                    bankMethods.logOut();
                    System.out.println("\nYou have successfully logged out!\n");
                    break;
                case 0:
                    System.out.println("\nBye!");
                    bankMethods.exit();
                    System.exit(0);
            }
        } while (choose != 5);
    }
}
