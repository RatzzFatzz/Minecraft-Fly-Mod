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


import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FlyModConfigManager {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Flymod Config Manager"));
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private static FlyModConfig config;
    private static Path configFile;

    public static FlyModConfig getConfig() {
        return config != null ? config : init();
    }

    public static FlyModConfig init() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("flymod" + ".json");
        if(! Files.exists(configFile)){
            System.out.println("Creating flymod config file");
            save().join();
        }
        load().thenApply(c -> config = c).join();
        return Objects.requireNonNull(config, "Failed to initialize flymod config");
    }

    public static CompletableFuture<FlyModConfig> load() {
        return CompletableFuture.supplyAsync(() -> {
            try(BufferedReader reader = Files.newBufferedReader(configFile)){
                return GSON.fromJson(reader, FlyModConfig.class);
            }catch(IOException | JsonParseException e){
                System.err.println("Unable to read flymod config, restoring defaults");
                save();
                return new FlyModConfig();
            }
        }, EXECUTOR);
    }

    public static CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            config = Optional.ofNullable(config).orElseGet(FlyModConfig::new);
            try(BufferedWriter writer = Files.newBufferedWriter(configFile)){
                GSON.toJson(config, writer);
            }catch(IOException | JsonIOException e){
                System.err.println("Failed to write flymod config file");
            }
        }, EXECUTOR);
    }
}
