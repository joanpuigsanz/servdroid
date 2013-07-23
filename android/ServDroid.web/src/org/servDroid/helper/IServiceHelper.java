package org.servDroid.helper;

import org.servDroid.server.service.ServiceController;
import org.servDroid.server.service.params.ServerParams;

import android.os.RemoteException;

public interface IServiceHelper {

	/**
	 * Connect to the service
	 */
	public void connect();

	/**
	 * Connect to the service and run a {@link Runnable} when the service is
	 * binded
	 * 
	 * @param runOnConnect
	 */
	public void connect(Runnable runOnConnect);

	/**
	 * Disconnect the service
	 */
	public void disconnect();

	/**
	 * Get if the service is connected
	 * 
	 * @return true if the service is connected, false otherwise
	 */
	public boolean isServiceConected();

	/**
	 * Start the server with the specified parameters
	 * 
	 * @param params
	 *            the parameters to run the server
	 * @return true if the server has been started
	 * @throws RemoteException
	 */
	public boolean startServer(ServerParams params) throws RemoteException;

	/**
	 * Start the server with the defined {@link ServerParams} in the ServDroid
	 * app
	 * 
	 * @return true if the server has been started, false otherwise
	 * @throws RemoteException
	 */
	public boolean startServer() throws RemoteException;

	/**
	 * Stop the server
	 * 
	 * @throws RemoteException
	 */
	public void stopServer() throws RemoteException;

	/**
	 * Add a {@link Runnable} to be processed whe the service is connected
	 * 
	 * @param runable
	 */
	public void addRunnableOnConnect(Runnable runable);

	/**
	 * Get the {@link ServiceController} that allows to interact with the server
	 * 
	 * @return
	 */
	public ServiceController getServiceController();

	/**
	 * Add a {@link ServerStatusListener} to be triggered when the
	 * server/service status is changed
	 * 
	 * @param serverEventListener
	 */
	public void addServerStatusListener(ServerStatusListener serverEventListener);

	/**
	 * It is recommended to unregister a listener before disconnect.
	 * 
	 * @param serverEventListener
	 */
	public void removeServerStatusListener(ServerStatusListener serverEventListener);

	public interface ServerStatusListener {

		public void onServerStatusChanged(ServiceController serviceController, int status);
	}

}
