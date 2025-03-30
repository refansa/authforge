package com.thumbleweed.authforge.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thumbleweed.authforge.AuthForge;
import com.thumbleweed.authforge.core.i18n.LanguageMap;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class I18nConfig {
    private final Map<String, ForgeConfigSpec.ConfigValue<String>> mappings;

    public I18nConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("i18n configuration").push("i18n");

        this.mappings = new HashMap<>();

        String path = String.format("/assets/%s/lang/en_us.json", AuthForge.ID);
        try (InputStream inputStream = LanguageMap.class.getResourceAsStream(path)) {
            assert inputStream != null : "input stream is null";

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            JsonObject jsonObject = new Gson().fromJson(inputStreamReader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                this.put(builder, entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException e) {
            AuthForge.LOGGER.trace(e);
        }

        builder.pop();
    }

    private void put(ForgeConfigSpec.Builder builder, String key, String comment) {
        this.mappings.put(key, builder.comment(comment).define(key, ""));
    }

    public Map<String, String> getTranslations() {
        return this.mappings.entrySet().
                stream().
                filter(e -> !e.getValue().get().isEmpty()).
                collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }
}
