package com.smartshaped.fesr.gencast.serving.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GencastCombinedModel {

  private GencastModelId id;

  private Float geopotential;
  private Float meanSeaLevelPressure;
  private Float seaSurfaceTemperature;
  private Float specificHumidity;
  private Float temperature;
  private Float temperature2m;
  private Float totalPrecipitation12hr;
  private Float verticalVelocity;

  @JsonProperty("vComponentOfWind")
  private Float vComponentOfWind;

  @JsonProperty("uComponentOfWind")
  private Float uComponentOfWind;

  @JsonProperty("vComponentOfWind10m")
  private Float vComponentOfWind10m;

  @JsonProperty("uComponentOfWind10m")
  private Float uComponentOfWind10m;
}
