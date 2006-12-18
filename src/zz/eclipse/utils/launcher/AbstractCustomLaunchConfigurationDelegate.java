/*
 * Created on Jun 16, 2005
 */
package zz.eclipse.utils.launcher;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * Base class that can be used for implementing launch delegates for
 * applications in a local Java VM.
 * @author gpothier
 */
public abstract class AbstractCustomLaunchConfigurationDelegate 
extends AbstractJavaLaunchConfigurationDelegate
{
	public static final int MONITOR_STEPS = 3;

	public void launch(
			ILaunchConfiguration aConfiguration, 
			String aMode,
			ILaunch aLaunch, 
			IProgressMonitor aMonitor) throws CoreException
	{
		launch(aConfiguration, aMode, aLaunch, aMonitor, false);
	}

	/**
	 * 
	 * @param aMonitorStarted If true, this method will not call {@link IProgressMonitor#beginTask(java.lang.String, int)},
	 * assuming the caller already called it.
	 * Note that this method always consumes {@link #MONITOR_STEPS} monitor steps.
	 * @throws CoreException
	 */
	public void launch(
					ILaunchConfiguration aConfiguration, 
					String aMode,
					ILaunch aLaunch, 
					IProgressMonitor aMonitor,
					boolean aMonitorStarted) throws CoreException
	{
		if (aMonitor == null) aMonitor = new NullProgressMonitor();

		String theTaskName = MessageFormat.format("{0}...", new String[] { aConfiguration.getName() }); //$NON-NLS-1$
		if (aMonitorStarted) aMonitor.subTask(theTaskName);
		else aMonitor.beginTask(theTaskName, MONITOR_STEPS);
		
		// check for cancellation
		if (aMonitor.isCanceled()) { return; }

		aMonitor.subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Verifying_launch_attributes____1); //$NON-NLS-1$

		IVMRunner theRunner = getVMRunner(aConfiguration, aMode);

		File theWorkingDir = verifyWorkingDirectory(aConfiguration);
		String theWorkingDirName = null;
		if (theWorkingDir != null)
		{
			theWorkingDirName = theWorkingDir.getAbsolutePath();
		}

		// Environment variables
		String[] theEnvironment = getEnvironment(aConfiguration);

		// Program & VM args
		String theProgramArgs = getProgramArguments(aConfiguration);
		System.out.println("Program args: "+theProgramArgs);
		
		String theVMArgs = getVMArguments(aConfiguration);
		System.out.println("VM args: "+theVMArgs);
		
		ExecutionArguments theExecutionArguments = new ExecutionArguments(theVMArgs, theProgramArgs);

		// VM-specific attributes
		Map theVMAttributesMap = getVMSpecificAttributesMap(aConfiguration);

		// Classpath
		String[] theClasspath = getClasspath(aConfiguration);
		System.out.println("Classpath: "+Arrays.asList(theClasspath));
		
		// Create VM config
		VMRunnerConfiguration theRunnerConfiguration = new VMRunnerConfiguration(
				getMainTypeName(aConfiguration), 
				theClasspath);
		
		theRunnerConfiguration.setProgramArguments(theExecutionArguments.getProgramArgumentsArray());
		theRunnerConfiguration.setEnvironment(theEnvironment);
		theRunnerConfiguration.setVMArguments(theExecutionArguments.getVMArgumentsArray());
		theRunnerConfiguration.setWorkingDirectory(theWorkingDirName);
		theRunnerConfiguration.setVMSpecificAttributesMap(theVMAttributesMap);

		// Bootpath
		theRunnerConfiguration.setBootClassPath(getBootpath(aConfiguration));

		// check for cancellation
		if (aMonitor.isCanceled()) { return; }

		// stop in main
		prepareStopInMain(aConfiguration);

		// done the verification phase
		aMonitor.worked(1);

		aMonitor.subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Creating_source_locator____2); //$NON-NLS-1$
		// set the default source locator if required
		setDefaultSourceLocator(aLaunch, aConfiguration);
		aMonitor.worked(1);

		// Launch the configuration - 1 unit of work
		theRunner.run(theRunnerConfiguration, aLaunch, aMonitor);

		// check for cancellation
		if (aMonitor.isCanceled()) { return; }

		aMonitor.done();
	}

	@Override
	public final String[] getClasspath(ILaunchConfiguration aConfiguration) throws CoreException
	{
		String[] theClasspath = super.getClasspath(aConfiguration);
		
		Collection<String> theEntries = getAdditionalClassPathEntries(aConfiguration);
		if (theEntries != null && theEntries.size() > 0)
		{
			List<String> theResult = new ArrayList<String>();
			for (String thePath : theClasspath) theResult.add(thePath);
			theResult.addAll(theEntries);
			
			return theResult.toArray(new String[0]);
		}
		else return theClasspath;
	}
	
	@Override
	public final String getVMArguments(ILaunchConfiguration aConfiguration) throws CoreException
	{
		StringBuilder theArguments = new StringBuilder (super.getVMArguments(aConfiguration));
		
		Map<String, String> theProperties = getAdditionalSystemProperties(aConfiguration);
		if (theProperties != null) for (Map.Entry<String, String> theEntry : theProperties.entrySet())
		{
			theArguments.append(" -D");
			theArguments.append(theEntry.getKey());
			theArguments.append("=\"");
			theArguments.append(theEntry.getValue());
			theArguments.append("\"");
		}
        
        List<String> theAdditionalArguments = getAdditionalVMArguments(aConfiguration);
        for (String theArgument : theAdditionalArguments)
		{
            theArguments.append(" \"");
            theArguments.append(theArgument);
            theArguments.append("\"");
		}
		
		return theArguments.toString();
	}
	
	@Override
	public final String getProgramArguments(ILaunchConfiguration aConfiguration) throws CoreException
	{
		StringBuilder theArguments = new StringBuilder ();
		
		// Prepend additional program args.
		List<String> theAdditionalProgramArguments = getAdditionalPrependedProgramArguments(aConfiguration);
		for (String theArgument : theAdditionalProgramArguments)
		{
			theArguments.append(" ");
			theArguments.append(theArgument);
		}
		
		// Append original program args
		theArguments.append (super.getProgramArguments(aConfiguration));

		// Append additional program args.
		theAdditionalProgramArguments = getAdditionalProgramArguments(aConfiguration);
		for (String theArgument : theAdditionalProgramArguments)
		{
			theArguments.append(" ");
			theArguments.append(theArgument);
		}
		
		return theArguments.toString();
	}

	/**
	 * Returns a set of properties to pass to the VM. 
	 * Subclasses that override this method should call super and add their own options to
	 * the map.
	 */
	protected Map<String, String> getAdditionalSystemProperties(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return new HashMap<String, String>();
	}
    
    /**
     * Returns a list of arguments to pass to the launched VM
     * Subclasses that override this method should call super and add their own arguments to
     * the list.
     * If the objective is to add system properties, consider
     * {@link #getAdditionalSystemProperties(ILaunchConfiguration)} instead.
     */
    protected List<String> getAdditionalVMArguments(ILaunchConfiguration aConfiguration) throws CoreException
    {
        List<String> theList = new ArrayList<String>();
        
        List<String> theEntries = getPrependedBootClassPathEntries(aConfiguration);
        if (! theEntries.isEmpty())
        {
            StringBuilder theBuilder = new StringBuilder("-Xbootclasspath/p:");
            boolean theFirst = true;
            for (String theEntry : theEntries)
    		{
                if (! theFirst) theBuilder.append(File.pathSeparatorChar);
                else theFirst = false;
    			theBuilder.append(theEntry);
    		}
            theList.add (theBuilder.toString());
        }
        
        return theList;
    }
	
	
	/**
	 * Returns a list of additional arguments to pass to the program, before original arguments
	 * Subclasses that override this method should call super and add their own entries to the list. 
	 */
	protected List<String> getAdditionalPrependedProgramArguments(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Returns a list of additional arguments to pass to the program.
	 * Subclasses that override this method should call super and add their own entries to the list. 
	 */
	protected List<String> getAdditionalProgramArguments(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Returns a collection of additional entries to include in the launched VM's classpath.
	 * Subclasses that override this meethod should call super and add their own entries to the list. 
	 */
	protected List<String> getAdditionalClassPathEntries(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Returns a collection of entries that should be prepended to the boot class path
	 * Subclasses that override this meethod should call super and add their own entries to the list. 
	 */
	protected List<String> getPrependedBootClassPathEntries(ILaunchConfiguration aConfiguration) throws CoreException
	{
		return new ArrayList<String>();
	}
	
}
