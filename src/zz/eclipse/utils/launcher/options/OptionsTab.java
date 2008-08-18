/*
 * Created on Jun 21, 2005
 */
package zz.eclipse.utils.launcher.options;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import zz.eclipse.utils.EclipseUtils;
import zz.eclipse.utils.launcher.ZLaunchTab;


/**
 * This tab permits to configure miscellanous options.
 * @author gpothier
 */
public abstract class OptionsTab<K> extends ZLaunchTab
{
	private OptionsControl<K> itsOptionsControl;
	
	protected abstract OptionsControl<K> createOptionsControl(Composite aParent);
	
	public void createControl(Composite aParent)
	{
		ScrolledComposite theScroll = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		itsOptionsControl = createOptionsControl(theScroll);
		theScroll.setContent(itsOptionsControl);
		setControl(theScroll);
	}
	
	/**
	 * Returns the name that is used to load and store the options map
	 * from the configuration.
	 */
	protected abstract String getMapName();
	
	public void setDefaults(ILaunchConfigurationWorkingCopy aConfiguration)
	{
		if (itsOptionsControl != null)
		{
			saveOptionsMap(itsOptionsControl.getDefaults(), aConfiguration);
		}
	}

	public void initializeFrom(ILaunchConfiguration aConfiguration)
	{
		try
		{
			itsOptionsControl.setValues(loadOptionsMap(aConfiguration));
		}
		catch (CoreException e)
		{
			EclipseUtils.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy aConfiguration)
	{
		saveOptionsMap(itsOptionsControl.getValues(), aConfiguration);
	}

	/**
	 * Retrieves the options map from the given configuration. 
	 */
	private Map<String, String> loadOptionsMap(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return loadOptionsMap(aConfiguration, getMapName());		
	}
	
	public static Map<String, String> loadOptionsMap(
			ILaunchConfiguration aConfiguration,
			String aMapName) throws CoreException
	{
		Map<String, String> theMap = aConfiguration.getAttribute(
				aMapName, 
				(Map) null);
		
		if (theMap == null) theMap = new HashMap<String, String>();
		return theMap;
	}

	/**
	 * Saves the options map in the launch configuration
	 * @param aMap A map whose keys are argument tags, and values parameter values. 
	 */
	private void saveOptionsMap(
			Map<String, String> aMap,
			ILaunchConfigurationWorkingCopy aConfiguration)
	{
		aConfiguration.setAttribute(getMapName(), aMap);
	}
}
