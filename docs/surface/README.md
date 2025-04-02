# How to set up Surface

## Prerequisites

- Docker installed

## 1. Clone Surface

- Go on [Surface](https://github.com/opencdms/surface) GitHub repository and clone it

- Copy the [production.env](resources/production.env) file inside the `api` folder

- Use this [Dockerfile](resources/Dockerfile) to replace the one in the `api` folder

## 2. Run Surface on Docker

- Run the following command to build the containers:  
     ```bash
     docker compose build
     ```

- Launch the environment in detached mode:  
     ```bash
     docker compose up -d
     ```
    At this point celery-beat and celery-worker containers may raise some errors, but that's not a problem, they will be automatically fixed after next operations.

## 3. Populate Surface DB

- Connect to the psql cli through this command (it will asks to insert the `dba` user password, which is the one written in the [production.env](resources/production.env)):
     ```bash
     docker compose exec postgres psql -U dba -h postgres -d postgres
     ```

- Now create the db running the following command:
     ```bash
     CREATE DATABASE surface_db;
     exit
     ```

- Then connect again to the psql cli, but this time running it on the newly created `surface_db`:
     ```bash
     docker compose exec postgres psql -U dba -h postgres -d surface_db
     ```

- And enable timescale extension if it's not already enabled:
     ```bash
     CREATE EXTENSION IF NOT EXISTS timescaledb;
     exit
     ```

- At this point run the following commands to initialize the database:
     ```bash
     docker compose exec api bash
     ```

     and then:

     ```bash
     python manage.py migrate
     ```

     ```bash
     python manage.py loaddata /surface/fixtures/*
     ```
- Then copy the three [sql script](resources) into the surface root folder and run these commands:
     ```bash
     Get-Content ./wx_station.sql | docker compose exec -T postgres psql -U dba -d surface_db
     ```
     ```bash
     Get-Content ./raw_data_station_id_14.sql | docker compose exec -T postgres psql -U dba -d surface_db
     ```
     ```bash
     Get-Content ./raw_data_station_id_108.sql | docker compose exec -T postgres psql -U dba -d surface_db
     ```

## 4. Create admin user for Surface

- At the end, run the following command to create the admin user and password:
     ```bash
     docker compose exec api python manage.py createsuperuser
     ```
