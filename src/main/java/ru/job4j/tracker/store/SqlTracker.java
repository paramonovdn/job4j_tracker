package ru.job4j.tracker.store;

import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store {

    private Connection cn;

    Statement statement;

    List<Item> itemList;

    public SqlTracker() {
        init();
    }

    public SqlTracker(Connection cn) {
        this.cn = cn;
    }

    private void init() {
        try (InputStream in = SqlTracker.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
            statement = cn.createStatement();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item item) {
        try (PreparedStatement statement = cn.prepareStatement("INSERT INTO items(name, created) VALUES (?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, item.getCreated());
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public boolean replace(int id, Item item) {
        try {
            String sql = String.format(
                    "SELECT * FROM items WHERE id = " + id + ";"
            );
            ResultSet selection = statement.executeQuery(sql);
            if (selection.next()) {
                sql = String.format(
                    "UPDATE items SET name = " + "'" + item.getName() + "'" + " WHERE id= " + id + ";"
                );
                statement.execute(sql);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

        @Override
    public boolean delete(int id) {
        try {
            String sql = String.format(
                "SELECT * FROM items WHERE id = " + id + ";"
            );
            ResultSet selection = statement.executeQuery(sql);
            if (selection.next()) {
                sql = String.format(
                    "DELETE FROM items WHERE id= " + id + ";"
                 );
                statement.execute(sql);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public List<Item> findAll() {
        itemList = new ArrayList<>();
        try {
            String sql = String.format(
                    "SELECT * FROM items;"
            );
            ResultSet selection = statement.executeQuery(sql);
            while (selection.next()) {
                int id = selection.getInt(1);
                String name = selection.getString(2);
                itemList.add(new Item(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemList;
    }

    @Override
    public List<Item> findByName(String key) {
        itemList = new ArrayList<>();
        try {
            String sql = String.format(
                    "SELECT * FROM items WHERE name= " + "'" + key + "'" + ";"
            );
            ResultSet selection = statement.executeQuery(sql);
            while (selection.next()) {
                int id = selection.getInt(1);
                String name = selection.getString(2);
                itemList.add(new Item(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemList;
    }

    @Override
    public Item findById(int id) {
        Item item = null;
        try {
            String sql = String.format(
                    "SELECT * FROM items WHERE id = " + id + ";"
            );
            ResultSet selection = statement.executeQuery(sql);
            if (selection.next()) {
                String name = selection.getString(2);
                item = new Item(id, name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return item;
    }


}