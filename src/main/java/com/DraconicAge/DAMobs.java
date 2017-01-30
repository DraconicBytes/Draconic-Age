package com.DraconicAge;

import org.apache.commons.lang3.ArrayUtils;

import com.DraconicAge.Entity.EntityDragon;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class DAMobs {

	// Declare mob spawn regions
	static BiomeGenBase[] OverworldOnLandSpawn = new BiomeGenBase[0];
	
	public static void init() {
		// TODO Auto-generated method stub
		//setup mob spawn regions
    	OverworldOnLandSpawn = ArrayUtils.addAll(OverworldOnLandSpawn, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.DENSE));
    	OverworldOnLandSpawn = ArrayUtils.addAll(OverworldOnLandSpawn, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FOREST));
    	OverworldOnLandSpawn = ArrayUtils.addAll(OverworldOnLandSpawn, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.RIVER));
    	
    	// register entities
    	EntityRegistry.registerGlobalEntityID(EntityDragon.class, "Dragon", EntityRegistry.findGlobalUniqueEntityId(), 0x151210, 0x4709E2);
    	
    	// register entity spawns
    	EntityRegistry.addSpawn(EntityDragon.class, 1, 1, 2, EnumCreatureType.monster, OverworldOnLandSpawn);
	}

}
