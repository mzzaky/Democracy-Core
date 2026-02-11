package id.democracycore.utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import id.democracycore.DemocracyCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;

public class MessageUtils {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    private static final Map<String, String> messages = new HashMap<>();
    
    public static Component parse(String message) {
        if (message.contains("&")) {
            return legacy.deserialize(message);
        } else {
            return mm.deserialize(message);
        }
    }

    public static Component prefix() {
        return parse(DemocracyCore.getInstance().getPrefix() + " ");
    }
    
    public static void send(Player player, String message) {
        player.sendMessage(prefix().append(parse(message)));
    }
    
    public static void send(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            send(player, message);
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(prefix().append(parse(message)));
        } else {
            sender.sendMessage(prefix().append(parse(message)));
        }
    }
    
    public static void sendRaw(Player player, String message) {
        player.sendMessage(parse(message));
    }
    
    public static void broadcast(String message) {
        Component msg = prefix().append(parse(message));
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(msg));
        Bukkit.getConsoleSender().sendMessage(msg);
    }
    
    public static void broadcastRaw(String message) {
        Component msg = parse(message);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(msg));
        Bukkit.getConsoleSender().sendMessage(msg);
    }
    
    public static void broadcastAnnouncement(String title, String message) {
        Component border = Component.text("═".repeat(50)).color(NamedTextColor.GOLD);
        Component titleComp = Component.text("📜 " + title).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
        Component msgComp = parse(message);
        
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Component.empty());
            p.sendMessage(border);
            p.sendMessage(titleComp);
            p.sendMessage(msgComp);
            p.sendMessage(border);
            p.sendMessage(Component.empty());
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        });
    }
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Title.Times times = Title.Times.times(
            Duration.ofMillis(fadeIn * 50L),
            Duration.ofMillis(stay * 50L),
            Duration.ofMillis(fadeOut * 50L)
        );
        Title titleObj = Title.title(parse(title), parse(subtitle), times);
        player.showTitle(titleObj);
    }
    
    public static void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, title, subtitle, fadeIn, stay, fadeOut));
    }
    
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(parse(message));
    }
    
    public static void broadcastActionBar(String message) {
        Component msg = parse(message);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendActionBar(msg));
    }
    
    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
    
    public static String formatTimeShort(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " days";
        } else if (hours > 0) {
            return hours + " hours";
        } else if (minutes > 0) {
            return minutes + " minutes";
        } else {
            return seconds + " seconds";
        }
    }
    
    public static String formatNumber(double number) {
        if (number >= 1_000_000) {
            return String.format("%.2fM", number / 1_000_000);
        } else if (number >= 1_000) {
            return String.format("%.2fK", number / 1_000);
        }
        return String.format("%.0f", number);
    }
    
    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
    
    public static void broadcastSound(Sound sound) {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, 1.0f, 1.0f));
    }

    // Language system methods
    public static void loadLanguage() {
        messages.clear();
        loadMessagesFromConfig("", DemocracyCore.getInstance().getLanguageConfig());
    }

    public static void reloadLanguage() {
        loadLanguage();
    }

    private static void loadMessagesFromConfig(String prefix, org.bukkit.configuration.file.FileConfiguration config) {
        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
                messages.put(fullKey, config.getString(key));
            }
        }
    }

    public static String getMessage(String key) {
        return messages.getOrDefault(key, "&cMessage not found: " + key);
    }

    public static String getMessage(String key, Object... args) {
        String message = getMessage(key);
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                String placeholder = "{" + args[i] + "}";
                String value = String.valueOf(args[i + 1]);
                message = message.replace(placeholder, value);
            }
        }
        return message;
    }

    // Overloaded send methods for message keys
    public static void send(Player player, String key, Object... args) {
        send(player, getMessage(key, args));
    }

    public static void send(CommandSender sender, String key, Object... args) {
        send(sender, getMessage(key, args));
    }

    public static void broadcast(String key, Object... args) {
        broadcast(getMessage(key, args));
    }

    public static void broadcastRaw(String key, Object... args) {
        broadcastRaw(getMessage(key, args));
    }
}
