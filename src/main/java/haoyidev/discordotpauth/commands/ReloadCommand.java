package haoyidev.discordotpauth.commands;

import haoyidev.discordotpauth.DiscordOTPAuth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final DiscordOTPAuth plugin;

    public ReloadCommand(DiscordOTPAuth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("discordotp.reload")) {
            sender.sendMessage("§cBạn không có quyền sử dụng lệnh này!");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sendUsage(sender);
            return true;
        }

        plugin.getLogger().info("Đang reload DiscordOTPAuth plugin...");
        sender.sendMessage("§aĐang reload DiscordOTPAuth plugin...");

        try {
            plugin.getConfigManager().reloadConfig();

            plugin.getDiscordManager().reload();

            sender.sendMessage("§aDiscordOTPAuth đã được reload thành công!");
            plugin.getLogger().info("DiscordOTPAuth đã được reload thành công!");
        } catch (Exception e) {
            sender.sendMessage("§cLỗi khi reload plugin: " + e.getMessage());
            plugin.getLogger().severe("Lỗi khi reload plugin: " + e.getMessage());
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6=== DiscordOTPAuth Commands ===");
        sender.sendMessage("§e/discordotp reload §7- Reload lại plugin");
    }
}
