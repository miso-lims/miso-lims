# Illumina Support for Run Scanner
To use the Illumina support in Run Scanner, this additional package must be built.

You will need a copy of [jsoncpp](https://github.com/open-source-parsers/jsoncpp) and an AutoTools-capable build environment. On Debian/Ubuntu:

    sudo apt-get install libjsoncpp-dev autoconf libtool build-essentials

On Fedora/Red Hat:

    sudo yum groupinstall "Development Tools"
    sudo yum install jsoncpp-devel libtool autoconf

Then:

    ./build-illumina-interop && autoreconf -i && ./configure && make && sudo make install

After which, configure Run Scanner as directed.
