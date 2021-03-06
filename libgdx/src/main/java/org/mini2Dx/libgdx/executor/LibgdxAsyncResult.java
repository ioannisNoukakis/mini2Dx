/*******************************************************************************
 * Copyright 2019 See AUTHORS file
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mini2Dx.libgdx.executor;

import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.executor.AsyncResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LibgdxAsyncResult<T> implements AsyncResult<T> {
	private static final String LOGGING_TAG = LibgdxAsyncResult.class.getSimpleName();

	private final Future<T> future;
	private T result;

	public LibgdxAsyncResult(Future<T> future) {
		this.future = future;
	}

	@Override
	public T getResult() {
		if(result == null) {
			try {
				result = future.get(100, TimeUnit.NANOSECONDS);
			} catch (TimeoutException e) {
				result = null;
			} catch (Exception e) {
				Mdx.log.error(LOGGING_TAG, e.getMessage(), e);
				result = null;
			}
		}
		return result;
	}

	@Override
	public boolean isFinished() {
		return future.isDone() || future.isCancelled();
	}
}
