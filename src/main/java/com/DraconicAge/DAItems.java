package com.DraconicAge;

import com.DraconicAge.Armors.ModelArmorItem;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

//------------------------------------------------------------
// This static class holds all items added by this mod to make them easier to access
// and to bring a clearer into this mod
public class DAItems {
	//------------------------------------------------------------
	// declare armor items
	public static Item scaleChest; 
    public static Item scaleLegs; 
    public static Item scaleBoots; 
    public static Item scaleHelmet;
    
    //------------------------------------------------------------
    // declare intermediate items
    public static Item ironScale;
    public static Item dragonScale;
    
    //------------------------------------------------------------
    // Declare armor item names
    public static String scaleTierName = "scale";
      
    // Declare intermediate itemNames
    public static String ironScaleName = "scale_iron";
    public static String dragonScaleName = "scale_dragon";
    
    //------------------------------------------------------------
    public static void init() {
    	// init armor items
		// Scale Tier
    	DAItems.scaleHelmet = new ModelArmorItem(scaleTierName, ArmorMaterialHelper.SCALE, 4, 0).setUnlocalizedName(scaleTierName+"_helmet").setCreativeTab(CreativeTabs.tabCombat); 
    	DAItems.scaleChest = new ModelArmorItem(scaleTierName, ArmorMaterialHelper.SCALE, 4, 1).setUnlocalizedName(scaleTierName+"_chest").setCreativeTab(CreativeTabs.tabCombat); 
    	DAItems.scaleLegs = new ModelArmorItem(scaleTierName, ArmorMaterialHelper.SCALE, 4, 2).setUnlocalizedName(scaleTierName+"_leggins").setCreativeTab(CreativeTabs.tabCombat); 
    	DAItems.scaleBoots = new ModelArmorItem(scaleTierName, ArmorMaterialHelper.SCALE, 4, 3).setUnlocalizedName(scaleTierName+"_boots").setCreativeTab(CreativeTabs.tabCombat);
    	
    	// init intermediate items
    	DAItems.ironScale = new Item().setUnlocalizedName(DraconicAge.MODID + ":" + ironScaleName).setCreativeTab(CreativeTabs.tabMaterials).setTextureName(DraconicAge.MODID + ":intermediates/" + ironScaleName);
    	DAItems.dragonScale = new Item().setUnlocalizedName(DraconicAge.MODID + ":" + dragonScaleName).setCreativeTab(CreativeTabs.tabMaterials).setTextureName(DraconicAge.MODID + ":intermediates/" + dragonScaleName);
		
    	
    	// register them in the GameRegistry
    	// Scale Tier
    	GameRegistry.registerItem(scaleHelmet, scaleTierName + "_helmet");
    	GameRegistry.registerItem(scaleChest, scaleTierName + "_chest");
    	GameRegistry.registerItem(scaleLegs, scaleTierName + "_legs");
    	GameRegistry.registerItem(scaleBoots, scaleTierName + "_boots");
    	
    	// register intermiediates
    	GameRegistry.registerItem(ironScale, ironScaleName);
    	GameRegistry.registerItem(dragonScale, dragonScaleName);
    }
}
