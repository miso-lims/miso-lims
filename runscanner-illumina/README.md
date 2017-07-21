# Illumina Support for Run Scanner
To use the Illumina support in Run Scanner, this additional package must be built.

You will need a copy of [jsoncpp](https://github.com/open-source-parsers/jsoncpp) and an AutoTools-capable build environment. On Debian/Ubuntu:

    sudo apt-get install cmake libjsoncpp-dev autoconf libtool build-essential

On Fedora/Red Hat:

    sudo yum groupinstall "Development Tools"
    sudo yum install cmake jsoncpp-devel libtool autoconf

Pull the Illumina Interop code via:

    git submodule init && git submodule update

Then:

    ./build-illumina-interop && autoreconf -i && ./configure && make && sudo make install

After which, configure Run Scanner as directed.
