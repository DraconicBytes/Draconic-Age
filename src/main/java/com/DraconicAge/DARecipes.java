package com.DraconicAge;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class DARecipes {
	public static void init(FMLInitializationEvent event) {
		
    	
    	// register crafting recipes
    	// armors - scale tier
    	GameRegistry.addShapedRecipe(new ItemStack(DAItems.scaleHelmet, 1), new Object[] {
    			"XXX",
    			"X X",
    			"   ",
    			'X', DAItems.ironScale
    	});
    	
    	GameRegistry.addShapedRecipe(new ItemStack(DAItems.scaleChest, 1), new Object[] {
    			"X X",
    			"XXX",
    			"XXX",
    			'X', DAItems.ironScale
    	});
    	
    	GameRegistry.addShapedRecipe(new ItemStack(DAItems.scaleLegs, 1), new Object[] {
    			"XXX",
    			"X X",
    			"X X",
    			'X', DAItems.ironScale
    	});
    	
    	GameRegistry.addShapedRecipe(new ItemStack(DAItems.scaleBoots, 1), new Object[] {
    			"X X",
    			"X X",
    			"   ",
    			'X', DAItems.ironScale
    	});
    	
    	// register intermediate crafting recipes
    	// dragon scale
    	GameRegistry.addShapelessRecipe(new ItemStack(DAItems.ironScale, 1), new Object[] {
		    Items.iron_ingot , Items.iron_ingot
	    });
    		
    	
    	// register smeltings
    	
	}
}
