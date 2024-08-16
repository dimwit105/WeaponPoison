package com.blaxout1213.WeaponPoison.tasks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.blaxout1213.WeaponPoison.WeaponPoison;
import com.blaxout1213.WeaponPoison.util.DamageUtil;
import com.blaxout1213.WeaponPoison.util.MessageUtil;

public class PoisonedEntityTask extends CancellableRunnable implements Serializable 
{
	
	private static final long serialVersionUID = 3L;
	private transient LivingEntity livingEntity;
	private transient boolean deathTick = false;
	private transient double damage = recalculateDamage();
	private int severity;
	private int severityDrain;
	private int damageTimer;
	
	public PoisonedEntityTask(LivingEntity livingEntity, int severity)
	{
		
		this.livingEntity = livingEntity;
		this.severity = severity;
		this.damageTimer = 18;
		this.severityDrain = 1;
		this.damage = recalculateDamage();
	}
	
	public PoisonedEntityTask(LivingEntity livingEntity, int severity, boolean venom)
	{
		this(livingEntity, severity);
		if(venom)
		{
			this.severityDrain = -10;
		}
	}
	//designed to be run once per second.
	public void run()
	{
		if(livingEntity.isValid() && livingEntity.getType() != EntityType.CAVE_SPIDER)
		{
			damageTimer--;
			//spawnEffectParticles((int) damage);
			//Bukkit.broadcastMessage("DamageTimer; " + damageTimer + " Severity:" + severity);
			if(damageTimer <= 0 && livingEntity.getNoDamageTicks() <= 0)
			{
				deathTick = livingEntity.getHealth() - damage <= 0 ? true : false;
				livingEntity.damage(DamageUtil.getPureDamage(livingEntity, damage));
				severity -= severityDrain;
				damage = recalculateDamage();
				sendDamageMessage();
				damageTimer = 18;
			}
			if(severity <= 0)
			{
				clearPoison(ClearReason.EXPIRE);
			}
		}
		else
		{
			clearPoison(ClearReason.INVALID);
		}
	}
	public void setSeverity(int severity, boolean venom)
	{
		if(venom) { this.severityDrain = -10; }
		if(severity > this.severity)
		{
			this.severity = severity;
		}
		damage = recalculateDamage();
	}
	public boolean isDeathTick()
	{
		return deathTick;
	}
	public boolean isVenom()
	{
		if(severityDrain >= 1)
		{
			return false;
		}
		return true;
	}
	public LivingEntity getEntity()
	{
		return livingEntity;
	}
	public int getSeverity()
	{
		return severity;
	}
	
	public double getRemainingDamage()
	{
		if(severityDrain <= 0)
		{
			return Double.MAX_VALUE;
		}
		double sum = 0.0D;
		for(int i = severity; i > 0; i -= severityDrain)
		{
			 sum += Math.floor((i+4.0D)/5.0D);
		}
		return sum;
	}
	public int getDamageTimer()
	{
		return damageTimer;
	}
	public double getHealingMultiplier()
	{
		return Math.min(1.0D, 1.0D / ((severity+15.0D)/20.0D));
	}
	public void applyAntidote()
	{
		boolean isVenom = false;
		if(severityDrain < 1 ) { severityDrain = 1; isVenom = true;}
		severity /= 2;
		severity -= 20;
		if(severity <= 0)
		{
			clearPoison(ClearReason.CURED);
		}
		else
		{
			if(livingEntity instanceof Player)
			{
				Player p = (Player)livingEntity;
				String s = "Accouncements.Messages.";
				if(isVenom) { s = s + "Venom.Milked"; } else { s = s + "Poison.Milked"; }
				p.sendMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString(s), this));
			}
		}
		damage = recalculateDamage();
	}
	public void clearPoison(ClearReason reason)
	{
		if(livingEntity instanceof Player)
		{
			Player p = (Player)livingEntity;
			String s = null;
			switch(reason)
			{
				case CURED:
					s = "Accouncements.Messages.Poison.Cured";
					break;
				case EXPIRE:
					s = "Accouncements.Messages.Poison.Expire";
					break;
			}
			if(s != null)
			{
				p.sendMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString(s), this));
			}
			
		}
		livingEntity.removeMetadata(WeaponPoison.WEAPONPOISONED_METADATA, WeaponPoison.PLUGIN);
		this.cancel();
	}
	private void sendDamageMessage()
	{
		if(!livingEntity.isDead() && livingEntity instanceof Player && severity > 0)
		{
			Player p = (Player)livingEntity;
			StringBuilder s = new StringBuilder("Accouncements.Messages.");
			if(severityDrain >= 1) { s.append("Poison.DamageDealt");} else {s.append("Venom.DamageDealt");}
			if(damage < recalculateDamage(severityDrain) && severityDrain >= 1) {s.append("Fading");}
			p.sendMessage(MessageUtil.prepareMessage(WeaponPoison.messages.getString(s.toString()), this));
		}
	}
	public double recalculateDamage()
	{
		return Math.floor((severity+4.0D)/5.0D);
	}

	private double recalculateDamage(double modifier)
	{
		return Math.floor((severity+modifier+4.0D)/5.0D);
	}
	
	private void spawnEffectParticles(int count)
	{
		double green = severityDrain < 1 ? 100.0D/255.0D : 200.0D/255D;
		for(int i=0; i < count; i++)
		{
			double x = WeaponPoison.RAND.nextDouble() - WeaponPoison.RAND.nextDouble();
			double z = WeaponPoison.RAND.nextDouble() - WeaponPoison.RAND.nextDouble();
			double y = livingEntity.getEyeHeight() + WeaponPoison.RAND.nextDouble(livingEntity.getHeight()*0.1D) - WeaponPoison.RAND.nextDouble(livingEntity.getHeight()*0.1D);
			livingEntity.getWorld().spawnParticle(Particle.SPELL_MOB, livingEntity.getLocation().add(x, y, z), 0, 0.0D, green, 0.0D, 1);
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(livingEntity.getUniqueId());
		out.writeInt(severity);
		out.writeInt(severityDrain);
		out.writeInt(damageTimer);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		UUID id = (UUID) in.readObject();
		WeaponPoison.PLUGIN.getLogger().log(Level.INFO, "Loading weapon poison info UUID: " + id);
		livingEntity = (LivingEntity) Bukkit.getServer().getEntity(id);
		severity = in.readInt();
		severityDrain = in.readInt();
		damageTimer = in.readInt();
		damage = recalculateDamage();
	}
	
	public enum ClearReason
	{
		EXPIRE,
		CURED,
		DEATH,
		DISCONNECT,
		INVALID;
	}
}
