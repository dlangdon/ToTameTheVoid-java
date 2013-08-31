/**
 * 
 */
package galaxy.generation;

import java.util.Random;

/**
 * @author Daniel Langdon
 */
public class SimpleBlobCreator implements ForceOfNature
{
	private int height;
	private int width;
	private int numBlobs;
	private int blobRadius;
	
	public SimpleBlobCreator(int height, int width, int numblobs, int blobRadius)
	{
		this.height = height;
		this.width = width;
		this.numBlobs = numblobs;
		this.blobRadius = blobRadius;
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#apply(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean apply(NascentGalaxy nascentGalaxy)
	{
		Random rand = new Random();
		float[][] map = nascentGalaxy.heatmap = new float[width][height];
		
		for(int b=0; b<numBlobs; b++)
		{
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			System.out.println(x + "," + y);

			for(int i=-blobRadius; i<=blobRadius; i++)
				for(int j=-blobRadius; j<=blobRadius; j++)
					if(x+i >= 0 && x+i < width && y+j >= 0 && y+j < height)
						map[x+i][y+j] = Math.min(map[x+i][y+j] + (2.0f / (float)(i*i + j*j)), 1.0f);
		}
		
		return false;
	}

}
