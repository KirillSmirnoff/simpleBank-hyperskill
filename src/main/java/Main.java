import dao.DataSource;

public class Main {
    public static void main(String[] args) {
        InteractiveMenu interactiveMenu = new InteractiveMenu();

        DataSource.initDB(args);
        interactiveMenu.mainMenu();

//        4000000435120458  3770

//        4000006606075579

    }
}