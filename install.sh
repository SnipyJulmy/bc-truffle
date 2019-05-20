#!/usr/bin/env bash
sudo gu remove bc &&
cd ./component/ &&
sudo gu install -L ./bc-component.jar &&
cd ..
