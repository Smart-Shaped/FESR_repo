# FESR Repository

This repository contains the source code for the SmartFESR framework, a modular and scalable framework designed to support machine learning applications. The framework is designed to be highly customizable and to support a wide range of use cases.

## Docker

The framework can be used with Docker. The Docker documentation can be found in the [docs/docker](docs/docker) directory.

## Replication Study of the GenCast Model inference

This repository contains the source code for a replication study of the GenCast model, a state-of-the-art model for weather forecasting. The study uses the SmartFESR framework to download and process the data, and to perform the inference using the pre-trained model.

### Hardware requirements

According to the [official documentation](https://github.com/google-deepmind/graphcast/blob/main/docs/cloud_vm_setup.md), the GenCast model in inference with GPU requires at least 300GB of RAM and 60GB of vRAM.

### Steps

The steps to reproduce the study are as follows:

- [ccai-gencast-data_fusion-app steps](apps/ccai-gencast-data_fusion-app/README.md)

- [ccai-gencast-ml-app steps](apps/ccai-gencast-ml-app/README.md)

## Communication with Surface

In order to move data from Surface and make them accessible via FESR applications follow these steps:

- Install and run [Surface](surface/README.md)

- Follow the tutorial

## Acknowledgments

- Developed by Smart Shaped srl
- Funded by REGIONE ABRUZZO A VALERE SUL PR FESR ABRUZZO 2021-2027, CODICE CUP C29J24000080007
