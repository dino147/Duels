package me.realized.duels.hook.hooks;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.PowerLossEvent;
import me.realized.duels.DuelsPlugin;
import me.realized.duels.arena.ArenaManager;
import me.realized.duels.config.Config;
import me.realized.duels.util.Log;
import me.realized.duels.util.compat.ReflectionUtil;
import me.realized.duels.util.hook.PluginHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionsHook extends PluginHook<DuelsPlugin> {

    private final Config config;
    private final ArenaManager arenaManager;

    public FactionsHook(final DuelsPlugin plugin) {
        super(plugin, "Factions");
        this.config = plugin.getConfiguration();
        this.arenaManager = plugin.getArenaManager();

        Listener listener = null;

        if (ReflectionUtil.getClassUnsafe("com.massivecraft.factions.event.PowerLossEvent") != null) {
            listener = new FactionsUUIDListener();
        } else if (ReflectionUtil.getClassUnsafe(("com.massivecraft.factions.event.EventFactionsPowerChange")) != null) {
            listener = new Factions2Listener();
        }

        if (listener == null) {
            Log.error("Could not detect this version of Factions. Please contact the developer if you believe this is an error.");
            return;
        }

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public class Factions2Listener implements Listener {

        @EventHandler
        public void on(final EventFactionsPowerChange event) {
            if (!config.isFNoPowerLoss()) {
                return;
            }

            final MPlayer mPlayer = event.getMPlayer();
            final Player player = mPlayer.getPlayer();

            if (!arenaManager.isInMatch(player)) {
                return;
            }

            event.setCancelled(true);
        }
    }

    public class FactionsUUIDListener implements Listener {

        @EventHandler
        public void on(final PowerLossEvent event) {
            if (!config.isFuNoPowerLoss()) {
                return;
            }

            final Player player = event.getfPlayer().getPlayer();

            if (!arenaManager.isInMatch(player)) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
