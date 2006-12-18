/*
 * Created on Aug 30, 2005
 */
package zz.eclipse.utils.launcher.options;

import java.util.Comparator;
import java.util.Map;

/**
 * Compares options map from launch configurations.
 * Plugins that use {@link OptionsTab} should declare an extension
 * of org.eclipse.debug.core.launchConfigurationComparators,
 * use the name of the map ({@link OptionsTab#getMapName()})
 * as "attribute" and can use this class as comparator.
 * @author gpothier
 */
public class OptionsMapComparator implements Comparator<Map<String, String>>
{
	public int compare(Map<String, String> aMap1, Map<String, String> aMap2)
	{
		if (aMap1.size() != aMap2.size()) return -1;
		
		for (String theKey : aMap1.keySet())
		{
			String theValue1 = aMap1.get(theKey);
			String theValue2 = aMap2.get(theKey);
			
			boolean theEmpty1 = theValue1 == null || theValue1.length() == 0;
			boolean theEmpty2 = theValue2 == null || theValue2.length() == 0;

			if (theEmpty1 && theEmpty2) continue;
			if (theEmpty1 || theEmpty2) return -1;
			if (! theValue1.equals(theValue2)) return -1;
		}
		
		return 0;
	}
}
