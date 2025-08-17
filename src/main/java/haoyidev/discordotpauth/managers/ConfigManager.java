package haoyidev.discordotpauth.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private String botToken;
    private String logFilePath;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        botToken = config.getString("discord.bot-token");
        logFilePath = config.getString("logging.file-path", "plugins/DiscordOTPAuth/auth.log");

        if (botToken == null || botToken.isEmpty()) {
            plugin.getLogger().severe("Khong tim thay token bot trong config.yml!");
            return false;
        }

        return true;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void saveConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Khong luu duoc file config.yml: " + e.getMessage());
        }
    }

    public boolean reloadConfig() {
        try {
            // Reload config day thang lon
            plugin.reloadConfig();
            loadConfig();
            plugin.getLogger().info("Config da duoc reload lại!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Config khong reload lai duoc: " + e.getMessage());
            return false;
        }
    }
}
