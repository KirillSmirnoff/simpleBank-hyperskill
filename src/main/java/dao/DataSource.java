package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DataSource {
    private static String dbName ;
    private Connection connection;

    public static void initDB(String[] args) {
        createDB(args);
    }

    private Connection getConnection() {
//        String s = Paths.get(dbName).toAbsolutePath().toString();
        String url = "jdbc:sqlite:" + dbName;

        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getBalance(String cardNumber) {
        final String query = "select balance from card where number = ?";
        String balance = "0";
        Connection connection = getConnection();

//        if (checkConnection(connection)) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            connection.commit();

            balance = resultSet.getString("balance");
        } catch (SQLException e) {
            System.out.println("Не удалось получить баланс карты");
            e.printStackTrace();
        }
        return balance;
    }

    public void topUpBalance(String cardNumber, String balance) {
        final String query = "UPDATE card SET balance = (select balance from card where number = ?) + ?  WHERE number = ?";
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardNumber);
            statement.setInt(2, Integer.parseInt(balance));
            statement.setString(3, cardNumber);

            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        }
    }

    public void persistCard(String cardNumber, String pin) {
        String query = "insert into card (number, pin) values (?,?)";
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardNumber);
            statement.setString(2, pin);

            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Не удалось создать новую карту");
            rollback(connection);
            e.printStackTrace();
        }
    }

    public void removeCard(String card) {
        final String query = "delete from card where number = ?";
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, card);

            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        }
    }

    public Map<String, String> getCardByNumberAndPIN(String cardNumber, String pin) {
        final String query = "select number, pin , balance from card where number = ? and pin = ?";
        Map<String, String> card = new HashMap<>();

        Connection connection = getConnection();

//        if (checkConnection(connection)){
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardNumber);
            statement.setString(2, pin);

            ResultSet resultSet = statement.executeQuery();
            connection.commit();

            if (resultSet.next()) {
                card.put("number", resultSet.getString("number"));
                card.put("pin", resultSet.getString("pin"));
                card.put("balance", resultSet.getString("balance"));
            }
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        }
//        }else System.out.println("Не удалось проверить номер карты");

        return card;
    }

    public Map<String, String> getCardByNumber(String cardNumber) {
        final String query = "select number, pin , balance from card where number = ?";
        Map<String, String> card = new HashMap<>();

        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cardNumber);

            ResultSet resultSet = statement.executeQuery();
            connection.commit();

            if (resultSet.next()) {
                card.put("number", resultSet.getString("number"));
                card.put("pin", resultSet.getString("pin"));
                card.put("balance", resultSet.getString("balance"));
            }
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        }
        return card;
    }

    public void transfer(String sendCard, String receiveCard, String sum) {
        String queryDecrease = "UPDATE card SET balance = (select balance from card where number = ?) - ?  WHERE number = ?";
        String queryIncrease = "UPDATE card SET balance = (select balance from card where number = ?) + ?  WHERE number = ?";

        Connection connection = getConnection();

        try (PreparedStatement statementDecrease = connection.prepareStatement(queryDecrease);
             PreparedStatement statementIncrease = connection.prepareStatement(queryIncrease)) {
            statementDecrease.setString(1, sendCard);
            statementDecrease.setString(2, sum);
            statementDecrease.setString(3, sendCard);
            statementDecrease.executeUpdate();

            statementIncrease.setString(1, receiveCard);
            statementIncrease.setString(2, sum);
            statementIncrease.setString(3, receiveCard);
            statementIncrease.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
        }

    }

    private static void createDB(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fileName")) {
                dbName = args[i + 1];
                break;
            }
        }
        if (dbName != null) {
            File db = new File(dbName);
            if (!db.exists()) {
                System.out.println("База данныз не найдена, производится создание новой БД");
                try {
                    db.createNewFile();
                } catch (IOException e) {
                    System.out.println("Не удалось создать файл БД");
                    e.printStackTrace();
                }
            } else
                System.out.println("Найдена существующая база данных");
        }

    }
}
