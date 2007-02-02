/*
 * Created on Jun 27, 2005
 */
package zz.eclipse.utils.launcher.options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Pane for arguments without parameters.
 * @author gpothier
 */
public class BooleanControl<K> extends AbstractItemControl<K> 
implements SelectionListener
{
	private Button itsCheckButton;
	
	public BooleanControl(Composite aParent, OptionsControl<K> aOptionsTab, K aItem)
	{
		super(aParent, aOptionsTab, aItem);
	}


	@Override
	protected Control createWidget()
	{
		itsCheckButton = new Button(this, SWT.CHECK);
		itsCheckButton.setText("enable");
		
		itsCheckButton.addSelectionListener(this);
		return itsCheckButton;
	}

	public void widgetDefaultSelected(SelectionEvent aE)
	{
	}

	public void widgetSelected(SelectionEvent aE)
	{
		changed();
	}

	@Override
	public String getValue()
	{
		return Boolean.toString(itsCheckButton.getSelection());
	}

	@Override
	public void setValue(String aValue)
	{
		Boolean theBoolean = Boolean.parseBoolean(aValue);
		itsCheckButton.setSelection(theBoolean != null && theBoolean.booleanValue());
	}
	
	

}
