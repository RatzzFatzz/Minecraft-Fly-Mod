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

package at.pcgf.flymod;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

@SuppressWarnings("SpellCheckingInspection")
@ExposableOptions(strategy = ConfigStrategy.Versioned, filename = "flymod.json")
public class FlyModConfig implements Exposable {
    @Expose
    @SerializedName("mouseControl")
    public boolean mouseControl = true;

    @Expose
    @SerializedName("flyUpDownBlocks")
    public float flyUpDownBlocks = 0.4f;

    @Expose
    @SerializedName("flySpeedMultiplier")
    public int flySpeedMultiplier = 3;

    @Expose
    @SerializedName("runSpeedMultiplier")
    public int runSpeedMultiplier = 2;
}