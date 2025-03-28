package org.example.concurrentmodificationexception.config;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authorization.AuthorizationProxyFactory;
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ThreadsafeAuthorizationProxyFactory implements AuthorizationProxyFactory {
	private AuthorizationAdvisorProxyFactory delegate;

	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public Object proxy(Object object) {
		try {
			this.lock.lock();
			return this.delegate.proxy(object);
		} finally {
			this.lock.unlock();
		}
	}

	public void setDelegate(AuthorizationAdvisorProxyFactory delegate) {
		this.delegate = delegate;
	}
}
