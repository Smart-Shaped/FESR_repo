package com.smartshaped.fesr.gencast.serving.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Setter
@Getter
@AllArgsConstructor
@PrimaryKeyClass
public class GencastModelId implements Serializable {
  @PrimaryKeyColumn(name = "level", type = PrimaryKeyType.PARTITIONED)
  private int level;

  @PrimaryKeyColumn(name = "lat", type = PrimaryKeyType.CLUSTERED)
  private float lat;

  @PrimaryKeyColumn(name = "lon", type = PrimaryKeyType.CLUSTERED)
  private float lon;

  @PrimaryKeyColumn(name = "datetime", type = PrimaryKeyType.CLUSTERED)
  private Instant dateTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GencastModelId that = (GencastModelId) o;
    return dateTime == that.dateTime
        && Float.compare(that.lat, lat) == 0
        && Float.compare(that.lon, lon) == 0
        && level == that.level;
  }

  @Override
  public int hashCode() {
    return Objects.hash(level, lat, lon, dateTime);
  }
}
