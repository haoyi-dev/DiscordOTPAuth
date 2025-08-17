package haoyidev.discordotpauth.commands;

import haoyidev.discordotpauth.DiscordOTPAuth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OTPCommand implements CommandExecutor {
    private final DiscordOTPAuth plugin;

    public OTPCommand(DiscordOTPAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cChỉ người chơi mới có thể sử dụng lệnh này!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "send":
                if (args.length < 2) {
                    player.sendMessage("§cSử dụng: /otp send <discord_id>");
                    return true;
                }
                handleSendOTP(player, args[1]);
                break;
            case "verify":
            default:
                if (args.length < 1) {
                    player.sendMessage("§cSử dụng: /otp <mã_otp>");
                    return true;
                }
                handleVerifyOTP(player, args[0]);
                break;
        }

        return true;
    }

    private void handleSendOTP(Player player, String discordId) {
        if (!discordId.matches("\\d+")) {
            player.sendMessage("§cDiscord ID phải là một chuỗi số!");
            return;
        }

        plugin.getAuthManager().requestAuth(player, discordId);
    }

    private void handleVerifyOTP(Player player, String otp) {
        plugin.getAuthManager().verifyOTP(player, otp);
    }

    private void sendUsage(Player player) {
        player.sendMessage("§6=== Discord OTP Auth ===");
        player.sendMessage("§e/otp send <discord_id> §7- Gửi mã OTP đến Discord");
        player.sendMessage("§e/otp <mã_otp> §7- Xác thực với mã OTP");
    }
}
