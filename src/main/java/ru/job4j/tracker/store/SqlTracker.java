package ru.job4j.tracker.store;

import ru.job4j.tracker.model.Item;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class SqlTracker implements Store {
    private Connection cn;

    List<Item> itemList;
    private Map<String, String> properties = new HashMap<String, String>();

    public SqlTracker() {
        init();
    }

    public SqlTracker(Connection cn) {
        this.cn = cn;
    }

    private void init() {
        try (InputStream in = new FileInputStream("db/liquibase.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
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
        try (PreparedStatement statement = cn.prepareStatement("UPDATE items SET name = ?, created = ? WHERE id= ?")) {
            statement.setString(1,  item.getName());
            statement.setTimestamp(2, item.getCreated());
            statement.setInt(3, id);
            boolean result = statement.executeUpdate() > 0;
            if (result) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try (PreparedStatement statement = cn.prepareStatement("DELETE FROM items WHERE id= ?")) {
            statement.setInt(1, id);
            boolean result = statement.executeUpdate() > 0;
            if (result) {
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
        try (PreparedStatement statement = cn.prepareStatement("SELECT * FROM items;")) {
            try (ResultSet selection = statement.executeQuery()) {
                while (selection.next()) {
                    int id = selection.getInt(1);
                    String name = selection.getString(2);
                    Timestamp timeStamp = selection.getTimestamp(3);
                    Item item = new Item(id, name);
                    item.setCreated(timeStamp);
                    itemList.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemList;
    }

    @Override
    public List<Item> findByName(String key) {
        itemList = new ArrayList<>();
        try (PreparedStatement statement = cn.prepareStatement("SELECT * FROM items WHERE name= ?")) {
            statement.setString(1,  key);
            try (ResultSet selection = statement.executeQuery()) {
                while (selection.next()) {
                    int id = selection.getInt(1);
                    String name = selection.getString(2);
                    Timestamp timeStamp = selection.getTimestamp(3);
                    Item item = new Item(id, name);
                    item.setCreated(timeStamp);
                    itemList.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemList;
    }

    @Override
    public Item findById(int id) {
        Item item = null;
        try (PreparedStatement statement = cn.prepareStatement("SELECT * FROM items WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet selection = statement.executeQuery()) {
                if (selection.next()) {
                    String name = selection.getString(2);
                    Timestamp timeStamp = selection.getTimestamp(3);
                    item = new Item(id, name);
                    item.setCreated(timeStamp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return item;
    }
}