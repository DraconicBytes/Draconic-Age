package com.DraconicAge;

import org.apache.commons.lang3.ArrayUtils;

import com.DraconicAge.Armors.ModelArmorItem;
import com.DraconicAge.Entity.EntityDragon;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public class CommonProxy {
	
	//------------------------------------------------------------
	public void preInit(FMLPreInitializationEvent event) 
	{
		// init items
		DAItems.init();
		
		// init mobs
		DAMobs.init();
		
		// init entities
		// TODO: insert DAEntities class
		
    	// calling client pre initialization
    	clientPreInit(event);
	}
	
	public void clientPreInit(FMLPreInitializationEvent event) 
	{
		
	}
	
	//------------------------------------------------------------
	public void init(FMLInitializationEvent event)
    {
		// init recipes
    	DARecipes.init(event);
    	
    	// init world generators
    	DAGenerators.init();
    	
    	// calling client initlialization
    	clientInit(event);
    }
	
	public void clientInit(FMLInitializationEvent event) 
	{
		
	}
	
	//------------------------------------------------------------
	public void postInit(FMLPostInitializationEvent event)
	{
		// calling client post init
		clientPostInit(event);
	}
	
	public void clientPostInit(FMLPostInitializationEvent event) 
	{
		
	}

	//------------------------------------------------------------
	public ModelBiped getArmorModel(int name, int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
