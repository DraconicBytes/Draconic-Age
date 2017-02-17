package com.DraconicAge.lib;

import net.minecraft.util.MathHelper;

public class FlyingHelper {
	
	/**
	 * Limits the angle to the given deltaLimit.
	 * @return Limited angle
	 */
	public static float smoothAngle( float angle, float targetAngle, float deltaLimit )
	{
		float angleDelta = MathHelper.wrapAngleTo180_float( targetAngle - angle );

		if ( angleDelta > deltaLimit )
			angleDelta = deltaLimit;

		if ( angleDelta < -deltaLimit )
			angleDelta = -deltaLimit;

		return angle + angleDelta;

	}
}
