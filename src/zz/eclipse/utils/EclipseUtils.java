package zz.eclipse.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus; 
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EclipseUtils extends AbstractUIPlugin 
{

	// The plug-in ID
	public static final String PLUGIN_ID = "zz.eclipse.utils";

	// The shared instance
	private static EclipseUtils plugin;
	
	/**
	 * The constructor
	 */
	public EclipseUtils() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EclipseUtils getDefault() {
		return plugin;
	}

	public static void log(Throwable e) 
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IJavaStatusConstants.INTERNAL_ERROR, JavaUIMessages.JavaPlugin_internal_error, e)); 
	}

	public static void log(IStatus status) 
	{
		getDefault().getLog().log(status);
	}
	
	/**
	 * Returns the path that contains the libraries of the given plugin
	 */
	public static String getLibraryPath(Plugin aPlugin)
	{
		Bundle theBundle = aPlugin.getBundle();

		String pluginLoc = null;
		if (theBundle != null) 
		{
			URL installLoc = theBundle.getEntry("/"); 
			URL resolved = null;
			try 
			{
				resolved = Platform.resolve(installLoc);
				pluginLoc = resolved.toExternalForm();
			} 
			catch (IOException e) 
			{
				throw new RuntimeException(e);
			}
		}
		
		if (pluginLoc != null) 
		{
			if (pluginLoc.startsWith("file:")) 
			{
				try 
				{
					pluginLoc = pluginLoc.replaceAll(" ", "%20");
					URI theUri = new URI(pluginLoc+"/lib");
					File theFile = new File(theUri);
					return theFile.getAbsolutePath();
				} 
				catch (URISyntaxException e) 
				{
					e.printStackTrace();
				} 
			}
		}
		
		return null;
	}

	/**
	 * Finds all the files with the given name in the project.
	 */
	public static IFile[] findFiles(IProject aProject, String aName)
	{
		try
		{
			FindFileVisitor theVisitor = new FindFileVisitor(aName);
			aProject.accept(theVisitor, 0);
			return theVisitor.getMatches();
		}
		catch (CoreException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static class FindFileVisitor implements IResourceProxyVisitor
	{
		private final String itsName;
		private List<IFile> itsMatches = new ArrayList<IFile>();

		public FindFileVisitor(String aName)
		{
			itsName = aName;
		}

		public boolean visit(IResourceProxy aProxy)
		{
			if (aProxy.getType() == IResource.FILE) 
			{
				if (aProxy.getName().equals(itsName)) itsMatches.add((IFile) aProxy.requestResource());
				return false;
			}
			else return true;
		}
		
		public IFile[] getMatches()
		{
			return itsMatches.toArray(new IFile[itsMatches.size()]);
		}
		
	}
}
