package com.DraconicAge;

import org.apache.commons.lang3.ArrayUtils;

import com.DraconicAge.WorldGeneration.DragonNestGenerator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class DAGenerators
{
	
	public static DragonNestGenerator MyDragonNestGenerator;
	
	public static BiomeGenBase[] LandBiomes = new BiomeGenBase[0];
	
	//------------------------------------------------------------
	/*
	 * Initialization + Registration of world generators
	 */
	public static void init() {
		// Register Spawn Regions
		LandBiomes = ArrayUtils.addAll(LandBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.DENSE));
		LandBiomes = ArrayUtils.addAll(LandBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FOREST));
		LandBiomes = ArrayUtils.addAll(LandBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.HILLS));
		LandBiomes = ArrayUtils.addAll(LandBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.MOUNTAIN));
		LandBiomes = ArrayUtils.addAll(LandBiomes, BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS));
		
		// Initialize generators
		MyDragonNestGenerator = new DragonNestGenerator();
		
		// Register generators
		GameRegistry.registerWorldGenerator(MyDragonNestGenerator, 1024);
	}
}
