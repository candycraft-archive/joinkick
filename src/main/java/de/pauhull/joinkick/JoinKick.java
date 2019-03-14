package de.pauhull.joinkick;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 13.03.2019
 *
 * @author pauhull
 */
public class JoinKick extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        PermissionGroup group = getGroup(event.getUniqueId());

        if (!TimoCloudAPI.getBukkitAPI().getThisServer().getState().equals("ONLINE")) {
            return;
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() < Bukkit.getMaxPlayers()) {
            return;
        }

        Collections.shuffle(players);
        Player playerToKick = null;
        PermissionGroup playerToKickGroup = null;

        for (Player check : players) {

            PermissionGroup checkGroup = getGroup(check.getUniqueId());
            if (checkGroup.getRank() <= group.getRank()) { // dont kick a player that has a higher rank than you
                continue;
            }

            if (playerToKickGroup == null || playerToKickGroup.getRank() < checkGroup.getRank()) {
                playerToKickGroup = checkGroup;
                playerToKick = check;
            }
        }

        if (playerToKick != null) {
            final Player kick = playerToKick;
            Bukkit.getScheduler().runTask(this, () -> {
                kick.kickPlayer("§f[§dCandyCraft§f] §cDu wurdest gekickt, um einem " + getColorCode(ChatColor.translateAlternateColorCodes('&', group.getPrefix())) + group.getName() + "§c Platz zu machen.");
            });
        }
    }

    private PermissionGroup getGroup(UUID uuid) {
        PermissionGroup highest = null;
        for (PermissionGroup group : PermissionsEx.getPermissionManager().getUser(uuid).getGroups()) {
            if (highest == null || group.getRank() < highest.getRank()) {
                highest = group;
            }
        }
        return highest;
    }

    private ChatColor getColorCode(String s) {
        return ChatColor.getByChar(s.charAt(1));
    }

}
