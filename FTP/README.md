# Docker FTP Server

This repository contains a Docker-based FTP server setup using the `garethflowers/ftp-server` image. The server is configured to provide a simple FTP service with customizable credentials and shared storage.

## Overview

The FTP server runs in a Docker container and exposes standard FTP ports to allow file transfers. It's configured to work with the `surface_default` network and provides a shared directory for file storage.

## Prerequisites

- Docker and Docker Compose installed on your system
- Basic understanding of FTP protocols
- Network access to ports 20-21 and 40000-40009

## Configuration

The FTP server is configured using environment variables defined in the `.env` file:

- `FTP_USERNAME`: Username for FTP authentication (default: ftpuser)
- `FTP_PASSWORD`: Password for FTP authentication

## Directory Structure

```plaintext
.
├── .env                  # Environment variables for FTP credentials
├── docker-compose.yml    # Docker Compose configuration
├── README.md             # This documentation file
└── shared/               # Shared directory mounted in the FTP container
```

## Usage

### Starting the FTP Server

To start the FTP server, run:

```bash
docker-compose up -d
 ```

This will start the FTP server in detached mode.

### Connecting to the FTP Server
You can connect to the FTP server using any FTP client with the following details:

- Host : Your machine's IP address or hostname
- Port : 21
- Username : Value of FTP_USERNAME in the .env file (default: ftpuser)
- Password : Value of FTP_PASSWORD in the .env file
- Mode : Both active and passive modes are supported

## Port Configuration

The following ports are exposed:

- 20-21 : Standard FTP command and data ports
- 40000-40009 : Ports for passive mode operation

## Volume Mapping

The `./shared` directory on the host is mapped to `/home/ftpuser/shared` inside the container. Any files placed in this directory will be accessible via FTP.

## Network Configuration

The FTP server is configured to use the `surface_default` external network. Make sure this network exists before starting the container.
