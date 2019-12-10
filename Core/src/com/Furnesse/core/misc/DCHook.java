package com.Furnesse.core.misc;

import java.util.HashMap;

import org.bukkit.Bukkit;

import com.Furnesse.core.Core;

public abstract class DCHook
{
  private static final HashMap<String, DCHook> hooks = new HashMap<>(); private String pluginName;
  
  static  {
    hooks.put("WorldGuard", new DCWorldGuardHook());
    hooks.put("GriefPrevention", new DCGriefPreventionHook());
  }
  private boolean enabled;
  public static void attemptHooks() {
    for (DCHook hook : hooks.values()) {
      hook.hook();
    }
  }

  
  public static boolean getHook(String pluginName) { return ((DCHook)hooks.get(pluginName)).isEnabled(); }


  
  public static DCHook getHookByName(String pluginName) { return hooks.get(pluginName); }


  
  public String getPluginName() { return this.pluginName; }
  
  public boolean isEnabled() { return this.enabled; }


  
  public DCHook(String pluginName) { this.pluginName = pluginName; }

  
  public void hook() {
    if (Bukkit.getPluginManager().isPluginEnabled(this.pluginName)) {
      Core.instance.getLogger().info("§ahooked into §e" + this.pluginName);
      this.enabled = true;
      runHookAction();
    } 
  }
  
  protected abstract void runHookAction();
}
