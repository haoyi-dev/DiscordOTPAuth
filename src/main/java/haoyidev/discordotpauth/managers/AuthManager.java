package haoyidev.discordotpauth.managers;

import haoyidev.discordotpauth.DiscordOTPAuth;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AuthManager {
    private final DiscordOTPAuth plugin;
    private final Map<UUID, AuthSession> pendingAuths = new ConcurrentHashMap<>();
    private final Set<UUID> restrictedPlayers = ConcurrentHashMap.newKeySet();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuthManager(DiscordOTPAuth plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    public void requestAuth(Player player, String discordId) {
        if (!plugin.getDiscordManager().isConnected()) {
            player.kickPlayer("§cDiscord bot đang offline! Vui lòng liên hệ admin.");
            return;
        }

        if (!isValidDiscordId(discordId)) {
            player.kickPlayer("§cDiscord ID không hợp lệ! Vui lòng kiểm tra lại.");
            return;
        }

        if (!plugin.getDiscordManager().isUserValid(discordId)) {
            player.kickPlayer("§cDiscord ID không tồn tại! Vui lòng kiểm tra lại.");
            return;
        }

        String otp = generateOTP();
        pendingAuths.put(player.getUniqueId(), new AuthSession(discordId, otp, System.currentTimeMillis()));
        restrictedPlayers.add(player.getUniqueId());

        plugin.getDiscordManager().sendOTP(discordId, otp).thenAccept(sent -> {
            if (sent) {
                player.sendMessage("§aĐã gửi mã OTP đến Discord của bạn! Vui lòng kiểm tra tin nhắn riêng.");
                logAuthAttempt(player, discordId, otp, "OTP_SENT");
            } else {
                player.kickPlayer("§cKhông thể gửi OTP! Vui lòng liên hệ admin.");
            }
        });
    }

    public boolean verifyOTP(Player player, String inputOtp) {
        AuthSession session = pendingAuths.get(player.getUniqueId());
        if (session == null) {
            player.sendMessage("§cBạn chưa yêu cầu OTP!");
            return false;
        }

        if (System.currentTimeMillis() - session.getTimestamp() > 300000) { // 5 phút
            pendingAuths.remove(player.getUniqueId());
            restrictedPlayers.remove(player.getUniqueId());
            player.kickPlayer("§cMã OTP đã hết hạn!");
            logAuthAttempt(player, session.getDiscordId(), inputOtp, "EXPIRED");
            return false;
        }

        if (session.getOtp().equals(inputOtp)) {
            pendingAuths.remove(player.getUniqueId());
            restrictedPlayers.remove(player.getUniqueId());
            player.sendMessage("§aXác thực thành công! Chào mừng đến với server.");
            logAuthAttempt(player, session.getDiscordId(), inputOtp, "SUCCESS");
            return true;
        } else {
            player.sendMessage("§cMã OTP không đúng! Vui lòng thử lại.");
            logAuthAttempt(player, session.getDiscordId(), inputOtp, "FAILED");
            return false;
        }
    }

    public boolean isPendingAuth(UUID playerId) {
        return pendingAuths.containsKey(playerId);
    }

    public void addRestrictedPlayer(UUID playerId) {
        restrictedPlayers.add(playerId);
    }

    public void removeRestrictedPlayer(UUID playerId) {
        restrictedPlayers.remove(playerId);
        pendingAuths.remove(playerId);
    }

    public boolean isPlayerRestricted(UUID playerId) {
        return restrictedPlayers.contains(playerId);
    }

    private boolean isValidDiscordId(String discordId) {
        try {
            Long.parseLong(discordId);
            return discordId.length() >= 17 && discordId.length() <= 19;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String generateOTP() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void logAuthAttempt(Player player, String discordId, String otp, String status) {
        String logEntry = String.format("[%s] Người Chơi: %s, IPv4: %s, Discord: %s, OTP: %s, Tình Trạng: %s",
                LocalDateTime.now().format(formatter),
                player.getName(),
                player.getAddress().getAddress().getHostAddress(),
                discordId,
                otp,
                status);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.getConfigManager().getLogFilePath(), true))) {
                writer.write(logEntry);
                writer.newLine();
            } catch (IOException e) {
                plugin.getLogger().warning("Không thể ghi vào log auth: " + e.getMessage());
            }
        });
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            pendingAuths.entrySet().removeIf(entry -> {
                if (currentTime - entry.getValue().getTimestamp() > 300000) {
                    restrictedPlayers.remove(entry.getKey());
                    return true;
                }
                return false;
            });
        }, 6000L, 6000L); // Chạy mỗi 5 phút
    }

    private static class AuthSession {
        private final String discordId;
        private final String otp;
        private final long timestamp;

        public AuthSession(String discordId, String otp, long timestamp) {
            this.discordId = discordId;
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public String getDiscordId() {
            return discordId;
        }

        public String getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
