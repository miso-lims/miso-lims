# Illumina Support for Run Scanner
To use the Illumina support in Run Scanner, this additional package must be built.

You will need a copy of [jsoncpp](https://github.com/open-source-parsers/jsoncpp) and an AutoTools-capable build environment with libstdc++ 5 or later. On Debian/Ubuntu:

    sudo apt-get install pkg-config cmake libjsoncpp-dev autoconf libtool build-essential

On Fedora/Red Hat:

    sudo yum groupinstall "Development Tools"
    sudo yum install pkgconfig cmake jsoncpp-devel libtool autoconf

Pull the Illumina Interop code via:

    git submodule init && git submodule update

Then:

    ./build-illumina-interop && autoreconf -i && ./configure && make && sudo make install

After which, configure Run Scanner as directed.

## Developer Information
Illumina provides a library to read the contents of runs on disk for all
non-GA/GAII instruments. This library has no Java binding and potential memory
management issues. For these reasons, a separate C++ application exists that
uses the library to process the data and write JSON-encoded output for
consumption by MISO. Some additional processing is done in Java, but most of
the work is done by this program.

This is no automatic coupling to keep the Java and C++'s mapping of the JSON
object in sync, so this must be done manually. Furthermore, all the metrics
output is consumed by the JavaScript in the front end. All this coupling must
be maintained manually if refactoring is required.

The Illumina library targets C++98, but being not masochistic, this program
targets C++11. Therefore, the Illumina library is compiled, using CMake,
targeting C++11 (since C++98 and C++11 do not have compatible ABIs). This
program is compiled using GNU Autotools targeting C++11 and statically linked
again the Illumina library. There is an additional dependency on `jsoncpp` to
create the JSON. This must be provided by the system.

### Compiling
The `build-illumina-interop` compiles a copy of the Illumina interop library
with the correct build flags. The build flags have two goals: make a library
suitable for static linking to a C++11 binary and discard any features that are
irrelevant.

The `configure.ac` script will detect the build environment for this program.
It does not detect whether the Illumina library has been built correctly (or at
all). It does check for `jsoncpp` using `pkg-config`.

Automake works as follows:

- `configure.ac` is turned into a shell script `configure` by `autoconf`
- `Makefile.am` is turned into `Makefile.in` by `automake`
- `configure` runs and detects the build environment; it turns `Makefile.in`
  into `Makefile` with all the appropriate information included
- `Makefile` is run by `make` to compile, link, and install the program

`autoreconf` runs `autoconf` and `automake` as one step, with the correct
settings.

To clear the build environment, `make clean` will delete all the compiler
output and `make distclean` will delete the compiler output and `Makefile`. The
Illumina library can be cleaned separate using `make -C interop clean` or
deleting `interop-build`.

### Testing
There are no direct tests for this code. There are tests for Run Scanner that
test the output of this program against golden output. These tests are disabled
by default because this program must be built and put on the path, which is not
a normal user workflow. See the Run Scanner readme for details.
