package org.advancedcharactersplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AdvancedCharacters extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        this.getCommand("character").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length > 1) {
                    createCharacter(player, args[1]);
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length > 2) {
                    if (args[1].equalsIgnoreCase("name")) {
                        setName(player, args[2]);
                    } else if (args[1].equalsIgnoreCase("age")) {
                        setAge(player, args[2]);
                    } else if (args[1].equalsIgnoreCase("story")) {
                        setStory(player, args[2]);
                    }
                }
            }
        }

        return true;
    }

    public void createCharacter(Player player, String playerName) {
        File userFile = new File(getDataFolder() + File.separator + "players" + File.separator + playerName + ".yml");

        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfiguration user = YamlConfiguration.loadConfiguration(userFile);

        user.set("Name", playerName);
        user.set("Age", "");
        user.set("Story", "");

        try {
            user.save(userFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setName(Player player, String name) {
        File userFile = new File(getDataFolder() + File.separator + "players" + File.separator + player.getName() + ".yml");

        if (userFile.exists()) {
            FileConfiguration user = YamlConfiguration.loadConfiguration(userFile);
            user.set("Name", ChatColor.translateAlternateColorCodes('&', name));

            try {
                user.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAge(Player player, String age) {
        File userFile = new File(getDataFolder() + File.separator + "players" + File.separator + player.getName() + ".yml");

        if (userFile.exists()) {
            FileConfiguration user = YamlConfiguration.loadConfiguration(userFile);
            user.set("Age", age);

            try {
                user.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStory(Player player, String story) {
        File userFile = new File(getDataFolder() + File.separator + "players"         + File.separator + player.getName() + ".yml");

        if (userFile.exists()) {
            FileConfiguration user = YamlConfiguration.loadConfiguration(userFile);
            user.set("Story", story);

            try {
                user.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Let's add admin commands and permissions
    @Override
    public boolean hasPermission(String name) {
        if (name.equalsIgnoreCase("characters.admin")) {
            return true;
        } else if (name.equalsIgnoreCase("characters.remove")) {
            return true;
        } else if (name.equalsIgnoreCase("characters.tokens")) {
            return true;
        }
        return false;
    }

    // Admin command to remove a character
    public void removeCharacter(String playerName) {
        File userFile = new File(getDataFolder() + File.separator + "players" + File.separator + playerName + ".yml");

        if (userFile.exists()) {
            userFile.delete();
        }
    }

    // Admin command to manage character tokens
    public void manageTokens(String action, String playerName, int amount) {
        File userFile = new File(getDataFolder() + File.separator + "players" + File.separator + playerName + ".yml");

        if (userFile.exists()) {
            FileConfiguration user = YamlConfiguration.loadConfiguration(userFile);

            int currentTokens = user.getInt("Tokens");

            if (action.equalsIgnoreCase("add")) {
                user.set("Tokens", currentTokens + amount);
            } else if (action.equalsIgnoreCase("set")) {
                user.set("Tokens", amount);
            } else if (action.equalsIgnoreCase("remove")) {
                user.set("Tokens", currentTokens - amount);
            }

            try {
                user.save(userFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
