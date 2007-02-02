/*
 * Created on Dec 10, 2006
 */
package zz.eclipse.utils.launcher.options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractItemControl<K> extends Composite 
{
	private final OptionsControl<K> itsOptionsControl;
	private Label itsLabel;
	private K itsItem;

	public AbstractItemControl(Composite aParent, OptionsControl<K> aOptionsTab, K aItem)
	{
		super(aParent, SWT.BORDER);
		itsItem = aItem;
		itsOptionsControl = aOptionsTab;
		
		GridLayout theLayout = new GridLayout();
		theLayout.numColumns = 1;
		setLayout(theLayout);		

		GridData theGridData;
		
		itsLabel = new Label(this, SWT.WRAP);
		StringBuilder theText = new StringBuilder();
		theText.append(itsOptionsControl.getCaption(itsItem));
		theText.append("\n");
		theText.append(itsOptionsControl.getDescription(itsItem));
		itsLabel.setText(theText.toString());
		theGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		itsLabel.setLayoutData(theGridData);
		
		Control theWidget = createWidget();
		theGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		theWidget.setLayoutData(theGridData);
	}

	public K getItem()
	{
		return itsItem;
	}

	protected void changed()
	{
		itsOptionsControl.changed();
	}
	
	/**
	 * Creates the specific widget for this pane.
	 */
	protected abstract Control createWidget();
	
	/**
	 * Returns the current user input
	 */
	public abstract String getValue();

	/**
	 * Sets the current value of the argument.
	 */
	public abstract void setValue (String aValue);
}
