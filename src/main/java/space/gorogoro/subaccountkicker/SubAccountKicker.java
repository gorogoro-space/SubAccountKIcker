package space.gorogoro.subaccountkicker;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SubAccountKicker extends JavaPlugin implements Listener{

  @Override
  public void onEnable(){
    try{
      getLogger().info("The Plugin Has Been Enabled!");
      getServer().getPluginManager().registerEvents(this, this);

      File configFile = new File(getDataFolder(), "config.yml");
      if(!configFile.exists()){
        saveDefaultConfig();
      }

      int intervalSeconds = getConfig().getInt("interval-seconds");
      String msgKickIp = getConfig().getString("kick-ip-message");
      String msgKickUuid = getConfig().getString("kick-uuid-message");
      getServer().getScheduler().runTaskTimer(this, new Runnable() {
        public void run() {
          int count;
          String ipAddress;
          String msgLog;
          String uuid;
          HashMap<String,Integer> mapAddress = new HashMap<>();
          HashMap<String,Integer> mapUuid = new HashMap<>();
          for(Player p:getServer().getOnlinePlayers()) {
            if(p.isOp()) {
              continue;
            }
            ipAddress = p.getAddress().getAddress().getHostAddress().toString();
            uuid = p.getUniqueId().toString();

            count = 1;
            if(mapAddress.containsKey(ipAddress)) {
              count = mapAddress.get(ipAddress);
              count++;
            }
            mapAddress.put(ipAddress, count);

            count = 1;
            if(mapUuid.containsKey(uuid)) {
              count = mapUuid.get(uuid);
              count++;
            }
            mapUuid.put(uuid, count);

            if(getServer().getOnlinePlayers().size() < getServer().getMaxPlayers()) {
              continue;
            }

            if(mapAddress.get(ipAddress) <= 1 && mapUuid.get(uuid) <= 1) {
              continue;
            }

            if(mapAddress.get(ipAddress) > 1) {
              for(Player sp:getServer().getOnlinePlayers()) {
                if(!sp.getAddress().getAddress().getHostAddress().toString().equals(ipAddress)) {
                  continue;
                }
                msgLog = "Kick sub account. ";
                if(sp.getEntityId() > p.getEntityId()) {
                  sp.kickPlayer(msgKickIp);
                  msgLog += "Player(ipAddress)=" + sp.getName();
                } else {
                  p.kickPlayer(msgKickIp);
                  msgLog += "Player(ipAddress)=" + p.getName();
                }
                getLogger().info(msgLog);
              }
            }

            if(mapUuid.get(uuid) > 1) {
              for(Player sp:getServer().getOnlinePlayers()) {
                if(!sp.getUniqueId().toString().equals(uuid)) {
                  continue;
                }
                msgLog = "Kick sub account. ";
                if(sp.getEntityId() > p.getEntityId()) {
                  sp.kickPlayer(msgKickUuid);
                  msgLog += "Player(uuid)=" + sp.getName();
                } else {
                  p.kickPlayer(msgKickUuid);
                  msgLog += "Player(uuid)=" + p.getName();
                }
                getLogger().info(msgLog);
              }
            }

          }
        }
      }, 0L, intervalSeconds * 20L);
    } catch (Exception e) {
      logStackTrace(e);
    }
  }

  /**
   * Output stack trace to log file.
   * @param Exception Exception
   */
  public void logStackTrace(Exception e){
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      getLogger().warning(sw.toString());
  }

  /**
   * JavaPlugin method onDisable.
   */
  @Override
  public void onDisable() {
    try {
      getLogger().info("The Plugin Has Been Disabled!");
    } catch (Exception e) {
      logStackTrace(e);
    }
  }
}