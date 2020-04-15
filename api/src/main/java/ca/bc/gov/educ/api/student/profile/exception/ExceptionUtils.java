package ca.bc.gov.educ.api.student.profile.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * ExceptionUtils to provide tools to generate error messages
 */

public class ExceptionUtils {
  private ExceptionUtils() {
  }

  public static <K, V> Map<K, V> toMap(
          Class<K> keyType, Class<V> valueType, Object... entries) {
    if (entries.length % 2 == 1)
      throw new IllegalArgumentException("Invalid entries");
    return IntStream.range(0, entries.length / 2).map(i -> i * 2)
            .collect(HashMap::new,
                    (m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])),
                    Map::putAll);
  }

}