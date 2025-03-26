package com.smartshaped.smartfesr.gencast.ml.model;

import java.sql.Timestamp;

import com.smartshaped.smartfesr.common.utils.TableModel;

public class GenCastPrediction extends TableModel {

  Timestamp datetime;
  float lat;
  float lon;
  int level;
  float u_component_of_wind_10m;
  float u_component_of_wind_10m_error;
  float v_component_of_wind_10m;
  float v_component_of_wind_10m_error;
  float temperature_2m;
  float temperature_2m_error;
  float geopotential;
  float geopotential_error;
  float mean_sea_level_pressure;
  float mean_sea_level_pressure_error;
  float sea_surface_temperature;
  float sea_surface_temperature_error;
  float specific_humidity;
  float specific_humidity_error;
  float temperature;
  float temperature_error;
  float total_precipitation_12hr;
  float total_precipitation_12hr_error;
  float u_component_of_wind;
  float u_component_of_wind_error;
  float v_component_of_wind;
  float v_component_of_wind_error;
  float vertical_velocity;
  float vertical_velocity_error;

  @Override
  protected String choosePrimaryKey() {
    return "level,lon,lat,datetime";
  }
}
