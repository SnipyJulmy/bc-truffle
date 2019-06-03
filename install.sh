#!/usr/bin/env bash
sudo gu remove bc &&
cd ./component/ &&
sudo gu -L install ./bc-component.jar &&
cd ..
