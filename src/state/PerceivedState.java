/**
 * 
 */
package state;

import java.util.*;
import java.util.Map.Entry;

import ai.PerceivedState.Classification;


/**
 * A view of the current state of the galaxy, as seen by a single empire.
 * This is a highly skewed view, as each empire will only have partial knowledge about what is going on, both due to fog of war and invisible units.
 * It is more efficient to update this view every time a turn begins, since we can distribute objects from the true state to each empire's view without having to go through it multiple times.
 *
 * The scope of this class does not include understanding of tactical status (defended or undefended territory, frontier or core system, etc). The idea is to leave those concepts for the player or AI.
 * The scope is thus limited to visibility and accessibility (this given by empire relationships)
 * 
 * @author Daniel Langdon
 */
public class PerceivedState
{

}
