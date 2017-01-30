package com.DraconicAge.WorldGeneration;

import java.util.Random;

import com.DraconicAge.DAGenerators;
import com.DraconicAge.Entity.EntityDragon;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockWood;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import scala.actors.threadpool.Arrays;

/*
 * Generates the dragon nests in the overworld.
 */
public class DragonNestGenerator implements IWorldGenerator
{
	/** Possibility that a nest spawns in a chunk */
	private float 	SpawnPossibility = 0.005f;
	/** Minimum distance to the next nest */
	private float 	MinDistance = 120.0f;
	/** Coordinates of the last nest */
	private Vec3 	LastNestCoords = null;
	/** Nest radius */
	private float 	NestRadius = 7.0f;
	/** Distance in wich objects are removed on spawn */
	private float 	ClearDistance = 3.0f;
	
	
	//----------------------------------------------------------------------------
	@Override
	public void generate( Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider )
	{
		/*
		 * TODO Dragon nest spawning
		 * 
		 * 1. Search for random place in chunk
		 * 2. Flatten area / prepare it for spawn
		 * 3. Spawn nest
		 * 4. Add Containers / Loot
		 * 5. Add mobs
		 */
		
		/*
		 * TODO: Spawn conditions for dragon nests
		 * 1. They do only spawn at a certain rarity
		 * 2. They must have a minimum distance x blocks to each other
		 * 3. They can only spawn in certain biomes
		 */
		
		// Apply Spawn rarity
		if (random.nextFloat( ) < SpawnPossibility) {
			if (Arrays.asList(DAGenerators.LandBiomes).contains(world.getBiomeGenForCoords( chunkX * 16, chunkZ *16 ))) {
				// 1. Search for highest block
				Vec3 HighestBlock = GetRandomBlock(random, chunkX, chunkZ, world);
				
				// Check if this nest is far enough away from the last generated
				if (LastNestCoords != null) {
					if ((HighestBlock.subtract(LastNestCoords)).lengthVector() < MinDistance)
						return;
				}
				
				System.out.println( "Spawning dragon nest at " + HighestBlock.xCoord + ", " + HighestBlock.yCoord + ", " + HighestBlock.zCoord );
				
				// 2. Prepare area around highest point for spawn
				PrepareNestArea(random, HighestBlock, world);
				
				// 3. Spawn Nest
				SpawnNest(random, HighestBlock, world);
				
				// 5. Spawn Dragon at nest
				EntityDragon dragon = new EntityDragon(world);
				dragon.setPosition( HighestBlock.xCoord, HighestBlock.yCoord + 20, HighestBlock.zCoord );
				world.spawnEntityInWorld( dragon );
				
				// Remember the coords
				LastNestCoords = HighestBlock;
			}
		}
	}
	
	//----------------------------------------------------------------------------
	/**
	 * Search for highest block in chunk at the given chunk koords.
	 * 
	 * @return: Vector of the highest block. Zeroes at min X / min Y / min Z.
	 */
	private Vec3 GetRandomBlock(Random random, int chunkX, int chunkZ, World world) {
		int 	columnheight;
		Vec3 	HighestBlock = Vec3.createVectorHelper( 0, 0, 0 );
		
		// Search for highest block column
		// Compares every Block column in chunk (256 columns in total)
		HighestBlock.xCoord = chunkX + random.nextInt( 16 );
		HighestBlock.zCoord = chunkZ + random.nextInt( 16 );
		HighestBlock.yCoord = world.getTopSolidOrLiquidBlock((int) HighestBlock.xCoord, (int) HighestBlock.zCoord );
		
		// If a tree is the highest point set nest center to the ground
		int 	currentY = (int)HighestBlock.yCoord;
		Block 	treeBlock = world.getBlock( (int)HighestBlock.xCoord, currentY, (int)HighestBlock.zCoord );
		while (treeBlock.isWood( world, (int)HighestBlock.xCoord, (int)HighestBlock.yCoord, (int)HighestBlock.zCoord ) 
				|| treeBlock.isLeaves( world, (int)HighestBlock.xCoord, (int)HighestBlock.yCoord, (int)HighestBlock.zCoord )) 
		{
			currentY--;
			treeBlock = world.getBlock( (int)HighestBlock.xCoord, currentY, (int)HighestBlock.zCoord );
		}
		HighestBlock.yCoord = currentY;
		
		return HighestBlock;
	}
	
