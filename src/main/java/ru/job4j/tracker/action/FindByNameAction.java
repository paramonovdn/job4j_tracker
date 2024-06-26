package ru.job4j.tracker.action;

import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.MemTracker;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.Store;

import java.util.List;

public class FindByNameAction implements UserAction {

    private final Output output;

    public FindByNameAction() {
        output = null;
    }

    public FindByNameAction(Output output) {
        this.output = output;
    }
    @Override
    public String name() {
        return "=== Find items by name ====";
    }

    @Override
    public boolean execute(Input input, Store tracker) {
        String name = input.askStr("Enter name: ");
        List<Item> items = tracker.findByName(name);
        for (Item item: items) {
            System.out.println(item);
        }
        return items.size() > 0;
    }
}
