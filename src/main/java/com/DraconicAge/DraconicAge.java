package com.DraconicAge;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = DraconicAge.MODID, version = DraconicAge.VERSION)
public class DraconicAge
{
	// ------------------------------------------------------------------------------------
	// TODO: 
	// - Make the correct logic of the Elder Dragon
	// - Make the right pictures for the armor
	// - Adding a weapon
	// - Adding a 2nd armor
	// - Adding an ore
	// - Adding a custom crafting station
	// ------------------------------------------------------------------------------------
	// Declaring Variables
    public static final String MODID = "DraconicAge";
    public static final String VERSION = "1.0.0 r10";
    
    // init proxies
    @SidedProxy(serverSide="com.DraconicAge.CommonProxy", clientSide="com.DraconicAge.ClientProxy")
	public static CommonProxy proxy;
    
    
    // ------------------------------------------------------------------------------------
    // Mod Initialization / Calling Proxy
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        this.proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        this.proxy.postInit(e);
    }
    
    // ------------------------------------------------------------------------------------
    // Load Method
    @EventHandler public void load(FMLInitializationEvent e) 
    {
    	
    } 
}
