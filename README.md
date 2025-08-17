# DiscordOTPAuth - Minecraft Plugin

Plugin đăng nhập bằng OTP qua Discord cho SvMC 1.21.1

## Tính năng
- Xác thực người chơi qua mã OTP gửi qua Discord
- Ghi log đầy đủ về các lần đăng nhập
- Hỗ trợ nhiều người chơi cùng lúc
- Mã OTP có thời hạn 5 phút
- Code tối ưu, dễ đọc và bảo trì

## Cài đặt

1. **Tạo Discord Bot:**
   - Vào [Discord Developer Portal](https://discord.com/developers/applications)
   - Tạo ứng dụng mới
   - Tạo bot và lấy token
   - Bật "Message Content Intent" trong bot settings

2. **Cài đặt plugin:**
   - Build plugin với Maven: `mvn clean package`
   - Copy file `target/DiscordOTPAuth-1.0.0.jar` vào thư mục `plugins` của server
   - Khởi động server để tạo file config

3. **Cấu hình:**
   - Mở file `plugins/DiscordOTPAuth/config.yml`
   - Thay `YOUR_DISCORD_BOT_TOKEN_HERE` bằng token bot của bạn
   - Restart server

## Sử dụng

1. **Người chơi mới vào server:**
   - Server sẽ kick và yêu cầu xác thực
   - Dùng lệnh: `/otp send <discord_id>` (ví dụ: `/otp send 123456789012345678`)
   - Kiểm tra tin nhắn riêng từ bot trên Discord

2. **Xác thực:**
   - Dùng lệnh: `/otp <mã_otp>` (ví dụ: `/otp 123456`)
   - Nếu đúng sẽ được vào server
   - Nếu sai thì cút mẹ mày đi

## Lệnh
- `/otp send <discord_id>` - Gửi mã OTP đến Discord
- `/otp <mã_otp>` - Xác thực với mã OTP

## Log
- File log được lưu tại: `plugins/DiscordOTPAuth/auth.log`
- Ghi lại: thời gian, tên người chơi, IP, Discord ID, OTP, trạng thái

## Creator
- Author: haoyidev
- Group ID: haoyidev
- Minecraft: 1.21.1 PaperMC
