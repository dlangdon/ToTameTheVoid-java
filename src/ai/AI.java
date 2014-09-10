/**
 * 
 */
package ai;

import empire.Empire;

/**
 * Basic interface for AI
 * @author Daniel Langdon
 */
public abstract class AI
{
	private Empire managed;

    protected AI(Empire managed)
    {
        this.managed = managed;
    }

    abstract void run();
}
