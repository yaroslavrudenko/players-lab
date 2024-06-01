/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lab.players.cache;

import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Spring {@link org.springframework.cache.Cache} adapter implementation
 * on top of a Guava {@link com.google.common.cache.Cache} instance.
 *
 * <p>Requires Google Guava 12.0 or higher.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Yaroslav Rudenko
 * @since 4.0
 */
public class GuavaCache extends AbstractValueAdaptingCache {

	private final String name;

	private final com.google.common.cache.Cache<Object, Object> cache;


	/**
	 * Create a {@link GuavaCache} instance with the specified name and the
	 * given internal {@link com.google.common.cache.Cache} to use.
	 * @param name the name of the cache
	 * @param cache the backing Guava Cache instance
	 */
	public GuavaCache(String name, com.google.common.cache.Cache<Object, Object> cache) {
		this(name, cache, true);
	}

	/**
	 * Create a {@link GuavaCache} instance with the specified name and the
	 * given internal {@link com.google.common.cache.Cache} to use.
	 * @param name the name of the cache
	 * @param cache the backing Guava Cache instance
	 * @param allowNullValues whether to accept and convert {@code null}
	 * values for this cache
	 */
	public GuavaCache(String name, com.google.common.cache.Cache<Object, Object> cache, boolean allowNullValues) {
		super(allowNullValues);
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(cache, "Cache must not be null");
		this.name = name;
		this.cache = cache;
	}


	@Override
    @NonNull
	public final String getName() {
		return this.name;
	}

	@Override
    @NonNull
	public final com.google.common.cache.Cache<Object, Object> getNativeCache() {
		return this.cache;
	}

	@Override
    @Nullable
	public ValueWrapper get(@NonNull Object key) {
		if (this.cache instanceof LoadingCache) {
			try {
				Object value = ((LoadingCache<Object, Object>) this.cache).get(key);
				return toValueWrapper(value);
			}
			catch (ExecutionException ex) {
				throw new UncheckedExecutionException(ex.getMessage(), ex);
			}
		}
		return super.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
    @Nullable
	public <T> T get(@NonNull Object key, @NonNull final Callable<T> valueLoader) {
		try {
			return (T) fromStoreValue(this.cache.get(key, () -> toStoreValue(valueLoader.call())));
		}
		catch (ExecutionException | UncheckedExecutionException ex) {
			throw new ValueRetrievalException(key, valueLoader, ex.getCause());
		}
	}

	@Override
    @Nullable
	protected Object lookup(@NonNull Object key) {
		return this.cache.getIfPresent(key);
	}

	@Override
	public void put(@NonNull Object key, @Nullable Object value) {
		this.cache.put(key, toStoreValue(value));
	}

	@Override
    @Nullable
	public ValueWrapper putIfAbsent(@NonNull Object key, final Object value) {
		try {
			PutIfAbsentCallable callable = new PutIfAbsentCallable(value);
			Object result = this.cache.get(key, callable);
			return (callable.called ? null : toValueWrapper(result));
		}
		catch (ExecutionException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void evict(@NonNull Object key) {
		this.cache.invalidate(key);
	}

	@Override
	public void clear() {
		this.cache.invalidateAll();
	}


	private class PutIfAbsentCallable implements Callable<Object> {

		private final Object value;

		private boolean called;

		public PutIfAbsentCallable(Object value) {
			this.value = value;
		}

		@Override
		public Object call() {
			this.called = true;
			return toStoreValue(this.value);
		}
	}

}
