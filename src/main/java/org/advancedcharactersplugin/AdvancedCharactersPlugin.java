package org.advancedcharactersplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedCharactersPlugin extends JavaPlugin {
    private static AdvancedCharactersPlugin instance;

    public AdvancedCharactersPlugin() {
    }

    public void onEnable() {
        instance = this;
        CharacterCommand characterCommand = new CharacterCommand();
        this.getCommand("character").setExecutor(characterCommand);
        this.getCommand("character").setTabCompleter(characterCommand);
        this.saveDefaultConfig();
        TokensCommand tokensCommand = new TokensCommand();
        this.getCommand("charactertokens").setExecutor(tokensCommand);
        this.getCommand("charactertokens").setTabCompleter(tokensCommand);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getLogger().info("AdvancedCharactersPlugin has been enabled!");
    }

    public void onDisable() {
        this.getLogger().info("AdvancedCharactersPlugin has been disabled!");
    }

    public static AdvancedCharactersPlugin getInstance() {
        return instance;
    }
}
package org.advancedcharactersplugin;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CharacterCommand implements CommandExecutor, TabCompleter {
    private Set<UUID> creationModePlayers = new HashSet();
    private Essentials essentials;

    public CharacterCommand() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (plugin != null && plugin instanceof Essentials) {
            this.essentials = (Essentials)plugin;
        } else {
            throw new RuntimeException("Essentials not found. This plugin requires Essentials.");
        }
    }

    private String getConfigMessage(String path) {
        String message = AdvancedCharactersPlugin.getInstance().getConfig().getString(path);
        return message != null ? ChatColor.translateAlternateColorCodes('&', message) : null;
    }

    private List<String> getConfigMessageList(String path) {
        List<String> messages = AdvancedCharactersPlugin.getInstance().getConfig().getStringList(path);
        return (List)messages.stream().map((message) -> {
            return ChatColor.translateAlternateColorCodes('&', message);
        }).collect(Collectors.toList());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.getConfigMessage("messages.only-players"));
            return true;
        } else {
            Player player = (Player)sender;
            UUID playerUUID = player.getUniqueId();
            boolean inCreationMode = this.creationModePlayers.contains(playerUUID);
            List var10000;
            if (args.length == 0) {
                var10000 = this.getConfigMessageList("messages.usage-character");
                Objects.requireNonNull(player);
                var10000.forEach(player::sendMessage);
                return true;
            } else {
                PlayerData playerData = new PlayerData(player);
                String field;
                int i;
                String value;
                String likes;
                String dislikes;
                String birthday;
                String family;
                String occupation;
                String traits;
                String currentStory;
                switch (args[0].toLowerCase()) {
                    case "create":
                        if (args.length == 2) {
                            if (!args[1].equals(player.getName()) && !player.hasPermission("characters.admin")) {
                                player.sendMessage(this.getConfigMessage("messages.no-permission"));
                            } else if (playerData.get().getInt("character.tokens") > 0) {
                                playerData.get().set("character.name", args[1]);
                                playerData.get().set("character.age", 0);
                                playerData.get().set("character.story", "");
                                playerData.get().set("character.likes", "");
                                playerData.get().set("character.dislikes", "");
                                playerData.get().set("character.birthday", "");
                                playerData.get().set("character.family", "");
                                playerData.get().set("character.occupation", "");
                                playerData.get().set("character.traits", "");
                                playerData.get().set("character.tokens", playerData.get().getInt("character.tokens") - 1);
                                playerData.save();
                                this.creationModePlayers.add(playerUUID);
                                var10000 = this.getConfigMessageList("messages.creation-mode-started");
                                Objects.requireNonNull(player);
                                var10000.forEach(player::sendMessage);
                            } else {
                                player.sendMessage(this.getConfigMessage("messages.not-enough-tokens"));
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-create");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                        break;
                    case "set":
                        if (args.length >= 3) {
                            if (inCreationMode) {
                                field = args[1].toLowerCase();
                                StringBuilder valueBuilder = new StringBuilder(args[2]);

                                for(int j = 3; j < args.length; ++j) {
                                    valueBuilder.append(" ").append(args[j]);
                                }

                                value = valueBuilder.toString();
                                switch (field) {
                                    case "age":
                                        try {
                                            int age = Integer.parseInt(value);
                                            if (age > 0) {
                                                playerData.get().set("character.age", age);
                                                playerData.save();
                                                player.sendMessage(ChatColor.GREEN + "Set your character's age to " + age + ".");
                                            } else {
                                                player.sendMessage(this.getConfigMessage("messages.invalid-age"));
                                            }

                                            return true;
                                        } catch (NumberFormatException var20) {
                                            player.sendMessage(this.getConfigMessage("messages.age-number"));
                                            return true;
                                        }
                                    case "birthday":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.birthday", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's birthday to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-birthday"));
                                        }

                                        return true;
                                    case "likes":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.likes", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's likes to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-likes"));
                                        }

                                        return true;
                                    case "dislikes":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.dislikes", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's dislikes to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-dislikes"));
                                        }

                                        return true;
                                    case "family":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.family", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's family to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-family"));
                                        }

                                        return true;
                                    case "occupation":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.occupation", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's occupation to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-occupation"));
                                        }

                                        return true;
                                    case "traits":
                                        if (!value.isEmpty()) {
                                            playerData.get().set("character.traits", value);
                                            playerData.save();
                                            player.sendMessage(ChatColor.GREEN + "Set your character's traits/personality to " + value + ".");
                                        } else {
                                            player.sendMessage(this.getConfigMessage("messages.invalid-traits"));
                                        }

                                        return true;
                                    default:
                                        player.sendMessage(this.getConfigMessage("messages.unknown-field"));
                                }
                            } else {
                                player.sendMessage(this.getConfigMessage("messages.creation-mode-required"));
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-set");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                        break;
                    case "name":
                        if (args.length == 2) {
                            if (inCreationMode) {
                                field = args[1];
                                currentStory = ChatColor.translateAlternateColorCodes('&', field);
                                User user = this.essentials.getUser(player);
                                if (user != null) {
                                    user.setNickname(currentStory);
                                }

                                player.setPlayerListName(currentStory);
                                playerData.get().set("character.name", currentStory);
                                playerData.save();
                                player.sendMessage(ChatColor.GREEN + "Set your character's name to " + currentStory + ChatColor.GREEN + ".");
                            } else {
                                player.sendMessage(this.getConfigMessage("messages.creation-mode-required"));
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-name");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                        break;
                    case "addstory":
                        if (args.length >= 2) {
                            if (inCreationMode) {
                                StringBuilder storyPart = new StringBuilder();

                                for(i = 1; i < args.length; ++i) {
                                    storyPart.append(args[i]).append(" ");
                                }

                                currentStory = playerData.get().getString("character.story");
                                currentStory = currentStory + storyPart.toString();
                                playerData.get().set("character.story", currentStory);
                                playerData.save();
                                player.sendMessage(this.getConfigMessage("messages.story-added"));
                            } else {
                                player.sendMessage(this.getConfigMessage("messages.creation-mode-required"));
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-addstory");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                        break;
                    case "complete":
                        if (inCreationMode) {
                            field = playerData.get().getString("character.name");
                            i = playerData.get().getInt("character.age");
                            value = playerData.get().getString("character.story");
                            likes = playerData.get().getString("character.dislikes");
                            dislikes = playerData.get().getString("character.likes");
                            birthday = playerData.get().getString("character.birthday");
                            family = playerData.get().getString("character.family");
                            occupation = playerData.get().getString("character.occupation");
                            traits = playerData.get().getString("character.traits");
                            if (!field.isEmpty() && i > 0 && !value.isEmpty()) {
                                this.creationModePlayers.remove(playerUUID);
                                player.sendMessage(this.getConfigMessage("messages.creation-completed"));
                            } else {
                                player.sendMessage(this.getConfigMessage("messages.complete-requirements"));
                            }
                        } else {
                            player.sendMessage(this.getConfigMessage("messages.creation-mode-required"));
                        }
                        break;
                    case "remove":
                        if (args.length == 2) {
                            if (!args[1].equals(player.getName()) && !player.hasPermission("characters.admin")) {
                                player.sendMessage(this.getConfigMessage("messages.no-permission"));
                            } else {
                                playerData.get().set("character.name", (Object)null);
                                playerData.get().set("character.story", (Object)null);
                                playerData.get().set("character.age", (Object)null);
                                playerData.get().set("character.birthday", (Object)null);
                                playerData.get().set("character.family", (Object)null);
                                playerData.get().set("character.occupation", (Object)null);
                                playerData.get().set("character.likes", (Object)null);
                                playerData.get().set("character.dislikes", (Object)null);
                                playerData.get().set("character.traits", (Object)null);
                                playerData.save();
                                User user = this.essentials.getUser(player);
                                if (user != null) {
                                    user.setNickname((String)null);
                                }

                                player.setPlayerListName(player.getName());
                                player.sendMessage(this.getConfigMessage("messages.character-removed"));
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-remove");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                        break;
                    case "info":
                        if (args.length == 2) {
                            if (!args[1].equals(player.getName()) && !player.hasPermission("characters.info")) {
                                player.sendMessage(this.getConfigMessage("messages.no-permission"));
                            } else {
                                field = playerData.get().getString("character.name");
                                i = playerData.get().getInt("character.age");
                                value = playerData.get().getString("character.story");
                                likes = playerData.get().getString("character.likes");
                                dislikes = playerData.get().getString("character.dislikes");
                                birthday = playerData.get().getString("character.birthday");
                                family = playerData.get().getString("character.family");
                                occupation = playerData.get().getString("character.occupation");
                                traits = playerData.get().getString("character.traits");
                                if (field != null && !field.isEmpty() && i > 0 && value != null && !value.isEmpty()) {
                                    player.sendMessage(ChatColor.RED + "Character Name: " + ChatColor.WHITE + field);
                                    player.sendMessage(ChatColor.RED + "Character Age: " + ChatColor.WHITE + i);
                                    player.sendMessage(ChatColor.RED + "Character Story: " + ChatColor.WHITE + value);
                                    player.sendMessage(ChatColor.RED + "Likes: " + ChatColor.WHITE + (likes != null ? likes : ""));
                                    player.sendMessage(ChatColor.RED + "Dislikes: " + ChatColor.WHITE + (dislikes != null ? dislikes : ""));
                                    player.sendMessage(ChatColor.RED + "Birthday: " + ChatColor.WHITE + (birthday != null ? birthday : ""));
                                    player.sendMessage(ChatColor.RED + "Family: " + ChatColor.WHITE + (family != null ? family : ""));
                                    player.sendMessage(ChatColor.RED + "Occupation: " + ChatColor.WHITE + (occupation != null ? occupation : ""));
                                    player.sendMessage(ChatColor.RED + "Traits/Personality: " + ChatColor.WHITE + (traits != null ? traits : ""));
                                } else {
                                    player.sendMessage(this.getConfigMessage("messages.no-character"));
                                }
                            }
                        } else {
                            var10000 = this.getConfigMessageList("messages.usage-info");
                            Objects.requireNonNull(player);
                            var10000.forEach(player::sendMessage);
                        }
                }

                return true;
            }
        }
    }

    public void askForConfirmation(Player player, String field, String value) {
        String confirmCommand = "/character confirm " + field + " " + value;
        String denyCommand = "/character deny " + field;
        String jsonMessage = String.format(
                "{\"text\":\"Is '%s' your correct %s? \",\"extra\":[{\"text\":\"[Confirm]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"%s\"}},{\"text\":\" [Deny]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"%s\"}}]}",
                value, field, confirmCommand, denyCommand
        );
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonMessage);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "set", "name", "addstory", "complete", "remove", "info");
        } else {
            if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "set":
                        return Arrays.asList("age", "likes", "dislikes", "birthday", "family", "occupation", "traits");
                    case "remove":
                        return Arrays.asList((String[])Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray((x$0) -> {
                            return new String[x$0];
                        }));
                    case "info":
                        return Arrays.asList((String[])Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray((x$0) -> {
                            return new String[x$0];
                        }));
                }
            }

            return null;
        }
    }
}
package org.advancedcharactersplugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerData {
    private final Player player;
    private File file;
    private FileConfiguration playerFile;

    public PlayerData(Player player) {
        this.player = player;
        this.file = new File(AdvancedCharactersPlugin.getInstance().getDataFolder(), player.getUniqueId().toString() + ".yml");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException var3) {
                System.out.println("Could not create file " + this.file.getName());
            }
        }

        this.playerFile = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration get() {
        return this.playerFile;
    }

    public void save() {
        try {
            this.playerFile.save(this.file);
        } catch (IOException var2) {
            System.out.println("Could not save file " + this.file.getName());
        }

    }

    public void reload() {
        this.playerFile = YamlConfiguration.loadConfiguration(this.file);
    }
}
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.advancedcharactersplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    public PlayerJoin() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerData playerData = new PlayerData(event.getPlayer());
        String name = playerData.get().getString("character.name");
        if (name != null && !name.isEmpty()) {
            event.getPlayer().setDisplayName(name);
            event.getPlayer().setPlayerListName(name);
        }

    }
}
