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
        configFile = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("flymod" + ".json");
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
