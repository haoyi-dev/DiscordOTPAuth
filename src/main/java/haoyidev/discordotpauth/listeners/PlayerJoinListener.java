package haoyidev.discordotpauth.listeners;

import haoyidev.discordotpauth.DiscordOTPAuth;
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
        event.setJoinMessage(null);
        if (!plugin.getAuthManager().isPendingAuth(event.getPlayer().getUniqueId())) {
            plugin.getAuthManager().addRestrictedPlayer(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage("§6[OTP] Bạn chưa xác thực!");
            event.getPlayer().sendMessage("§6[OTP] $f/otp send <discord_id> -> Gửi OTP về Discord");
            event.getPlayer().sendMessage("§6[OTP] $f/otp <mã_otp> -> Nhập mã OTP trong game");
            event.getPlayer().sendMessage("§6[OTP] $7Bạn sẽ bị giới hạn hành động cho đến khi xác thực!");
        }
    }
}
