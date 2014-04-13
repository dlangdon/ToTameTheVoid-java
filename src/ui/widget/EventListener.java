package ui.widget;

/**
 * Basic interface for event listening.
 * There is not a lot of background for this interface, as it is pretty general purpose.
 */
public interface EventListener<T>
{
	void onEvent(T value);
}
