# FESR Repository

This repository contains the source code for the SmartFESR teachnology stack, a modular and scalable toolset designed to support machine learning applications. The infrastructure is designed to be highly customizable and to support a wide range of use cases.

## Docker

SmartFESR can be used with Docker. The Docker documentation can be found in the [/docker](/docker) folder.

## Replication Study of the GenCast Model inference

This repository contains the source code for a replication study of the GenCast model, a state-of-the-art model for weather forecasting. The study uses the SmartFESR toolkit to download and process the data, and to perform the inference using the pre-trained model.

### Hardware requirements

According to the [official documentation](https://github.com/google-deepmind/graphcast/blob/main/docs/cloud_vm_setup.md), the GenCast model in inference with GPU requires at least 300GB of RAM and 60GB of vRAM.

### Steps

The steps to reproduce the study are as follows:

- The first step is to download and process the data using the [ccai-gencast-data_fusion-app](apps/ccai-gencast-data_fusion-app/README.md).

- The second step is to run the pre-trained GenCast model on the processed data using the [ccai-gencast-ml-app](apps/ccai-gencast-ml-app/README.md).

- The third step is to make predictions accessible using the [ccai_gencast-serving-app](apps/ccai-gencast-serving-app/README.md).

- The fourth and final step is to visualize the results using the [ccai_gencast_frontend-app](apps/ccai-gencast-frontend-app/README.md).

## Communication with Surface

In order to move data from Surface and make them accessible via FESR applications follow these steps:

- Install and run [Surface](surface/README.md).

- Run [FTP server](FTP/README.md).

- Run an FTP file ingestion with Surface, following this [tutorial](https://drive.google.com/file/d/1F-DJC0sLVAHiH9uqLj6SnU-G0-Wyc9m7/view?usp=sharing).

- Optionally, you can also execute a backup of all surface data, following this [tutorial](https://drive.google.com/file/d/1t6ycGFNASB2DO9ucPSzNnEy5bCHEBXAk/view?usp=sharing).

- Proceed on our [Docker documentation](/docker/README.md).

- Then, all Surface data can be reached from Fesr applications thanks to the [ccai_surface-data_fusion-app](apps/ccai-surface-data_fusion-app). In order to do that, watch the last [tutorial](https://drive.google.com/file/d/1neMW9stwkGOOuE28fnyKln4D9uMniadx/view?usp=sharing).

## Acknowledgments

- Developed by Smart Shaped srl
- Funded by REGIONE ABRUZZO A VALERE SUL PR FESR ABRUZZO 2021-2027, CODICE CUP C29J24000080007
