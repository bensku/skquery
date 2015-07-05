package com.w00tmast3r.skquery.util.minecraft;

import java.util.ArrayList;

import com.w00tmast3r.skquery.util.Reflection;

import me.virustotal.utils.ServerUtils;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkFactory {

    private static ArrayList<Player> players = new ArrayList<Player>();
    static
    {
    	for(Player player : ServerUtils.getOnlinePlayers())
    	{
    		players.add(player);
    	}
    }
    private FireworkEffect[] effects = null;
    private Location loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

    public FireworkFactory() {
    }

    public FireworkFactory players(Player... players) {
        FireworkFactory.players.clear();
        for(Player p : players)
        {
        	FireworkFactory.players.add(p);
        }
        return this;
    }

    public FireworkFactory effects(FireworkEffect... effects) {
        this.effects = effects;
        return this;
    }

    public FireworkFactory location(Location loc) {
        this.loc = loc;
        return this;
    }

    public void play() {
        Object packet = constructPacket(loc, effects);
        ArrayList<Player> myPlayers = FireworkFactory.players;
        Player[] ar = new Player[myPlayers.size()];
        for(int i = 0; i < myPlayers.size(); i++)
        {
        	ar[i] = myPlayers.get(i);
        }
        Reflection.sendPacket(packet, ar);
    }

    private static Object constructPacket(Location loc, FireworkEffect... effects) {
        try {
            Firework firework = loc.getWorld().spawn(loc, Firework.class);
            FireworkMeta data = firework.getFireworkMeta();
            data.clearEffects();
            data.setPower(1);
            for (FireworkEffect f : effects) {
                data.addEffect(f);
            }
            firework.setFireworkMeta(data);
            Object nmsFirework = firework.getClass().getMethod("getHandle").invoke(firework);
            firework.remove();
            return Reflection.nmsClass("PacketPlayOutEntityStatus").getConstructor(Reflection.nmsClass("Entity"), byte.class).newInstance(nmsFirework, (byte) 17);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}