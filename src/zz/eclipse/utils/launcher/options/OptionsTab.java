/*
 * Created on Jun 21, 2005
 */
package zz.eclipse.utils.launcher.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import zz.eclipse.utils.EclipseUtils;
import zz.eclipse.utils.launcher.ZLaunchTab;


/**
 * This tab permits to configure miscellanous Reflex options.
 * @author gpothier
 */
public abstract class OptionsTab<K> extends ZLaunchTab
{
	private List<AbstractItemControl<K>> itsControls;
	
	/**
	 * Maps items to SWT components.
	 */
	private Map<K, AbstractItemControl<K>> itsControlsMap;
	
	private Map<String, K> itsItemsMap;
	
	private List<K> itsItems;
	
	private boolean itsUpdating = false;

	public OptionsTab(List<K> aItems)
	{
		itsItems = aItems;
	}
	
	public OptionsTab(K... aItems)
	{
		itsItems = new ArrayList<K>();
		for(K theItem : aItems) itsItems.add(theItem);
	}

	public void createControl(Composite aParent)
	{
		ScrolledComposite theScroll = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		// Setup scroll & layout
		Composite theComposite = new Composite(theScroll, SWT.NONE);
		theScroll.setContent(theComposite);
		setControl(theScroll);
		
		GridLayout theTopLayout = new GridLayout();
		theTopLayout.numColumns = 1;
		theComposite.setLayout(theTopLayout);		

		// Create the argument panes
		itsControls = new ArrayList<AbstractItemControl<K>>();
		itsControlsMap = new HashMap<K, AbstractItemControl<K>>();
		itsItemsMap = new HashMap<String, K>();
		
		for (K theItem : itsItems)
		{
			AbstractItemControl<K> theControl = createControl(this, theComposite, theItem);
			theControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
			
			itsControls.add(theControl);
			itsControlsMap.put(theItem, theControl);
			itsItemsMap.put(getKey(theItem), theItem);
		}
		
		Point theSize = theComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		theComposite.setSize(theSize);
	}
	
	/**
	 * Returns the name that is used to load and store the options map
	 * from the configuration.
	 */
	protected abstract String getMapName();
	
	/**
	 * Returns a string that uniquely identifies the given item.
	 */
	protected abstract String getKey(K aItem);
	
	/**
	 * Returns a caption that describes the given item.
	 */
	public abstract String getCaption(K aItem);
	
	/**
	 * Returns a full description for the given item.
	 */
	public abstract String getDescription(K aItem);
	
	/**
	 * Returns the default value for the given item.
	 */
	protected abstract String getDefault(K aItem);
	
	/**
	 * Creates the SWT control used to edit the option corresponding
	 * to the given item.
	 * The control should call {@link #changed()} whenever its
	 * current value changes.
	 */
	protected abstract AbstractItemControl<K> createControl (
			OptionsTab<K> aOptionsTab, 
			Composite aParent, 
			K aItem);
	
	public void setDefaults(ILaunchConfigurationWorkingCopy aConfiguration)
	{
		Map<String, String> theMap = new HashMap<String, String>();
		for (K theItem : itsItems)
		{
			theMap.put(getKey(theItem), getDefault(theItem));
		}
		
		saveOptionsMap(theMap, aConfiguration);
	}

	public void initializeFrom(ILaunchConfiguration aConfiguration)
	{
		try
		{
			itsUpdating = true;
			Map<String, String> theMap = loadOptionsMap(aConfiguration);
			
			for(Map.Entry<K, AbstractItemControl<K>> theEntry : itsControlsMap.entrySet())
			{
				K theItem = theEntry.getKey();
				AbstractItemControl<K> theControl = theEntry.getValue();
				
				String theValue = theMap.get(getKey(theItem));
				if (theValue == null) theValue = getDefault(theItem);
				theControl.setValue(theValue);
			}
			
			itsUpdating = false;
		}
		catch (CoreException e)
		{
			EclipseUtils.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy aConfiguration)
	{
		Map<String, String> theMap = new HashMap<String, String>();
		
		for(Map.Entry<K, AbstractItemControl<K>> theEntry : itsControlsMap.entrySet())
		{
			K theItem = theEntry.getKey();
			AbstractItemControl<K> theControl = theEntry.getValue();
			
			String theValue = theControl.getValue();
			
			theMap.put(getKey(theItem), theValue);
		}
		
		saveOptionsMap(theMap, aConfiguration);
	}

	/**
	 * This method is called whenever some state changes
	 */
	public void changed()
	{
		if (! itsUpdating)
		{
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
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
