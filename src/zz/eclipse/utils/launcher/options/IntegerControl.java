/*
 * Created on Jun 27, 2005
 */
package zz.eclipse.utils.launcher.options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class IntegerControl<K> extends AbstractItemControl<K> 
implements ModifyListener
{
	private Text itsText;
	
	public IntegerControl(Composite aParent, OptionsControl<K> aOptionsTab, K aItem)
	{
		super(aParent, aOptionsTab, aItem);
	}

	@Override
	protected Control createWidget()
	{
		itsText = new Text(this, SWT.BORDER);
		
		itsText.addModifyListener(this);
		return itsText;
	}

	public void modifyText(ModifyEvent aE)
	{
		changed();
	}

	@Override
	public String getValue()
	{
		String theText = itsText.getText();
		if (theText.length() > 0)
		{
			try
			{
				int theInteger = Integer.parseInt(theText);
				return Integer.toString(theInteger);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		else return null;
	}

	@Override
	public void setValue(String aValue)
	{
		itsText.setText(aValue != null ? aValue : "");
	}

}
