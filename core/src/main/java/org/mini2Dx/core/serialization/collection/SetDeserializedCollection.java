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
package org.mini2Dx.core.serialization.collection;

import org.mini2Dx.core.exception.ReflectionException;
import org.mini2Dx.core.reflect.Field;
import org.mini2Dx.core.serialization.AotSerializationData;
import org.mini2Dx.core.serialization.aot.AotSerializedClassData;
import org.mini2Dx.core.serialization.aot.AotSerializedFieldData;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class used during JSON/XML deserialization
 */
public class SetDeserializedCollection extends DeserializedCollection<Set> {

	public SetDeserializedCollection(Class<?> ownerClass, Field field, Class<?> fieldClass, Object object) throws ReflectionException {
		super(ownerClass, field, fieldClass, object);
	}

	@Override
	public Class<? extends Set> getFallbackImplementation() {
		return HashSet.class;
	}

	@Override
	public Class<?> getValueClass() {
		AotSerializedClassData aotClassData = AotSerializationData.getClassData(ownerClass);
		if(aotClassData != null) {
			AotSerializedFieldData aotFieldData = aotClassData.getFieldData(field.getName());
			if(aotFieldData != null) {
				return aotFieldData.getElementType(0);
			}
		}
		return field.getElementType(0);
	}

	@Override
	public void add(Object element) {
		collection.add(element);
	}
}
