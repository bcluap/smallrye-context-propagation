package io.smallrye.concurrency.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.util.AnnotationLiteral;

import org.eclipse.microprofile.concurrent.ThreadContext;
import org.eclipse.microprofile.concurrent.ThreadContextConfig;

import io.smallrye.concurrency.SmallRyeConcurrencyManager;
import io.smallrye.concurrency.SmallRyeConcurrencyProvider;
import io.smallrye.concurrency.impl.ThreadContextImpl;

public class ThreadContextInjectionBean implements Bean<ThreadContext>, PassivationCapable {

	private BeanManager bm;

	public ThreadContextInjectionBean(BeanManager bm) {
		this.bm = bm;
	}

	@Override
	public ThreadContext create(CreationalContext<ThreadContext> creationalContext) {
		InjectionPoint ip = (InjectionPoint) bm.getInjectableReference(new InjectionPointMetadataInjectionPoint(),
				creationalContext);
		Annotated annotated = ip.getAnnotated();

		SmallRyeConcurrencyManager manager = SmallRyeConcurrencyProvider.getManager();
		ThreadContextConfig config = annotated.getAnnotation(ThreadContextConfig.class);

		String[] propagated;
		String[] unchanged;
		if (config != null) {
			propagated = config.value();
			unchanged = config.unchanged();
		} else {
			propagated = manager.getAllProviderTypes();
			unchanged = SmallRyeConcurrencyManager.NO_STRING;
		}
		return new ThreadContextImpl(manager, propagated, unchanged);
	}

	@Override
	public void destroy(ThreadContext instance, CreationalContext<ThreadContext> creationalContext) {
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(ThreadContext.class);
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Annotation> getQualifiers() {
		return Collections.<Annotation>singleton(new AnnotationLiteral<Default>() {
		});
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return Dependent.class;
	}

	@Override
	public String getName() {
		return "ThreadContextInjectionBean";
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public String getId() {
		return "ThreadContextInjectionBean";
	}

	@Override
	public Class<?> getBeanClass() {
		return ThreadContextInjectionBean.class;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	private static class InjectionPointMetadataInjectionPoint implements InjectionPoint {

		@Override
		public Type getType() {
			return InjectionPoint.class;
		}

		@SuppressWarnings("serial")
		@Override
		public Set<Annotation> getQualifiers() {
			return Collections.<Annotation>singleton(new AnnotationLiteral<Default>() {
			});
		}

		@Override
		public Bean<?> getBean() {
			return null;
		}

		@Override
		public Member getMember() {
			return null;
		}

		@Override
		public Annotated getAnnotated() {
			return null;
		}

		@Override
		public boolean isDelegate() {
			return false;
		}

		@Override
		public boolean isTransient() {
			return false;
		}

	}
}