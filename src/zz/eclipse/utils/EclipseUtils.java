package zz.eclipse.utils;

import java.io.IOException;
import java.net.URL;

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
public class EclipseUtils extends AbstractUIPlugin {

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
				return pluginLoc.substring("file:".length()) + "lib"; 
			}
		}
		
		return null;
	}


}
