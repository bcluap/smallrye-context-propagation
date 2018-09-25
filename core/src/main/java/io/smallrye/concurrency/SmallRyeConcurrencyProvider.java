package io.smallrye.concurrency;

import org.eclipse.microprofile.concurrent.ManagedExecutorBuilder;
import org.eclipse.microprofile.concurrent.ThreadContextBuilder;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyProvider;
import org.eclipse.microprofile.concurrent.spi.ConcurrencyProviderRegistration;

import io.smallrye.concurrency.impl.ManagedExecutorBuilderImpl;
import io.smallrye.concurrency.impl.ThreadContextBuilderImpl;

public class SmallRyeConcurrencyProvider implements ConcurrencyProvider {

	private static ConcurrencyProviderRegistration registration;

	public static void register() {
		if(registration == null)
			registration = ConcurrencyProvider.register(new SmallRyeConcurrencyProvider());
	}
	
	public static void unregister() {
		registration.unregister();
		registration = null;
	}

	private static SmallRyeConcurrencyManager defaultManager = new SmallRyeConcurrencyManager();
	
	private static ThreadLocal<SmallRyeConcurrencyManager> localManager = new ThreadLocal<SmallRyeConcurrencyManager>();
	
	@Override
	public ManagedExecutorBuilder newManagedExecutorBuilder() {
		return new ManagedExecutorBuilderImpl(getManager());
	}

	@Override
	public ThreadContextBuilder newThreadContextBuilder() {
		return new ThreadContextBuilderImpl(getManager());
	}
	
	public static SmallRyeConcurrencyManager getManager() {
		SmallRyeConcurrencyManager ret = localManager.get();
		if(ret != null)
			return ret;
		return defaultManager;
	}

	public static SmallRyeConcurrencyManager setLocalManager(SmallRyeConcurrencyManager manager) {
		SmallRyeConcurrencyManager previousManager = localManager.get();
		if(manager == null)
			localManager.remove();
		else
			localManager.set(manager);
		return previousManager;
	}
	
	public static void clearLocalManager() {
		localManager.remove();
	}
}