package io.smallrye.concurrency.propagators.rxjava1;

import org.eclipse.microprofile.concurrent.ThreadContext;

import io.smallrye.concurrency.spi.ThreadContextPropagator;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.plugins.RxJavaHooks;

/**
 * Reactive Context propagator for RxJava 1. Supports propagating context to all {@link Single},
 * {@link Observable} and {@link Completable} types.
 *
 * @author Stéphane Épardaud
 */
public class RxJava1ContextPropagator implements ThreadContextPropagator {

	public void setup(ThreadContext threadContext) {
		RxJavaHooks.setOnSingleCreate(new ContextPropagatorOnSingleCreateAction(threadContext));
		RxJavaHooks.setOnObservableCreate(new ContextPropagatorOnObservableCreateAction(threadContext));
		RxJavaHooks.setOnCompletableCreate(new ContextPropagatorOnCompleteCreateAction(threadContext));
	}

}