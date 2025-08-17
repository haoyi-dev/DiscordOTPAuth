package haoyidev.discordotpauth.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public class DiscordManager {
    private final String botToken;
    private JDA jda;

    public DiscordManager(String botToken) {
        this.botToken = botToken;
    }

    public boolean initialize() {
        try {
            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                    .build();
            jda.awaitReady();
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Khong khoi tao duoc bot discord: " + e.getMessage());
            return false;
        }
    }

    public boolean reload() {
        try {
            if (jda != null) {
                jda.shutdown();
                jda = null;
            }

            String newToken = haoyidev.discordotpauth.DiscordOTPAuth.getInstance()
                    .getConfigManager()
                    .getBotToken();

            if (newToken == null || newToken.isEmpty()) {
                Bukkit.getLogger().severe("Bot token not found in config!");
                return false;
            }

            return initialize();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Loi khong the reload lai bot discord: " + e.getMessage());
            return false;
        }
    }

    public CompletableFuture<Boolean> sendOTP(String discordId, String otp) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = jda.retrieveUserById(discordId).complete();
                if (user != null) {
                    user.openPrivateChannel().queue(channel -> {
                        channel.sendMessage("🔐 **Mã OTP của bạn là: " + otp + "**\n" +
                                "Sử dụng lệnh `/otp " + otp + "` trong game để đăng nhập.\n" +
                                "Mã này sẽ hết hạn sau 5 phút.").queue();
                    });
                    return true;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Loi khong the gui duoc otp den discord nguoi dung " + discordId + ": " + e.getMessage());
            }
            return false;
        });
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public boolean isUserValid(String discordId) {
        try {
            User user = jda.retrieveUserById(discordId).complete();
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }
}
