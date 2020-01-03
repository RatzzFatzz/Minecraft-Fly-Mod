/*
 * Copyright (C) 2017 MarkusWME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgf.flymod.gui;

import at.pcgf.flymod.FlyModImpl;
import com.google.gson.annotations.Expose;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class FlyModConfig {

    @Expose
    public boolean mouseControl = true;

    @Expose
    public float flyUpDownBlocks = 0.4f;

    @Expose
    public int flySpeedMultiplier = 3;

    @Expose
    public int runSpeedMultiplier = 2;

    @Expose
    public boolean multiplyUpDown = true;

    static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(ConfigTexts.TITLE.asString());
        FlyModConfig config = FlyModConfigManager.getConfig();
        builder.getOrCreateCategory(ConfigTexts.CATEGORY.asString())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.MOUSE_CONTROL.asString(), config.mouseControl).setDefaultValue(true).setSaveConsumer(b -> config.mouseControl = b).build())
                .addEntry(ConfigEntryBuilder.create().startFloatField(ConfigTexts.FLY_UP_DOWN_BLOCKS.asString(), config.flyUpDownBlocks).setDefaultValue(0.4f).setSaveConsumer(b -> config.flyUpDownBlocks = b).build())
                .addEntry(ConfigEntryBuilder.create().startIntField(ConfigTexts.FLY_SPEED_MULTIPLIER.asString(), config.flySpeedMultiplier).setDefaultValue(3).setSaveConsumer(b -> config.flySpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startIntField(ConfigTexts.RUN_SPEED_MULTIPLIER.asString(), config.runSpeedMultiplier).setDefaultValue(2).setSaveConsumer(b -> config.runSpeedMultiplier = b).build())
                .addEntry(ConfigEntryBuilder.create().startBooleanToggle(ConfigTexts.MULTIPLY_UP_DOWN1.asString(), config.multiplyUpDown).setDefaultValue(true).setSaveConsumer(b -> config.multiplyUpDown = b).build());
        builder.setSavingRunnable((FlyModConfigManager::save));
        return builder.build();
    }

    static private class ConfigTexts {
        static final TranslatableText TITLE = new TranslatableText(String.format("text.%s.title", FlyModImpl.MOD_ID));
        static final TranslatableText CATEGORY = new TranslatableText(String.format("text.%s.category.default", FlyModImpl.MOD_ID));
        static final TranslatableText MOUSE_CONTROL = new TranslatableText(String.format("text.%s.option.mouseControl", FlyModImpl.MOD_ID));
        static final TranslatableText FLY_UP_DOWN_BLOCKS = new TranslatableText(String.format("text.%s.option.flyUpDownBlocks", FlyModImpl.MOD_ID));
        static final TranslatableText FLY_SPEED_MULTIPLIER = new TranslatableText(String.format("text.%s.option.flySpeedMultiplier", FlyModImpl.MOD_ID));
        static final TranslatableText RUN_SPEED_MULTIPLIER = new TranslatableText(String.format("text.%s.option.runSpeedMultiplier", FlyModImpl.MOD_ID));
        static final TranslatableText MULTIPLY_UP_DOWN1 = new TranslatableText(String.format("text.%s.option.multiplyUpDown", FlyModImpl.MOD_ID));
    }
}