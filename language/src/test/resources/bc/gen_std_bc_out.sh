#!/usr/bin/env bash

for file in `ls | grep '\\.out'`
do
    rm ${file}
done

for file in `ls | grep '\\.bcout'`
do
    rm ${file}
done

if [[ ${BC_PATH} == "" ]]; then
    export BC_PATH="/usr/bin/bc"
fi

for file in `ls | grep '\\.bc'`
do
    echo ${file}
    filename="${file%.*}"
    ${BC_PATH} -q ${file} > ${filename}.out
done