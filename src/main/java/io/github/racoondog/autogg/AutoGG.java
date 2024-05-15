package io.github.racoondog.autogg;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class AutoGG implements ClientModInitializer {
    public static AutoGGConfig config = new AutoGGConfig(true, true, true, true);
    private final Logger LOGGER = LogManager.getLogger();
    private final Gson data = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
    private final Path configPath = Paths.get("config/autogg.json");

    public void saveData() {
        if (Files.exists(this.configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(this.configPath)) {
                config = this.data.fromJson(reader, AutoGGConfig.class);
            } catch (IOException e) {
                LOGGER.error("Error reading config", e);
            }
        } else {
            try {
                Files.write(this.configPath, Collections.singleton(this.data.toJson(config)));
            } catch (IOException e) {
                LOGGER.error("Error writing config", e);
            }
        }
    }

    @Override
    public void onInitializeClient() {
        this.saveData();
    }
}
