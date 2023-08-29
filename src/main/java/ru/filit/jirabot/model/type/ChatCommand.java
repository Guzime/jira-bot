package ru.filit.jirabot.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ChatCommand {
    SUBSCRIBE("/subscribe"),
    DEF("/def"),
    HELP ("/help"),
    UNSUBSCRIBE("/unsubscribe"),
    SUBSCRIBE_LIST("/subscribe_list");

    private final String name;

    public static final Map<String, ChatCommand> map;
    static {
        map = new HashMap<>();
        for (ChatCommand v : ChatCommand.values()) {
            map.put(v.name, v);
        }
    }

    public static ChatCommand findByName(String name) {
        if (!map.containsKey(name)) {
            return DEF;
        }
        return map.get(name);
    }
}
