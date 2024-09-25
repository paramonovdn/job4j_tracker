package ru.job4j.lombok;

import java.util.List;
import lombok.*;

@Builder(builderMethodName = "of")
@ToString
public class Permission {
    private int id;
    private String name;
    @Singular("accessBy")
    private List<String> rules;

    public static class PermissionUsage {
        public static void main(String[] args) {
            var permission = Permission.of()
                    .id(1)
                    .name("John")
                    .accessBy("create")
                    .accessBy("update")
                    .accessBy("read")
                    .accessBy("delete")
                    .build();
            System.out.println(permission);
        }
    }
}