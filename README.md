# bc-truffle [![Build Status](https://travis-ci.org/SnipyJulmy/bc-truffle.svg?branch=master)](https://travis-ci.org/SnipyJulmy/bc-truffle)
bc (basic calculator) is an arbitrary precision calculator language. This project aims to implement
bc using the Truffle library in order to improve its performance.

## Getting started

In order to install the bc component on your own GrallVM, you can just do the following steps.

##### 1. Clone this repository
```bash
git clone git@github.com:SnipyJulmy/bc-truffle.git
```

##### 2. Build bc-truffle

Note : currently, there is a problem when building the project using a "normal" way 
(see this [issue](https://github.com/SnipyJulmy/bc-truffle/issues/3)). So you would have to use 
the `package.sh` script.

```
cd bc-truffle
chmod +x ./package.sh
./package.sh
```

##### 3. Installation
Make sure that your JAVA_HOME variable is pointing to a GraalVM JVM
(for example `JAVA_HOME="/usr/lib/jvm/java-8-graal"`).

Then we need to install the bc component into GraalVM (with or without sudo, depending on your setup) :
```
sudo gu -L install component/bc-component.jar
```

If the component is already installed, you just need to remove it before installing the new one :
```
sudo gu remove bc
```

##### 4. Checking the installation

At this point, the `bc` and `bcnative` program are available inside the `JAVAHOME/bin/` directory.

```
bc language/src/test/resources/bc/add_1.bc
bcnative language/src/test/resources/bc/add_1.bc
```

Both line should give `3` as a result.

## Example

TODO

## License

This repository is licensed under the UPL license.
