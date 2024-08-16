package com.blaxout1213.WeaponPoison;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;
import com.blaxout1213.WeaponPoison.util.DamageUtil;
import com.blaxout1213.WeaponPoison.util.ItemUtil;
import com.blaxout1213.WeaponPoison.util.MessageUtil;
import com.blaxout1213.WeaponPoison.util.SavePlayerDataUtil;
import com.tcoded.folialib.FoliaLib;

public class WeaponPoison extends JavaPlugin
{
	public static WeaponPoison PLUGIN;
	static File messagesFile = new File("plugins/WeaponPoison/messages.yml");
	public static YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);
	public static final String POISONABLE_METADATA = "WeaponPoisonCauserMetaData";
	public static final String WEAPONPOISONED_METADATA = "WeaponPoisonMetaData";
	public static final String WEAPONPOISONNBT_SEVERITY = "WeaponpoisonSeverity";
	public static final String WEAPONPOISONNBT_DAMAGE_TIMER = "WeaponpoisonTimer";
	public static final Random RAND = new Random();
	
	//public static final String SEVERITY_TAG = "Severity";
	//public static final String VENOM_TAG = "Venom";
	
	public static NamespacedKey POISONED_ITEM_KEY;
	//public static final NamespacedKey VENOM_KEY = new NamespacedKey(WeaponPoison.PLUGIN, "venom");
	private FoliaLib foliaLib;
	
	public void onEnable()
	{
		PLUGIN = this;
		POISONED_ITEM_KEY = new NamespacedKey(WeaponPoison.PLUGIN, "severity");
		new EventListener();
		foliaLib = new FoliaLib(this);
		loadConfig();
		
		try
		{
			File file = new File("plugins/WeaponPoison/disconnectedplayers");
			getLogger().log(Level.INFO, "Made directories: " + file.mkdirs());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void onDisable()
	{
		for(Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(player.hasMetadata(WEAPONPOISONED_METADATA))
			{
				try
				{
					SavePlayerDataUtil.saveDisconnector(player);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if ((sender instanceof Player))
		{
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("weaponpoison"))
			{
				if(args[0].equalsIgnoreCase("status"))
				{
					try
					{
						PoisonedEntityTask data = DamageUtil.getWeaponPoisonTask(p);
						if(data.isVenom())
						{
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Venomed.Severity"), data));
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Venomed.Damage"), data));
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Venomed.Healing"), data));
						}
						else
						{
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Poisoned.Severity"), data));
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Poisoned.Damage"), data));
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Poisoned.DamageRemaining"), data));
							p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Poisoned.Healing"), data));
						}
					}
					catch(IllegalArgumentException e)
					{
						p.sendMessage(MessageUtil.prepareMessage(messages.getString("Commands.Info.Okay")));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("help"))
				{
					if(args.length == 1) { return false; } 
					if(args[1].equalsIgnoreCase("poison"))
					{
						String s = "Commands.Info.Poison.Help.";
						int message = 1;
						while(messages.get(s + message) != null)
						{
							p.sendMessage(MessageUtil.prepareMessage(messages.getString(s + message)));
							message ++;
						}
						return true;
					}
					if(args[1].equalsIgnoreCase("venom"))
					{
						String s = "Commands.Info.Venom.Help.";
						int message = 1;
						while(messages.get(s + message) != null)
						{
							p.sendMessage(MessageUtil.prepareMessage(messages.getString(s + message)));
							message ++;
						}
						return true;
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase("poisonthis"))
			{
				if(args.length != 1)
				{
					p.sendMessage("Correct usage: /poisonthis <severity>");
					return false;
				}
				try
				{
					int severity = Integer.valueOf(args[0]);
					if(p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR)
					{
						p.sendMessage("Cant poison nothing!");
						return true;
					}
					ItemStack is = ItemUtil.poisonThat(p.getInventory().getItemInMainHand(), severity);
					p.getInventory().remove(p.getInventory().getItemInMainHand());
					p.getInventory().setItemInMainHand(is);
					return true;
				}
				catch (NumberFormatException e)
				{
					p.sendMessage("Correct usage: /poisonthis <severity>");
					return false;
				}
			}
			if (cmd.getName().equalsIgnoreCase("venomthis"))
			{
				if(args.length != 1)
				{
					return false;
				}
				try
				{
					int severity = Integer.valueOf(args[0]);
					if(p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR)
					{
						p.sendMessage("Cant venom nothing!");
						return true;
					}
					ItemStack is = ItemUtil.venomThat(p.getInventory().getItemInMainHand(), severity, true);
					p.getInventory().remove(p.getInventory().getItemInMainHand());
					p.getInventory().setItemInMainHand(is);
					return true;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			}
			if (cmd.getName().equalsIgnoreCase("gaming"))
			{
				p.getInventory().addItem((ItemUtil.poisonThis(Material.DIAMOND_SWORD, 30, 1)));
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("rangedgamer"))
			{
				p.getInventory().addItem(ItemUtil.poisonThis(Material.ARROW, 30, 64));
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("enemygamer"))
			{

				Zombie z = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
				z.getEquipment().setItemInMainHand(ItemUtil.poisonThis(Material.DIAMOND_SWORD, 30, 1));
				z.getEquipment().setItemInMainHandDropChance(1.0F);
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("rangedenemygamer"))
			{

				Skeleton z = (Skeleton) p.getWorld().spawnEntity(p.getLocation(), EntityType.SKELETON);
				z.getEquipment().setItemInOffHand(ItemUtil.poisonThis(Material.ARROW, 30, 1));
				z.getEquipment().setItemInMainHandDropChance(1.0F);
				return true;
			}
		}
		return false;
	}
	
	public void loadConfig()
	{
		HashMap<String, Object> weapons = new HashMap<String, Object>();
		weapons.put(Material.WOODEN_SWORD.toString(), 2);
		weapons.put(Material.WOODEN_AXE.toString(), 3);
		weapons.put(Material.STONE_SWORD.toString(), 3);
		weapons.put(Material.STONE_AXE.toString(), 4);
		weapons.put(Material.IRON_SWORD.toString(), 4);
		weapons.put(Material.IRON_AXE.toString(), 5);
		weapons.put(Material.GOLDEN_SWORD.toString(), 1);
		weapons.put(Material.GOLDEN_AXE.toString(), 2);
		weapons.put(Material.TRIDENT.toString(), 12);
		
		getConfig().addDefault("Trades.Weapons", weapons);
		
		messages.addDefault("Broadcasts.Death.Poison", "%p succumbed to &apoison&f");
		messages.addDefault("Broadcasts.Death.Venom", "%p succumbed to &2venom&f");
		
		messages.addDefault("Accouncements.Messages.Poison.Inflicted", "A &apoison&f seeps into your blood");
		messages.addDefault("Accouncements.Messages.Poison.DamageDealt", "The &apoison&f works its deadly trade");
		messages.addDefault("Accouncements.Messages.Poison.DamageDealtFading", "The &apoison&f works its deadly trade, its effect beginning to fade");
		messages.addDefault("Accouncements.Messages.Poison.Expire", "The &apoison&f fades from your veins");
		messages.addDefault("Accouncements.Messages.Poison.Milked", "The &apoison&f softens significantly, but it still remains");
		messages.addDefault("Accouncements.Messages.Poison.Cured", "The &apoison&f is purged from your system");
		
		messages.addDefault("Accouncements.Messages.Venom.Inflicted", "A &2venom&f seeps into your blood");
		messages.addDefault("Accouncements.Messages.Venom.Upgrade", "A &2venom&f seeps into your blood, intensifying the &apoison&f already within");
		messages.addDefault("Accouncements.Messages.Venom.DamageDealt", "The &2venom&f works its deadly trade, and grows even deadlier");
		messages.addDefault("Accouncements.Messages.Venom.Milked", "The &2venom&f weakens significantly, but a &apoison&f still remains");
		messages.addDefault("Accouncements.Messages.Venom.Cured", "The &2venom&f is purged from your system");
		
		
		messages.addDefault("Commands.Info.Okay", "Currently, you are not afflicted with any poisons or venoms");
		
		messages.addDefault("Commands.Info.Poisoned.Severity", "Currently, you are &apoisoned&f with &a%s&f severity. This will wear off over time");
		messages.addDefault("Commands.Info.Poisoned.Healing", "This will reduce your healing to &4%h%");
		messages.addDefault("Commands.Info.Poisoned.Damage", "This will do &a%d&f damage per tick");
		messages.addDefault("Commands.Info.Poisoned.DamageRemaining", "Without a cure, this will do a total of &a%r&f damage over %s instances");
		
		messages.addDefault("Commands.Info.Venomed.Severity", "Currently, you are &2venomed&f with &2%s&f severity. This will &cworsen&f over time.");
		messages.addDefault("Commands.Info.Venomed.Healing", "This will reduce your healing to &4%h%");
		messages.addDefault("Commands.Info.Venomed.Damage", "This will do &2%d&f damage per tick");
		
		messages.addDefault("Commands.Info.Poison.Help.1", "Poison will deal damage over time, every 18 seconds equal to (severity+4) / 5. Every time it does damage, its severity will lessen, until completely wearing off at 0.");
		messages.addDefault("Commands.Info.Poison.Help.2", "It will also reduce your incoming healing as a function of severity. More severity, means more healing reduction, starting at 5 severity. Healing is reduced by 50% at 25 severity.");
		messages.addDefault("Commands.Info.Poison.Help.3", "It can be counteracted with milk, which will drastically reduce the severity of your poison, and in most cases, cure it. Extraordinarily strong poisons may not be cured with just one, however.");
		
		messages.addDefault("Commands.Info.Venom.Help.1", "Venom, like poison, will also deal damage over time. What makes it different from poison, is that it will get worse over time, instead of wearing off.");
		messages.addDefault("Commands.Info.Venom.Help.2", "It gets worse remarkably fast, and without a quick milk to down, it will eventually kill anything. Once a milk is drunk, it will convert to poison if this isn't enough to cure it.");
		
		/*
		if(!messagesFile.exists())
		{
			saveResource("messages.yml", false);
		}
		*/
		getConfig().options().copyDefaults(true);
		saveConfig();
		messages.options().copyDefaults(true);
		try
		{
			messages.save(messagesFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public FoliaLib getFoliaLib()
	{
		return foliaLib;
	}
}
