/*
 * Copyright (C) 2017 MarkusWME RatzzFatzz
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgf.flymod.gui;

import at.pcgf.flymod.FlyModImpl;
import com.google.gson.annotations.Expose;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import static at.pcgf.flymod.gui.FlyModConfig.ConfigTexts.*;

public class FlyModConfig {

    @Expose
    public boolean mouseControl;

    @Expose
    public boolean onlyForCreative;

    @Expose
    public float flyUpDownBlocks;

    @Expose
    public float flySpeedMultiplier;

    @Expose
    public float runSpeedMultiplier;

    @Expose
    public boolean multiplyUpDown;

    @Expose
    public boolean fadeMovement;

    @Expose
    public boolean overrideExhaustion;

    @Expose
    public boolean activeInMultiplayer;

    @Expose
    public boolean activeInLocalMultiplayer;

    @Expose
    public boolean activeInSingleplayer;

    static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(ConfigTexts.TITLE);
        FlyModConfig config = FlyModConfigManager.getConfig();

        builder.getOrCreateCategory(ConfigTexts.MODIFIERS)
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(MOUSE_CONTROL, config.mouseControl).setDefaultValue(true).setSaveConsumer(b -> config.mouseControl = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(FLY_UP_DOWN_BLOCKS, config.flyUpDownBlocks).setDefaultValue(0.4f).setSaveConsumer(b -> config.flyUpDownBlocks = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(FLY_SPEED_MULTIPLIER, config.flySpeedMultiplier).setDefaultValue(2f).setSaveConsumer(b -> config.flySpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(RUN_SPEED_MULTIPLIER, config.runSpeedMultiplier).setDefaultValue(1.3f).setSaveConsumer(b -> config.runSpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(MULTIPLY_UP_DOWN1, config.multiplyUpDown).setDefaultValue(true).setSaveConsumer(b -> config.multiplyUpDown = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(FADE_MOVEMENT, config.fadeMovement).setDefaultValue(false).setSaveConsumer(b -> config.fadeMovement = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(OVERRIDE_EXHAUSTION, config.overrideExhaustion).setDefaultValue(false).setSaveConsumer(b -> config.overrideExhaustion = b).build());

        builder.getOrCreateCategory(ConfigTexts.RUNTIME)
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(CREATIVE_ONLY, config.onlyForCreative).setDefaultValue(false).setSaveConsumer(b -> config.onlyForCreative = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ACTIVE_IN_MULTIPLAYER, config.activeInMultiplayer).setDefaultValue(false).setSaveConsumer(b -> config.activeInMultiplayer = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ACTIVE_IN_LOCAL_MULTIPLAYER, config.activeInLocalMultiplayer).setDefaultValue(true).setSaveConsumer(b -> config.activeInLocalMultiplayer = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ACTIVE_IN_SINGLEPLAYER, config.activeInSingleplayer).setDefaultValue(true).setSaveConsumer(b -> config.activeInSingleplayer = b).build());

        builder.setSavingRunnable((FlyModConfigManager::save));
        return builder.build();
    }

    static class ConfigTexts {
        static final Text TITLE = createTranslatableText("text.%s.title");

        static final Text MODIFIERS = createTranslatableText("text.%s.category.modifiers");
        static final Text MOUSE_CONTROL = createTranslatableText("text.%s.option.mouseControl");
        static final Text FLY_UP_DOWN_BLOCKS = createTranslatableText("text.%s.option.flyUpDownBlocks");
        static final Text FLY_SPEED_MULTIPLIER = createTranslatableText("text.%s.option.flySpeedMultiplier");
        static final Text RUN_SPEED_MULTIPLIER = createTranslatableText("text.%s.option.runSpeedMultiplier");
        static final Text MULTIPLY_UP_DOWN1 = createTranslatableText("text.%s.option.multiplyUpDown");
        static final Text FADE_MOVEMENT = createTranslatableText("text.%s.option.fadeMovement");
        static final Text OVERRIDE_EXHAUSTION = createTranslatableText("text.%s.option.overrideExhaustion");

        static final Text RUNTIME = createTranslatableText("text.%s.category.runtime");
        static final Text CREATIVE_ONLY = createTranslatableText("text.%s.option.creativeOnly");
        static final Text ACTIVE_IN_MULTIPLAYER = createTranslatableText("text.%s.option.activeInMultiplayer");
        static final Text ACTIVE_IN_LOCAL_MULTIPLAYER = createTranslatableText("text.%s.option.activeInLocalMultiplayer");
        static final Text ACTIVE_IN_SINGLEPLAYER = createTranslatableText("text.%s.option.activeInSingleplayer");

        private static MutableText createTranslatableText(String translationReference) {
            return MutableText.of(new TranslatableTextContent(String.format(translationReference, FlyModImpl.MOD_ID), "", new String[]{}));
        }
    }

}