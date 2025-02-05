package ru.job4j.tracker;

import org.junit.jupiter.api.Test;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.store.HbmTracker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

public class TrackerHbmTest {
    @Test
    public void whenAddNewItemsThenTrackerHasSameItem() throws Exception {
        try (var tracker = new HbmTracker()) {
            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);

            Item item = new Item();
            item.setName("test 1");
            item.setCreated(creationDate);
            Item item2 = new Item();
            item2.setName("test 2");
            item2.setCreated(creationDate);
            Item item3 = new Item();
            item3.setName("test 3");
            item3.setCreated(creationDate);

            tracker.add(item);
            tracker.add(item2);
            tracker.add(item3);

            Item result = tracker.findById(item.getId());
            assertThat(result).isEqualTo(item);

            Item result2 = tracker.findById(item2.getId());
            assertThat(result2).isEqualTo(item2);

            Item result3 = tracker.findById(item3.getId());
            assertThat(result3).isEqualTo(item3);
        }
    }

    @Test
    public void whenReplaceNewItemThenTrackerReturnTrueAndFalseAndSameItem() throws Exception {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);
            item.setName("test1");
            item.setCreated(creationDate);
            tracker.add(item);

            Item replaceItem = new Item();
            replaceItem.setId(item.getId());
            replaceItem.setName("replaceItem");
            replaceItem.setCreated(LocalDateTime.parse("2024-08-24T15:59:57"));

            var successfulReplace = tracker.replace(item.getId(), replaceItem);
            assertThat(successfulReplace).isEqualTo(true);

            Item result = tracker.findById(item.getId());
            assertThat(result).isEqualTo(replaceItem);

            tracker.delete(item.getId());

            var unSuccessfulReplace = tracker.replace(item.getId(), replaceItem);
            assertThat(unSuccessfulReplace).isEqualTo(false);
        }
    }


    @Test
    public void whenDeleteItemThenTrackerReturnTrueAndFalse() throws Exception {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);
            item.setName("test1");
            item.setCreated(creationDate);
            tracker.add(item);

            var result = tracker.delete(item.getId());
            assertThat(result).isEqualTo(true);

            var result2 = tracker.delete(item.getId());
            assertThat(result2).isEqualTo(false);

        }
    }

    @Test
    public void whenFindAllItemsThenTrackerReturnAllItems() throws Exception {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            Item item2 = new Item();
            Item item3 = new Item();

            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);

            item.setName("test 1");
            item.setCreated(creationDate);
            item2.setName("test 2");
            item2.setCreated(creationDate);
            item3.setName("test 3");
            item3.setCreated(creationDate);

            tracker.add(item);
            tracker.add(item2);
            tracker.add(item3);

            var expectedItems = Arrays.asList(item, item2, item3);
            var result = tracker.findAll();

            assertThat(result).isEqualTo(expectedItems);
        }
    }

    @Test
    public void whenAddSeveralItemsThenTrackerHasSameItemByName() throws Exception {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            Item item2 = new Item();
            Item item3 = new Item();

            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);

            item.setName("test 1");
            item.setCreated(creationDate);
            item2.setName("test 2");
            item2.setCreated(creationDate);
            item3.setName("no find");
            item3.setCreated(creationDate);

            tracker.add(item);
            tracker.add(item2);
            tracker.add(item3);

            var expectedItems = Arrays.asList(item, item2);
            var result = tracker.findByName("test");

            assertThat(result).isEqualTo(expectedItems);
        }
    }

    @Test
    public void whenAddSeveralItemsThenTrackerHasSameItemById() throws Exception {
        try (var tracker = new HbmTracker()) {
            Item item = new Item();
            Item item2 = new Item();
            Item item3 = new Item();

            var creationDate = now().truncatedTo(ChronoUnit.SECONDS);

            item.setName("test 1");
            item.setCreated(creationDate);
            item2.setName("test 2");
            item2.setCreated(creationDate);
            item3.setName("test 3");
            item3.setCreated(creationDate);

            tracker.add(item);
            tracker.add(item2);
            tracker.add(item3);

            var result = tracker.findById(item2.getId());

            assertThat(result).isEqualTo(item2);
        }
    }
}