	//----------------------------------------------------------------------------
	/**
	 * Prepares a place for the dragon nest to be spawned.
	 * 
	 * Clears out an area of 10 x 10 x 10 in this case.
	 */
	private void PrepareNestArea(Random random, Vec3 highestBlock, World world) {
		// Deforest area
		
		// Starting values for clear
		int 	startX = (int) (highestBlock.xCoord - 20), 
				startZ = (int) (highestBlock.zCoord - 20);
		/* Max values for clear */
		int 	maxX = (int) (highestBlock.xCoord + 20), 
				maxZ = (int) (highestBlock.zCoord + 20);
		
		for (int x = startX; x < maxX; x++) {
			for (int z = startZ; z < maxZ; z++) {
				if (Math.pow((x-highestBlock.xCoord),2) + Math.pow((z-highestBlock.zCoord),2) <= 400) {
					int currentY = world.getTopSolidOrLiquidBlock( x, z );
					
					// If a tree is the highest point set nest center to the ground
					Block 	treeBlock = world.getBlock( x, currentY, z );
					while (treeBlock.isWood( world, x, currentY, z ) 
							|| treeBlock.isLeaves( world, x, currentY, z )) 
					{
						world.setBlockToAir( x, currentY, z );
						currentY--;
						treeBlock = world.getBlock( x, currentY, z );
					}
					if  (!treeBlock.isAir( world, x, currentY, z ) &&
							treeBlock.isWood( world, x, currentY+1, z )){
						// Degenerate tree
						
					}
				}
			}
		}
	}
	
