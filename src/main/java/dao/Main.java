package dao;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydatabase";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "1111";

    static Connection conn;

    public static void main(String[] args) {


        try (Scanner sc = new Scanner(System.in)) {
            try {
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);

                while (true) {
                    System.out.println("Add flat -> ADD");
                    System.out.println("Find flat by parameters -> SEARCH");
                    System.out.println("View all flats -> ALL");
                    System.out.print("-> ");

                    String command = sc.nextLine();
                    switch (command) {
                        case "ADD":
                            addFlat(sc);
                            break;
                        case "SEARCH":
                            searchFlat(sc);
                            break;
                        case "ALL":
                            viewAllFlats();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void addFlat(Scanner sc) throws SQLException {
        System.out.print("Enter region: ");
        String region = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter square: ");
        double square = Double.parseDouble(sc.nextLine());
        System.out.print("Enter number of rooms: ");
        int roomCount = Integer.parseInt(sc.nextLine());
        System.out.print("Enter price: ");
        double price = Double.parseDouble(sc.nextLine());

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Flats (region, address,square,room_count,price) VALUES(?, ?,?,?,?)")) {
            ps.setString(1, region);
            ps.setString(2, address);
            ps.setDouble(3, square);
            ps.setInt(4, roomCount);
            ps.setDouble(5, price);
            ps.executeUpdate();
        }
    }

    private static void searchFlat(Scanner sc) throws SQLException {
        System.out.println("Enter parameter for search (region, square, address, price, room_count)");
        String parameter = sc.nextLine();
        System.out.print("Enter value: ");
        String value = sc.nextLine();
        PreparedStatement ps = null;
        try {
            if (parameter.equals("region")) {
                ps = conn.prepareStatement("SELECT * FROM Flats WHERE region = ?");
                ps.setString(1, value);
            } else if (parameter.equals("square")) {
                ps = conn.prepareStatement("SELECT * FROM Flats WHERE square = ?");
                ps.setDouble(1, Double.parseDouble(value));
            } else if (parameter.equals("room_count")) {
                ps = conn.prepareStatement("SELECT * FROM Flats WHERE room_count = ?");
                ps.setInt(1, Integer.parseInt(value));
            } else if (parameter.equals("address")) {
                ps = conn.prepareStatement("SELECT * FROM Flats WHERE address = ?");
                ps.setString(1, value);
            } else if (parameter.equals("price")) {
                ps = conn.prepareStatement("SELECT * FROM Flats WHERE price = ?");
                ps.setDouble(1, Double.parseDouble(value));
            }
            if (ps != null) {
                ResultSet rs = ps.executeQuery();
                printResults(rs);
            }
        } finally {
            ps.close();
        }
    }

    private static void printResults(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t\t");
                }
                System.out.println();
            }
        } finally {
            rs.close();
        }
    }

    private static void viewAllFlats() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Flats")) {
            ResultSet rs = ps.executeQuery();
            printResults(rs);
        }
    }
}
