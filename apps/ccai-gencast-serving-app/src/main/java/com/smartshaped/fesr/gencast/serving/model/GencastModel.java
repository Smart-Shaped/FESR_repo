package com.smartshaped.fesr.gencast.serving.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@Table("gencastprediction")
public class GencastModel {

  @PrimaryKey private GencastModelId id;

  @Column("geopotential")
  private Float geopotential;

  @Column("geopotential_error")
  private Float geopotentialError;

  @Column("mean_sea_level_pressure")
  private Float meanSeaLevelPressure;

  @Column("mean_sea_level_pressure_error")
  private Float meanSeaLevelPressureError;

  @Column("sea_surface_temperature")
  private Float seaSurfaceTemperature;

  @Column("sea_surface_temperature_error")
  private Float seaSurfaceTemperatureError;

  @Column("specific_humidity")
  private Float specificHumidity;

  @Column("specific_humidity_error")
  private Float specificHumidityError;

  @Column("temperature")
  private Float temperature;

  @Column("temperature_error")
  private Float temperatureError;

  @Column("temperature_2m")
  private Float temperature2m;

  @Column("temperature_2m_error")
  private Float temperature2mError;

  @Column("total_precipitation_12hr")
  private Float totalPrecipitation12hr;

  @Column("total_precipitation_12hr_error")
  private Float totalPrecipitation12hrError;

  @Column("u_component_of_wind")
  @JsonProperty("uComponentOfWind")
  private Float uComponentOfWind;

  @Column("u_component_of_wind_error")
  @JsonProperty("uComponentOfWindError")
  private Float uComponentOfWindError;

  @Column("u_component_of_wind_10m")
  @JsonProperty("uComponentOfWind10m")
  private Float uComponentOfWind10m;

  @Column("u_component_of_wind_10m_error")
  @JsonProperty("uComponentOfWind10mError")
  private Float uComponentOfWind10mError;

  @Column("v_component_of_wind")
  @JsonProperty("vComponentOfWind")
  private Float vComponentOfWind;

  @Column("v_component_of_wind_error")
  @JsonProperty("vComponentOfWindError")
  private Float vComponentOfWindError;

  @Column("v_component_of_wind_10m")
  @JsonProperty("vComponentOfWind10m")
  private Float vComponentOfWind10m;

  @Column("v_component_of_wind_10m_error")
  @JsonProperty("vComponentOfWind10mError")
  private Float vComponentOfWind10mError;

  @Column("vertical_velocity")
  private Float verticalVelocity;

  @Column("vertical_velocity_error")
  private Float verticalVelocityError;
}
