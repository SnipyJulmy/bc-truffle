#!/usr/bin/env bash
sudo gu uninstall bc &&
cd ./component/ &&
sudo gu install -L ./bc-component.jar &&
cd ..
