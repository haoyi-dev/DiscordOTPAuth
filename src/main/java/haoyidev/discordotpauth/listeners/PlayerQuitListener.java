package haoyidev.discordotpauth.listeners;

import haoyidev.discordotpauth.DiscordOTPAuth;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final DiscordOTPAuth plugin;

    public PlayerQuitListener(DiscordOTPAuth plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        plugin.getAuthManager().isPendingAuth(event.getPlayer().getUniqueId());
    }
}
