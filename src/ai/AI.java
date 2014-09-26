/**
 *
 */
package ai;

import empire.Empire;

/**
 * Basic interface for AI
 *
 * @author Daniel Langdon
 */
public abstract class AI
{
   protected Empire managed;

   protected AI(Empire managed)
   {
      this.managed = managed;
   }

   abstract void run();
}
