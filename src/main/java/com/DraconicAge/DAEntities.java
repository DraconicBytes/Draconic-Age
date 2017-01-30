package com.DraconicAge;

import com.DraconicAge.Entity.EntityDragon;
import com.DraconicAge.Entity.Projectiles.EntityGuidedFireball;

import cpw.mods.fml.common.registry.EntityRegistry;

public class DAEntities {
	
	public static void init() {
		// register entities
    	EntityRegistry.registerGlobalEntityID(EntityGuidedFireball.class, "Guided Fireball", EntityRegistry.findGlobalUniqueEntityId());
    	
	}

}
