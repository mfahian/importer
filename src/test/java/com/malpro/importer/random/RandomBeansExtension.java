package com.malpro.importer.random;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;

public class RandomBeansExtension implements TestInstancePostProcessor, ParameterResolver {

  private final EasyRandom easyRandom;

  public RandomBeansExtension() {
    this(new EasyRandomParameters()
        .seed(123L)
        .objectPoolSize(10)
        .randomizationDepth(5)
        .charset(StandardCharsets.UTF_8)
        .stringLengthRange(5, 50)
        .collectionSizeRange(1, 10)
        .scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false));
  }

  public RandomBeansExtension(EasyRandomParameters randomParameters) {
    this.easyRandom = new EasyRandom(randomParameters);
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getAnnotation(Random.class) != null;
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return resolve(
        parameterContext.getParameter().getType(),
        parameterContext.getParameter().getAnnotation(Random.class));
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext)
      throws Exception {
    for (Field field : testInstance.getClass().getDeclaredFields()) {
      if (AnnotationSupport.isAnnotated(field, Random.class)) {
        Random annotation = field.getAnnotation(Random.class);
        Object randomObject = resolve(field.getType(), annotation);
        
        field.setAccessible(true);
        field.set(testInstance, randomObject);
      }
    }
  }


  private Object resolve(Class<?> targetType, Random annotation) {
    if (targetType.isAssignableFrom(List.class) || targetType.isAssignableFrom(Collection.class)) {
      return easyRandom
          .objects(annotation.type(), annotation.size())
          .collect(Collectors.toList());
    } else if (targetType.isAssignableFrom(Set.class)) {
      return easyRandom
          .objects(annotation.type(), annotation.size())
          .collect(Collectors.toSet());
    } else if (targetType.isAssignableFrom(Stream.class)) {
      return easyRandom.objects(annotation.type(), annotation.size());
    } else {
      return easyRandom.nextObject(targetType);
    }
  }

}
