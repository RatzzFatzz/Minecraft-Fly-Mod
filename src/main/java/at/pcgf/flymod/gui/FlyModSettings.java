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

import at.pcgf.flymod.LiteModFlyMod;
import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

@SuppressWarnings("FieldCanBeLocal,NullableProblems,SpellCheckingInspection,UnusedAssignment")
public class FlyModSettings extends GuiScreen {
    private GuiCheckbox mouseControl;
    private GuiSlider flyUpDownBlocks;
    private GuiSlider flySpeedMultiplier;
    private GuiSlider runSpeedMultiplier;

    private static final int MOUSE_CONTROL_ID = 0;
    private static final int FLY_UP_DOWN_ID = 1;
    private static final int FLY_MULTIPLIER_ID = 2;
    private static final int RUN_MULTIPLIER_ID = 3;

    @Override
    public void initGui() {
        final int LEFT = width / 10;
        final int WIDTH = LEFT * 8;
        int y = 40;
        mouseControl = new GuiCheckbox(MOUSE_CONTROL_ID, LEFT, y, I18n.format("flymod.settings.mousecontrol"));
        mouseControl.checked = LiteModFlyMod.config.mouseControl;
        buttonList.add(MOUSE_CONTROL_ID, mouseControl);
        flyUpDownBlocks = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {}

            @Override
            public void setEntryValue(int id, float value) {
                LiteModFlyMod.config.flyUpDownBlocks = value;
                setEntryValue(id, "" + LiteModFlyMod.config.flyUpDownBlocks);
            }

            @Override
            public void setEntryValue(int id, String value) {}
        }, FLY_UP_DOWN_ID, LEFT, y += 20, "flymod.settings.flyupdownblocks", 0.2f, 1.0f, LiteModFlyMod.config.flyUpDownBlocks, (id, name, value) -> name + ": " + value);
        flyUpDownBlocks.setWidth(WIDTH);
        buttonList.add(FLY_UP_DOWN_ID, flyUpDownBlocks);
        flySpeedMultiplier = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {}

            @Override
            public void setEntryValue(int id, float value) {
                LiteModFlyMod.config.flySpeedMultiplier = Math.round(value);
                setEntryValue(id, "" + LiteModFlyMod.config.flySpeedMultiplier);
            }

            @Override
            public void setEntryValue(int id, String value) {}
        }, FLY_MULTIPLIER_ID, LEFT, y += 25, "flymod.settings.flyspeedmultiplier", 2.0f, 10.0f, LiteModFlyMod.config.flySpeedMultiplier, (id, name, value) -> name + ": " + Math.round(value));
        flySpeedMultiplier.setWidth(WIDTH);
        buttonList.add(FLY_MULTIPLIER_ID, flySpeedMultiplier);
        runSpeedMultiplier = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {}

            @Override
            public void setEntryValue(int id, float value) {
                LiteModFlyMod.config.runSpeedMultiplier = Math.round(value);
                setEntryValue(id, "" + LiteModFlyMod.config.runSpeedMultiplier);
            }

            @Override
            public void setEntryValue(int id, String value) {}
        }, RUN_MULTIPLIER_ID, LEFT, y += 25, "flymod.settings.runspeedmultiplier", 2.0f, 10.0f, LiteModFlyMod.config.runSpeedMultiplier, (id, name, value) -> name + ": " + Math.round(value));
        runSpeedMultiplier.setWidth(WIDTH);
        buttonList.add(RUN_MULTIPLIER_ID, runSpeedMultiplier);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(LiteModFlyMod.minecraft.fontRenderer, I18n.format("flymod.settings"), width / 2, 12, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case MOUSE_CONTROL_ID:
                mouseControl.checked = !mouseControl.checked;
                LiteModFlyMod.config.mouseControl = mouseControl.checked;
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        LiteLoader.getInstance().writeConfig(LiteModFlyMod.config);
    }
}