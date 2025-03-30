package com.thumbleweed.authforge.core.i18n;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class LanguageMap {
    private final Map<String, String> map;

    private static LanguageMap instance;
    private static final Gson GSON = new Gson();
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    public enum Language {
        EN_US,
        ID_ID,
    }

    private LanguageMap(Map<String, String> map) {
        this.map = map;
    }

    private static LanguageMap loadLangFile(String lang) {
        final Map<String, String> map = new HashMap<>();
        BiConsumer<String, String> biConsumer = map::put;

        String langFile = String.format("/assets/authforge/lang/%s.json", lang.toLowerCase());

        InputStream inputStream = LanguageMap.class.getResourceAsStream(langFile);
        loadFromJson(inputStream, biConsumer);

        return new LanguageMap(map);
    }

    private static void loadFromJson(InputStream in, BiConsumer<String, String> consumer) {
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);

        JsonObject jsonobject = GSON.fromJson(inputStreamReader, JsonObject.class);
        for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            String s = UNSUPPORTED_FORMAT_PATTERN.matcher(convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            consumer.accept(entry.getKey(), s);
        }
    }

    private static CharSequence convertToString(JsonElement value, String key) {
        if (value.isJsonPrimitive()) {
            return value.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a string");
        }
    }

    private static void init(String lang) {
        if (instance == null) {
            instance = loadLangFile(lang);
        }
    }

    public static void loadTranslations(String lang) {
        if (instance == null) init(lang); else instance.replaceWith(loadLangFile(lang).map);
    }

    public static void loadTranslations() {
        loadTranslations("en_us");
    }

    public static LanguageMap getInstance() {
        if (instance == null) loadTranslations();
        return instance;
    }

    public static void replaceTranslations(Map<String, String> dict) {
        if (instance != null) {
            instance.replaceWith(dict);
        }
    }

    public String getOrDefault(String key) {
        return this.map.getOrDefault(key, key);
    }

    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    public void replaceWith(Map<String, String> dict) {
        this.map.putAll(dict);
    }
}
