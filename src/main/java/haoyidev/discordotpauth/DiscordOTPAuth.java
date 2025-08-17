package haoyidev.discordotpauth;

import haoyidev.discordotpauth.commands.OTPCommand;
import haoyidev.discordotpauth.commands.ReloadCommand;
import haoyidev.discordotpauth.listeners.PlayerJoinListener;
import haoyidev.discordotpauth.listeners.PlayerQuitListener;
import haoyidev.discordotpauth.managers.AuthManager;
import haoyidev.discordotpauth.managers.ConfigManager;
import haoyidev.discordotpauth.managers.DiscordManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordOTPAuth extends JavaPlugin implements Listener {
    private static DiscordOTPAuth instance;
    private ConfigManager configManager;
    private DiscordManager discordManager;
    private AuthManager authManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        if (!configManager.loadConfig()) {
            getLogger().severe("Khong load duoc config plugin, plugin đang tat...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        discordManager = new DiscordManager(configManager.getBotToken());
        if (!discordManager.initialize()) {
            getLogger().severe("Khong load duoc bot discord, plugin đang tat...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        authManager = new AuthManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(this, this);
        // Gao Dep Chai
        getCommand("otp").setExecutor(new OTPCommand(this));
        getCommand("discordotp").setExecutor(new ReloadCommand(this));
        getLogger().info("   __ __               _   ___          ");
        getLogger().info("  / // /__ ____  __ __(_) / _ \\___ _  __");
        getLogger().info(" / _  / _ `/ _ \\/ // / / / // / -_) |/ /");
        getLogger().info("/_//_/\\_,_/\\___/\\_, /_/ /____/\\__/|___/ ");
        getLogger().info("               /___/                    ");
        getLogger().info("DiscordOTPAuth đã được bật!!");
        getLogger().info("===================================");
        getLogger().info("Cảm ơn bạn đã sử dụng plugin.:");
        getLogger().info("Author: Haoyi Developer");
        getLogger().info("Version: 1.0.0");
        getLogger().info("Description: Plugin này giúp bạn đăng nhập bằng OTP thay vì Password");
        getLogger().info("===================================");
    }

    @Override
    public void onDisable() {
        if (discordManager != null) {
            discordManager.shutdown();
        }
        getLogger().info("DiscordOTPAuth đã được tắt!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (authManager.isPlayerRestricted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (authManager.isPlayerRestricted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§6[OTP] §cBạn cần xác thực trước khi chat!");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (authManager.isPlayerRestricted(event.getPlayer().getUniqueId())) {
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/otp")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§6[OTP] §cBạn cần xác thực trước khi sử dụng lệnh!");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        authManager.removeRestrictedPlayer(event.getPlayer().getUniqueId());
    }

    public static DiscordOTPAuth getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}
