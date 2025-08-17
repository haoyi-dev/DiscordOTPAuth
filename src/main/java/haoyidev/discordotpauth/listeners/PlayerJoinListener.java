package haoyidev.discordotpauth.listeners;

import haoyidev.discordotpauth.DiscordOTPAuth;
import haoyidev.discordotpauth.managers.IPManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final DiscordOTPAuth plugin;

    public PlayerJoinListener(DiscordOTPAuth plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        IPManager ipManager = plugin.getIpManager();
        ipManager.recordPlayerIP(event.getPlayer());
    }
}
