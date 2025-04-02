package com.thumbleweed.authforge.util.text;

import com.thumbleweed.authforge.core.i18n.LanguageMap;
import net.minecraft.network.chat.Component;

public class TextComponent {
    public static Component Create(String translationKey, Object... args) {
        String message = String.format(LanguageMap.getInstance().getOrDefault(translationKey), args);
        return Component.literal(message);
    }
}
