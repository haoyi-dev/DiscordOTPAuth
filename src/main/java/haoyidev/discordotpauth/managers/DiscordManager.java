package haoyidev.discordotpauth.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DiscordManager {
    private final String botToken;
    private JDA jda;
    private boolean isConnected = false;

    public DiscordManager(String botToken) {
        this.botToken = botToken;
    }

    public boolean initialize() {
        if (botToken == null || botToken.trim().isEmpty()) {
            Bukkit.getLogger().severe("Token bot trống, vui lòng thêm token bot trong config.yml!");
            return false;
        }

        try {
            jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER)
                    .setAutoReconnect(true)
                    .setRequestTimeoutRetry(true)
                    .build();

            jda.awaitReady();
            isConnected = true;
            Bukkit.getLogger().info("Discord Bot kết nối với plugin thành công!");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Lỗi không thể kết nối đến Bot Discord!: " + e.getMessage(), e);
            isConnected = false;
            return false;
        }
    }

    public boolean reload() {
        shutdown();
        return initialize();
    }

    public CompletableFuture<Boolean> sendOTP(String discordId, String otp) {
        if (!isConnected) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = jda.retrieveUserById(discordId).submit().get(5, TimeUnit.SECONDS);
                if (user != null) {
                    user.openPrivateChannel().queue(channel -> {
                        channel.sendMessage("🔐 **Mã OTP của bạn là: " + otp + "**\n" +
                                "Sử dụng lệnh `/otp " + otp + "` trong game để đăng nhập.\n" +
                                "Mã này sẽ hết hạn sau 5 phút.").queue(
                                success -> {},
                                failure -> Bukkit.getLogger().warning("Không thể gửi được otp tới user " + discordId + ": " + failure.getMessage())
                        );
                    }, failure -> Bukkit.getLogger().warning("Lỗi không thể mở kênh riêng tư cho" + discordId + ": " + failure.getMessage()));
                    return true;
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Lỗi không thể gửi được OTP " + discordId + ": " + e.getMessage());
            }
            return false;
        });
    }

    public boolean isUserValid(String discordId) {
        if (!isConnected) return false;
        
        try {
            User user = jda.retrieveUserById(discordId).submit().get(3, TimeUnit.SECONDS);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void shutdown() {
        if (jda != null) {
            try {
                jda.shutdown();
                jda.awaitShutdown(5, TimeUnit.SECONDS);
                isConnected = false;
                Bukkit.getLogger().info("Bot Discord đã ngắt kết nối!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("Lỗi trong quá trình tắt bot Discord: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return isConnected && jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
}
