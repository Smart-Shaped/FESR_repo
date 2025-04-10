package com.smartshaped.fesr.gencast.serving.controller;

import com.smartshaped.fesr.gencast.serving.model.GencastCombinedModel;
import com.smartshaped.fesr.gencast.serving.model.GencastModel;
import com.smartshaped.fesr.gencast.serving.model.GencastModelId;
import com.smartshaped.fesr.gencast.serving.repository.GencastRepository;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
    origins = "*",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/gencast")
public class GencastController {

  private final GencastRepository repository;

  public GencastController(GencastRepository repository) {
    this.repository = repository;
  }

  @Operation(summary = "Get first 500 rows of GenCast predictions")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the predictions"),
        @ApiResponse(responseCode = "404", description = "Predictions not found")
      })
  @GetMapping("/first500")
  public List<GencastModel> getFirst500() {
    return repository.findAll().stream()
        .sorted(Comparator.comparing(model -> model.getId().getDateTime()))
        .limit(500)
        .toList();
  }

  @Operation(summary = "Get all unique timestamps")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the timestamps"),
        @ApiResponse(responseCode = "404", description = "Timestamps not found")
      })
  @GetMapping("/timestamps")
  public List<String> getDatetimes() {
    Stream<GencastModel> stream = repository.findAll().stream();
    return stream
        .map(GencastModel::getId)
        .map(GencastModelId::getDateTime)
        .map(Instant::toString)
        .distinct()
        .toList();
  }

  @Operation(summary = "Get all unique levels")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the levels"),
        @ApiResponse(responseCode = "404", description = "Levels not found")
      })
  @GetMapping("/levels")
  public List<Integer> getAllUniqueLevels() {
    return repository.findAll().stream()
        .map(model -> model.getId().getLevel())
        .distinct()
        .sorted()
        .toList();
  }

  @Operation(summary = "Get first 500 combined Gencast predictions")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the combined predictions"),
        @ApiResponse(responseCode = "404", description = "Combined predictions not found")
      })
  @GetMapping("/combinedValues500")
  public List<GencastCombinedModel> getCombinedValues500() {
    return repository.findAll().stream()
        .sorted(Comparator.comparing(model -> model.getId().getDateTime()))
        .limit(500)
        .map(this::convertToCombinedModel)
        .toList();
  }

  @Operation(summary = "Get filtered points by level and timestamp")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the filtered points"),
        @ApiResponse(responseCode = "404", description = "Filtered points not found")
      })
  @GetMapping("/testSet/filter")
  public List<GencastCombinedModel> getFilteredPoints(
      @Parameter(description = "Level of the model") @RequestParam("level") int level,
      @Parameter(description = "Timestamp of the model") @RequestParam("timestamp")
          String timestamp) {
    Instant dateTime = Instant.parse(timestamp);

    return repository.findAll().stream()
        .filter(
            model ->
                model.getId().getLevel() == level && model.getId().getDateTime().equals(dateTime))
        .map(this::convertToCombinedModel)
        .toList();
  }

  @Operation(summary = "Get Gencast predictions by level and timestamp")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the predictions"),
        @ApiResponse(responseCode = "404", description = "Predictions not found")
      })
  @GetMapping("/prediction/filter")
  public List<GencastModel> getByLevelAndTimestamp(
      @Parameter(description = "Level of the model") @RequestParam("level") int level,
      @Parameter(description = "Timestamp of the model") @RequestParam("timestamp")
          String timestamp) {
    Instant dateTime = Instant.parse(timestamp);

    return repository.findAll().stream()
        .filter(
            model ->
                model.getId().getLevel() == level && model.getId().getDateTime().equals(dateTime))
        .toList();
  }

  @Operation(summary = "Get filtered points by level, timestamp, latitude, and longitude")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the filtered points"),
        @ApiResponse(responseCode = "404", description = "Filtered points not found")
      })
  @GetMapping("/prediction/point/filter")
  public List<GencastModel> getFilteredPointsByLevelTimestampLatLon(
      @Parameter(description = "Level of the model") @RequestParam("level") int level,
      @Parameter(description = "Timestamp of the model") @RequestParam("timestamp")
          String timestamp,
      @Parameter(description = "Latitude of the model") @RequestParam("lat") double latitude,
      @Parameter(description = "Longitude of the model") @RequestParam("lon") double longitude) {

    Instant dateTime = Instant.parse(timestamp);

    return repository.findAll().stream()
        .filter(
            model ->
                model.getId().getLevel() == level
                    && model.getId().getDateTime().equals(dateTime)
                    && model.getId().getLat() == latitude
                    && model.getId().getLon() == longitude)
        .toList();
  }

  @Operation(summary = "Get filtered points by level, latitude, and longitude")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the filtered points"),
        @ApiResponse(responseCode = "404", description = "Filtered points not found")
      })
  @GetMapping("/prediction/timeSeries/filter")
  public List<GencastModel> getFilteredPointsByLevelLatLon(
      @Parameter(description = "Level of the model") @RequestParam("level") int level,
      @Parameter(description = "Latitude of the model") @RequestParam("lat") double latitude,
      @Parameter(description = "Longitude of the model") @RequestParam("lon") double longitude) {

    return repository.findAll().stream()
        .filter(
            model ->
                model.getId().getLevel() == level
                    && model.getId().getLat() == latitude
                    && model.getId().getLon() == longitude)
        .sorted(Comparator.comparing(model -> model.getId().getDateTime()))
        .toList();
  }

  @Operation(summary = "Get all column names excluding IDs")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the column names"),
        @ApiResponse(responseCode = "404", description = "Column names not found")
      })
  @GetMapping("/columnNames/dataVars")
  public List<String> getAllColumnNamesExcludingIds() {
    List<String> columnNames = new ArrayList<>();

    for (Field field : GencastModel.class.getDeclaredFields()) {
      if (field.getName().equals("id")) {
        continue;
      }

      Column columnAnnotation = field.getAnnotation(Column.class);
      if (columnAnnotation != null) {
        columnNames.add(columnAnnotation.value());
      }
    }

    return columnNames;
  }

  public String underscoreToCamelCase(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    StringBuilder result = new StringBuilder();
    boolean capitalizeNext = false;

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);

      if (c == '_') {
        capitalizeNext = true;
      } else {
        if (capitalizeNext) {
          result.append(Character.toUpperCase(c));
          capitalizeNext = false;
        } else {
          result.append(c);
        }
      }
    }

    return result.toString();
  }

  @Operation(summary = "Get column names excluding errors")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Found the column names"),
        @ApiResponse(responseCode = "404", description = "Column names not found")
      })
  @GetMapping("/columnNames/dataVars/excludeErrors")
  public List<String> getColumnsExcludingErrors() {
    List<String> columnNames = new ArrayList<>();

    for (Field field : GencastModel.class.getDeclaredFields()) {
      if (field.getName().equals("id") || field.getName().endsWith("Error")) {
        continue;
      }

      Column columnAnnotation = field.getAnnotation(Column.class);
      if (columnAnnotation != null) {
        String columnName = columnAnnotation.value();

        if (!columnName.endsWith("_error")) {
          columnName = underscoreToCamelCase(columnName);
          columnNames.add(columnName);
        }
      }
    }

    return columnNames;
  }

  private GencastCombinedModel convertToCombinedModel(GencastModel model) {
    GencastCombinedModel combinedModel = new GencastCombinedModel();

    combinedModel.setId(model.getId());

    combinedModel.setGeopotential(calculateReal(model.getGeopotential(), model.getGeopotentialError()));
    combinedModel.setMeanSeaLevelPressure(
        calculateReal(model.getMeanSeaLevelPressure(), model.getMeanSeaLevelPressureError()));
    combinedModel.setSeaSurfaceTemperature(
        calculateReal(model.getSeaSurfaceTemperature(), model.getSeaSurfaceTemperatureError()));
    combinedModel.setSpecificHumidity(
        calculateReal(model.getSpecificHumidity(), model.getSpecificHumidityError()));
    combinedModel.setTemperature(calculateReal(model.getTemperature(), model.getTemperatureError()));
    combinedModel.setTemperature2m(
        calculateReal(model.getTemperature2m(), model.getTemperature2mError()));
    combinedModel.setTotalPrecipitation12hr(
        calculateReal(model.getTotalPrecipitation12hr(), model.getTotalPrecipitation12hrError()));
    combinedModel.setUComponentOfWind(
        calculateReal(model.getUComponentOfWind(), model.getUComponentOfWindError()));
    combinedModel.setVComponentOfWind(
        calculateReal(model.getVComponentOfWind(), model.getVComponentOfWindError()));
    combinedModel.setUComponentOfWind10m(
        calculateReal(model.getUComponentOfWind10m(), model.getUComponentOfWind10mError()));
    combinedModel.setVComponentOfWind10m(
        calculateReal(model.getVComponentOfWind10m(), model.getVComponentOfWind10mError()));
    combinedModel.setVerticalVelocity(
        calculateReal(model.getVerticalVelocity(), model.getVerticalVelocityError()));

    return combinedModel;
  }

  private Float calculateReal(Float a, Float b) {
    if (a == null || b == null) {
      return a;
    }
    return a - b;
  }
}
