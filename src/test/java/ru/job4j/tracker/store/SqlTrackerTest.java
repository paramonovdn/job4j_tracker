package ru.job4j.tracker.store;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.tracker.model.Item;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class SqlTrackerTest {

    private static Connection connection;
    private static Map<String, String> properties = new HashMap<String, String>();

    @BeforeAll
    public static void initConnection() {
        StringBuilder text = null;
        try (InputStream in = new FileInputStream("db/liquibase_test.properties")) {
            text = new StringBuilder();
            int read;
            while ((read = in.read()) != -1) {
                text.append((char) read);
            }
            String[] lines = text.toString().split(System.lineSeparator());
            for (String line : lines) {
                int simbolPosition = line.indexOf("=");
                String key = line.substring(0, simbolPosition);
                String value = line.substring(simbolPosition + 1, line.length());
                properties.put(key, value);
            }
            Class.forName(properties.get("driver-class-name"));
            connection = DriverManager.getConnection(
                    properties.get("url"),
                    properties.get("username"),
                    properties.get("password")
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @AfterEach
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from items")) {
            statement.execute();
        }
    }

    @Test
    public void whenSaveItemAndFindByGeneratedIdThenMustBeTheSame() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        assertThat(tracker.findById(item.getId())).isEqualTo(item);
    }

    @Test
    public void whenAddSeveralItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item0 = new Item("item0");
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        assertThat(tracker.add(item0)).isEqualTo(item0);
        assertThat(tracker.add(item1)).isEqualTo(item1);
        assertThat(tracker.add(item2)).isEqualTo(item2);
    }

    @Test
    public void whenReplaceItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        Item anotherItem = new Item(item.getId(), "anotherItem");
        assertThat(tracker.replace(item.getId(), anotherItem)).isEqualTo(true);
        assertThat(tracker.findById(item.getId())).isEqualTo(anotherItem);
    }

    @Test
    public void whenDeleteItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item0 = new Item("item0");
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        tracker.add(item0);
        tracker.add(item1);
        tracker.add(item2);
        assertThat(tracker.delete(item2.getId())).isEqualTo(true);
        assertThat(tracker.findById(item2.getId())).isEqualTo(null);
    }

    @Test
    public void whenFindAllItems() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item0 = new Item("item0");
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        tracker.add(item0);
        tracker.add(item1);
        tracker.add(item2);
        assertThat(tracker.findAll()).isEqualTo(Arrays.asList(item0, item1, item2));
    }

    @Test
    public void whenFindByName() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item0 = new Item("item2");
        Item item1 = new Item("item2");
        Item item2 = new Item("item2");
        tracker.add(item0);
        tracker.add(item1);
        tracker.add(item2);
        assertThat(tracker.findByName("item2")).isEqualTo(Arrays.asList(item0, item1, item2));
    }

    @Test
    public void whenFindById() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item0 = new Item("item0");
        Item item1 = new Item("item1");
        Item item2 = new Item("item2");
        tracker.add(item0);
        tracker.add(item1);
        tracker.add(item2);
        assertThat(tracker.findById(item2.getId())).isEqualTo(item2);
    }



}