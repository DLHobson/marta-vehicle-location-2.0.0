 package com.itsmarta.technology.its.martavehiclelocation.service.impl;

 import com.itsmarta.technology.its.martavehiclelocation.configuration.RouterMessageConfiguration;
 import com.itsmarta.technology.its.martavehiclelocation.service.PortAllocationService;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
 import org.springframework.stereotype.Service;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.TimeUnit;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReadWriteLock;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;

 @Service
public class PortAllocService implements com.itsmarta.technology.its.martavehiclelocation.service.PortAllocationService {
	private static final Logger logger = LogManager.getLogger(PortAllocService.class);

	private static final ReadWriteLock lock;
	private static final ReentrantLock mutex;
	private static final Lock writeLock;

	static {
		lock = new ReentrantReadWriteLock();
		mutex = new ReentrantLock();
		writeLock = lock.writeLock();
	}


	@Autowired
	private RouterMessageConfiguration routerMessageConfig;

	private final Map<Integer, Boolean> portMap = new HashMap<>();

	@Bean(name="portAllocationSVC")
	public PortAllocationService portAllocationSVC() {

		// To initialize the hashmap of ports, add all ports and set value 0 to true
		// to designate that ports are available.
		try{
			writeLock.lock();
			portMap.put(0,true);
			for(Integer item:routerMessageConfig.getListener().getPorts()) {
				portMap.put(item, true);
			}
		}
		finally {
				writeLock.unlock();
		}

		logger.info("List to hashmap" + portMap);

		return this;
	}

	@Override
	public Integer retrieveNextAvailablePort() throws InterruptedException {
		logger.info("Entering: retrieveNextAvailablePort()");
		Integer retValue = null;
		boolean foundPort = false;

		// No need to search for available ports if none are available
		if(portsAvailable()) {
			 // Try to acquire a mutex.  If successful, then follow normal logic
			 // else do nothing
			boolean lockAcquired = mutex.tryLock(500, TimeUnit.MILLISECONDS);
			if(lockAcquired) {
				logger.info("Lock acquired");
				try{
					logger.info("Entering critical section");

					 // Iterate through the hash map containing the ports
					 // The first entry that has a value of true (port available) is returned
					 // and the entry's value is set to false (port not available)
					for(Entry<Integer, Boolean> entry:portMap.entrySet()) {
						if((entry.getValue()) && (entry.getKey() != 0)) {
							foundPort = true;
							retValue = entry.getKey();

							logger.info("Entry: " + entry);
							entry.setValue(false);
							break;
						}
					}
					logger.info("Exiting critical section");
				}
				catch (Exception ex) {
					logger.error(ex.getMessage());
				}
				finally {
					mutex.unlock();
					logger.info("Lock released");
				}
			}
			else {
				logger.info("Lock was not acquired");
			}
			String msg;
			if(foundPort){
				msg = "Returning port: " + retValue;
			}
			else {
				portMap.replace(0,false);
				msg = "No port available.  Try again.";
			}
			logger.info(msg);
		}
		else logger.info("Not ports available");

		logger.info("Exiting: retrieveNextAvailablePort()");

		return retValue;
	}

	@Override
    public void setPortToAvailable(Integer key) {
		logger.info("Entering: setPortToAvailable(" + key + ")");

		// Acquire a WriteLock for the map.  Set the used port to
		// true (available).  If no ports were previously available
		// then change the status.  Finally, release the lock.
		try{
			writeLock.lock();
			logger.info("Lock acquired");
			logger.info("Entering critical section");

			logger.info("Releasing port: " + key);
			this.portMap.replace(key, true);

			if(!portsAvailable()) {
				logger.info("At least one port is available now, so setting to true");
				this.portMap.replace(0, true);
			}

			logger.info("Exiting critical section");
		}
		catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		finally {
			writeLock.unlock();
			logger.info("Lock released");
			logger.info("Exiting: setPortToAvailable(Integer key)");
		}
    }

	 @Override
	 public boolean portsAvailable() { return portMap.get(0);}

}
