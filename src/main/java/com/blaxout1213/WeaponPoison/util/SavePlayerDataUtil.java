package com.blaxout1213.WeaponPoison.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.metadata.PoisonedEntityTaskMeta;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask;
import com.blaxout1213.WeaponPoison.tasks.PoisonedEntityTask.ClearReason;

public class SavePlayerDataUtil
{
	public static void saveDisconnector(Player p) throws IOException
	{
		WeaponPoison.PLUGIN.getLogger().log(Level.INFO, "Attempting to save " + p.getName() + " data");
		String filename = "plugins/WeaponPoison/disconnectedplayers/" + p.getUniqueId().toString() + ".ser";
		File file = new File(filename);
		file.createNewFile();
		PoisonedEntityTaskMeta poisonTaskMeta = (PoisonedEntityTaskMeta) p.getMetadata(WeaponPoison.WEAPONPOISONED_METADATA).get(0);

		FileOutputStream  fos = new FileOutputStream(file, false);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		
		out.writeObject(poisonTaskMeta.getPoisonedEntityTask());
		poisonTaskMeta.getPoisonedEntityTask().clearPoison(ClearReason.DISCONNECT);
		out.close();
		fos.close();
	}
	
	public static PoisonedEntityTask readDisconnector(Player p, int compensation) throws FileNotFoundException, IOException, ClassNotFoundException, EntityNotFoundException
	{
		String filename = "plugins/WeaponPoison/disconnectedplayers/" + p.getUniqueId().toString() + ".ser";
		File file = new File(filename);
		if(!file.exists() ) 
		{
			throw new FileNotFoundException();
		}
		FileInputStream fos = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fos);
		
		PoisonedEntityTask o = (PoisonedEntityTask) in.readObject();
		fos.close();
		in.close();
		Files.delete(Paths.get(filename));
		if(o.getEntity() == null) {throw new EntityNotFoundException("Bukkit could not find entity by ID!");}
		return o;
	}
}
