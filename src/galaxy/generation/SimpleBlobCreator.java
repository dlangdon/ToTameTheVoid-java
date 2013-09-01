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
	private int minBlobRadius;
	private int maxBlobRadius;
	
	public SimpleBlobCreator(int height, int width, int numblobs, int minBlobRadius, int maxBlobRadius)
	{
		this.height = height;
		this.width = width;
		this.numBlobs = numblobs;
		this.minBlobRadius = minBlobRadius;
		this.maxBlobRadius = (maxBlobRadius > minBlobRadius) ? maxBlobRadius : minBlobRadius+5;
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#apply(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascentGalaxy)
	{
		Random rand = new Random();
		float[][] map = nascentGalaxy.heatmap = new float[width][height];
		
		for(int b=0; b<numBlobs; b++)
		{
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);

			for(int i=-maxBlobRadius; i<=maxBlobRadius; i++)
				for(int j=-maxBlobRadius; j<=maxBlobRadius; j++)
					if(x+i >= 0 && x+i < width && y+j >= 0 && y+j < height)
					{
//						map[x+i][y+j] = Math.min(map[x+i][y+j] + (1.0f / (float)(i*i + j*j)), 1.0f);
						int dX = Math.max(Math.abs(i)-minBlobRadius, 0);
						int dY = Math.max(Math.abs(j)-minBlobRadius, 0);
						float intensity = 1.0f / (float)(dX*dX + dY*dY);
						map[x+i][y+j] = Math.min(map[x+i][y+j] + intensity, 1.0f);
					}
		}
		
		return true;
	}

}
