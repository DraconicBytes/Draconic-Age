package com.DraconicAge;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

// Holds the ArmorMaterials
// To the make code more transparent
public class ArmorMaterialHelper {
	// Define materials
    // 1. Armors
	public static ArmorMaterial SCALE = EnumHelper.addArmorMaterial("scale", 30, new int[]{3, 8, 6, 3}, 25);
	
	public static int getArmorID(String pName)
	{
		if (pName.equals("scale")) 
			return 1;
		else 
			return 0;
	}
	
}