	//----------------------------------------------------------------------------
	/**
	 * Spawns nest at desired location
	 * 
	 * @param world Current world
	 * @param highestBlock Desired Position
	 * @param random Random object from Minecraft
	 */
	private void SpawnNest(Random random, Vec3 location, World world) {
		
		// Starting values for clear
		int 	startX = (int) (location.xCoord - NestRadius), 
				startY = (int) location.yCoord - 10, 
				startZ = (int) (location.zCoord - NestRadius);
		/* Max values for clear */
		int 	maxX = (int) (location.xCoord + NestRadius + 1) , 
				maxY = (int) location.yCoord, 
				maxZ = startZ + (int) (location.zCoord + NestRadius + 1);
		/** Local Coordinates */
		int 	localX = (int) - NestRadius, localZ;
		
		int 	currentConeStep = 0;
		float  	nestRadiusSquared = NestRadius * NestRadius, 
				clearDistanceSquared = (NestRadius + ClearDistance) * (NestRadius + ClearDistance);
		
		// Making foundation
		for (int x = startX; x < maxX; x++) {
			localZ = (int) - NestRadius;
			for (int z = startZ; z < maxZ; z++) {
				for (int y = startY; y < maxY -3; y++) {
					// Create base for the the nest
					// Make a cobblestone cylinder under the nest
					if ((localX*localX + localZ*localZ) <= nestRadiusSquared) {
						world.setBlock( x, y, z, Block.getBlockById( 4 ) );
						
						if (y == maxY - 4) {
							if ((localX*localX + localZ*localZ) <= Math.pow((NestRadius/2),2)) {
								world.setBlock( x, y	, z, Block.getBlockById( 49 ) );
								if (random.nextFloat() <= 0.7f)
									world.setBlock( x, y+1	, z, Block.getBlockById( 49 ) );
								else
									world.setBlock( x, y+1	, z, Block.getBlockById( 173) );
								world.setBlockToAir(x, y+2, z);
								world.setBlockToAir(x, y+3, z);
								world.setBlockToAir(x, y+4, z);
							}
							else if ((localX*localX + localZ*localZ) <= Math.pow((NestRadius/2+1),2)) {
								world.setBlock( x, y	, z, Block.getBlockById( 49 ) );
								world.setBlock( x, y+1	, z, Block.getBlockById( 49 ) );
								if (random.nextFloat() <= 0.5f)
									world.setBlock( x, y+2	, z, Block.getBlockById( 49 ) );
								else
									world.setBlockToAir(x, y+2, z);
								world.setBlockToAir(x, y+3, z);
								world.setBlockToAir(x, y+4, z);
							}
							else if ((localX*localX + localZ*localZ) <= Math.pow((NestRadius-2),2)) {
								world.setBlock( x, y	, z, Block.getBlockById( 49 ) );
								world.setBlock( x, y+1	, z, Block.getBlockById( 49 ) );
								if (random.nextFloat() <= 0.7f)
									world.setBlock( x, y+2	, z, Block.getBlockById( 49 ) );
								else
									world.setBlock( x, y+2	, z, Block.getBlockById( 173) );
								world.setBlockToAir(x, y+3, z);
								world.setBlockToAir(x, y+4, z);
							}
							else if ((localX*localX + localZ*localZ) <= Math.pow((NestRadius-1),2)) {
								world.setBlock( x, y	, z, Block.getBlockById( 49 ) );
								world.setBlock( x, y+1	, z, Block.getBlockById( 49 ) );
								if (random.nextFloat() <= 0.9f)
									world.setBlock( x, y+2	, z, Block.getBlockById( 49 ) );
								else
									world.setBlock( x, y+2	, z, Block.getBlockById( 173) );
								if (random.nextFloat() <= 0.3f)
									world.setBlock( x, y+3	, z, Block.getBlockById( 49 ) );
								else 
									world.setBlockToAir(x, y+3, z);
								world.setBlockToAir(x, y+4, z);
							}
							else {
								world.setBlock( x, y+1	, z, Block.getBlockById( 3 ) );
								world.setBlock( x, y+1	, z, Block.getBlockById( 49 ) );
								world.setBlock( x, y+2	, z, Block.getBlockById( 49 ) );
								if (random.nextFloat() <= 0.7f)
									world.setBlock( x, y+3	, z, Block.getBlockById( 49 ) );
								else
									world.setBlock( x, y+3	, z, Block.getBlockById( 173 ) );
								if (random.nextFloat() <= 0.3f)
									world.setBlock( x, y+4	, z, Block.getBlockById( 49 ) );
								else
									world.setBlockToAir(x, y+4, z);
							}
						}
					}
				}
				localZ++;
			}
			localX++;
		}
		
		// Adjust variables to new cylinder boundaries
		startX = (int) (location.xCoord - (NestRadius + ClearDistance));
		startZ = (int) (location.zCoord - (NestRadius + ClearDistance));
		startY = (int) location.yCoord+1;
		
		maxY = startY + 10;
		maxX = (int) (location.xCoord + (NestRadius + ClearDistance + 1));
		maxZ = (int) (location.zCoord + (NestRadius + ClearDistance + 1));
		
		localX = (int) - (NestRadius + ClearDistance);
		
		// Clearing area around and over the nest
		for (int x = startX; x < maxX; x++) {
			localZ = (int) - (NestRadius + ClearDistance);
			for (int z = startZ; z < maxZ; z++) {
				for (int y = startY; y < maxY; y++) {
					
					// Make enough space
					if ((localX*localX + localZ*localZ) <= clearDistanceSquared) {// 3 blocks spacing
						world.setBlockToAir(x, y, z);
					}
				}
				localZ++;
			}
			localX++;
		}
	}
	
}
