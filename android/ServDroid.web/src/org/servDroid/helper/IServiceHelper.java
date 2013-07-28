/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.helper;

import org.servDroid.server.service.ServerValues;
import org.servDroid.server.service.ServiceController;
import org.servDroid.server.service.params.ServerParams;

import android.os.RemoteException;

public interface IServiceHelper {

	public static final int STATUS_RUNNING = ServerValues.STATUS_RUNNING;
	public static final int STATUS_STOPPED = ServerValues.STATUS_STOPPED;
	public static final int STATUS_CONNECTED = 514;
	public static final int STATUS_DISCONNECTED = 514 + 1;
	public static final int STATUS_UNKNOWN = 514 + 2;

	public static final int DEFAULT_STATUS_REFRESH_TIME = 5000;

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
	 * Set the max amount of milliseconds that will take to get a new status
	 * event. By default it take {@link IServiceHelper}
	 * {@link #DEFAULT_STATUS_REFRESH_TIME} ms
	 * 
	 * @param time Time in ms
	 */
	public void setStatusTimeRefresh(int time);

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
