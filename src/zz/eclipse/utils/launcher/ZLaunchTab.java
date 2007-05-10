/*
 * Created on Aug 30, 2005
 */
package zz.eclipse.utils.launcher;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * This class can be used as a base for launch configuration tabs.
 * Provides several convenience methods and simplifies the handling of
 * validation through the {@link #isValid(ILaunchConfiguration)} method.
 * @author gpothier
 */
public abstract class ZLaunchTab extends JavaLaunchTab
{
	protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

	
	@Override
	public final boolean isValid(ILaunchConfiguration aLaunchConfig)
	{
		try
		{
			checkValid(aLaunchConfig);
			setErrorMessage(null);
			return true;
		}
		catch (InvalidLaunchConfiguration e)
		{
			setErrorMessage(e.getMessage());
			return false;
		}
	}
	
	/**
	 * Checks that the given launch configuration is valid. If it is not valid,
	 * this method exits with an {@link InvalidLaunchConfiguration}; it exists
	 * normally otherwise.
	 */
	protected void checkValid(ILaunchConfiguration aLaunchConfiguration) throws InvalidLaunchConfiguration
	{
	}

	/**
	 * Returns the Java project as defined in the current launch config.
	 */
	protected IJavaProject getJavaProject(ILaunchConfiguration aLaunchConfig)
	{
		String projectName = EMPTY_STRING;
		try 
		{
			projectName = aLaunchConfig.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					EMPTY_STRING);	
		} 
		catch (CoreException ce) 
		{
			JDIDebugUIPlugin.log(ce);
			return null;
		}
		
		return getJavaModel().getJavaProject(projectName);
	}
	
	/**
	 * Convenience method to get the workspace root.
	 */
	protected IWorkspaceRoot getWorkspaceRoot() 
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * Convenience method to get access to the java model.
	 */
	protected IJavaModel getJavaModel() 
	{
		return JavaCore.create(getWorkspaceRoot());
	}

	/**
	 * An exception thrown by {@link ZLaunchTab#checkValid(ILaunchConfiguration)}
	 * to indicate that the configuration is invalid.
	 * @author gpothier
	 */
	protected static class InvalidLaunchConfiguration extends Exception
	{
		public InvalidLaunchConfiguration(String aMessage)
		{
			super (aMessage);
		}

		public InvalidLaunchConfiguration(Throwable aCause)
		{
			super (aCause);
		}
	}

}
