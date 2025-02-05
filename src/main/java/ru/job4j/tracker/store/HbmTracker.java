package ru.job4j.tracker.store;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.tracker.model.Item;

import java.util.ArrayList;
import java.util.List;

public class HbmTracker implements Store, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HbmTracker.class.getName());

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public Item add(Item item) {
        var session = sf.openSession();
        try {
            session.beginTransaction();
            session.save(item);
            var newId = session.createQuery("FROM Item AS item WHERE item.name = :itemName AND item.created = :itemCreated",
                            Item.class)
                    .setParameter("itemName", item.getName())
                    .setParameter("itemCreated", item.getCreated())
                    .uniqueResult().getId();
            item.setId(newId);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return item;
    }

    @Override
    public boolean replace(int id, Item item) {
        var session = sf.openSession();
        boolean result = false;
        try {
            session.beginTransaction();
            var query = session.createQuery(
                            "UPDATE Item SET name = :iName, created = :iCreated WHERE id = :iId")
                    .setParameter("iName", item.getName())
                    .setParameter("iCreated", item.getCreated())
                    .setParameter("iId", id);
            result = query.executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return  result;
    }

    @Override
    public boolean delete(int id) {
        var session = sf.openSession();
        boolean result = false;
        try {
            session.beginTransaction();
            var query = session.createQuery(
                            "DELETE Item WHERE id = :iId")
                    .setParameter("iId", id);
            result = query.executeUpdate() > 0;
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public List<Item> findAll() {
        var session = sf.openSession();
        List<Item> result = new ArrayList<>();
        try {
            session.beginTransaction();
            result = session.createQuery("FROM Item", Item.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public List<Item> findByName(String key) {
        var session = sf.openSession();
        List<Item> result = new ArrayList<>();
        try {
            session.beginTransaction();
            result = session.createQuery("FROM Item WHERE name LIKE :searchKey", Item.class)
                    .setParameter("searchKey", "%" + key + "%")
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public Item findById(int id) {
        var session = sf.openSession();
        Item result = null;
        try {
            session.beginTransaction();
            Query<Item> query = session.createQuery(
                    "FROM Item AS item WHERE item.id = :itemId", Item.class);
            query.setParameter("itemId", id);
            result = query.uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}