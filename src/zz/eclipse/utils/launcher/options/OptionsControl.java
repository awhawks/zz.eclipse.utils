/*
 * Created on Jan 31, 2007
 */
package zz.eclipse.utils.launcher.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public abstract class OptionsControl<K> extends Composite
{
	private List<AbstractItemControl<K>> itsControls;
	
	/**
	 * Maps items to SWT components.
	 */
	private Map<K, AbstractItemControl<K>> itsControlsMap;
	
	private Map<String, K> itsItemsMap;
	
	private List<K> itsItems;
	
	private boolean itsUpdating = false;

	public OptionsControl(Composite aParent, int aStyle, K... aItems)
	{
		super(aParent, aStyle);
		itsItems = new ArrayList<K>();
		for(K theItem : aItems) itsItems.add(theItem);
		createControl(this);
		
	}
	
	public OptionsControl(Composite aParent, int aStyle, List<K> aItems)
	{
		super(aParent, aStyle);
		itsItems = aItems;
		createControl(this);
	}
	
	public void createControl(Composite aParent)
	{
		GridLayout theTopLayout = new GridLayout();
		theTopLayout.numColumns = 1;
		setLayout(theTopLayout);		

		preFill(this);
		
		// Create the argument panes
		itsControls = new ArrayList<AbstractItemControl<K>>();
		itsControlsMap = new HashMap<K, AbstractItemControl<K>>();
		itsItemsMap = new HashMap<String, K>();
		
		for (K theItem : itsItems)
		{
			AbstractItemControl<K> theControl = createControl(this, theItem);
			theControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
			
			itsControls.add(theControl);
			itsControlsMap.put(theItem, theControl);
			itsItemsMap.put(getKey(theItem), theItem);
		}
		
		postFill(this);
		
		Point theSize = this.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		this.setSize(theSize);
	}
	
	/**
	 * This method gives subclasses an opportunity to add additional
	 * components to the tab, before all other components.
	 */
	protected void preFill(Composite aParent)
	{
	}
	
	/**
	 * This method gives subclasses an opportunity to add additional
	 * components to the tab, after all other components.
	 */
	protected void postFill(Composite aParent)
	{
	}
	
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
			Composite aParent, 
			K aItem);
	
	/**
	 * Returns a map of all default values.
	 */
	public Map<String, String> getDefaults()
	{
		Map<String, String> theMap = new HashMap<String, String>();
		for (K theItem : itsItems)
		{
			theMap.put(getKey(theItem), getDefault(theItem));
		}
		
		return theMap;
	}

	public void setValues(Map<String, String> aMap)
	{
		itsUpdating = true;
		
		for(Map.Entry<K, AbstractItemControl<K>> theEntry : itsControlsMap.entrySet())
		{
			K theItem = theEntry.getKey();
			AbstractItemControl<K> theControl = theEntry.getValue();
			
			String theValue = aMap.get(getKey(theItem));
			if (theValue == null) theValue = getDefault(theItem);
			theControl.setValue(theValue);
		}
		
		itsUpdating = false;
	}

	public Map<String, String> getValues()
	{
		Map<String, String> theMap = new HashMap<String, String>();
		
		for(Map.Entry<K, AbstractItemControl<K>> theEntry : itsControlsMap.entrySet())
		{
			K theItem = theEntry.getKey();
			AbstractItemControl<K> theControl = theEntry.getValue();
			
			String theValue = theControl.getValue();
			
			theMap.put(getKey(theItem), theValue);
		}

		return theMap;
	}

	/**
	 * Notifies this control that the state of one of the option
	 * controls has changed.
	 */
	final void changed()
	{
		if (! itsUpdating)
		{
			contentChanged();
		}
	}

	/**
	 * This method is called whenever the state changes.
	 */
	protected void contentChanged()
	{
	}
	
}
