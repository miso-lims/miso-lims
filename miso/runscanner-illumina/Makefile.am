# Define an empty variable for list termination
NULL = 
ACLOCAL_AMFLAGS = -I m4

# Build the following binaries and put the in $prefix/bin
bin_PROGRAMS = \
	runscanner-illumina \
	$(NULL)
# Install the following manpages in $prefix/share/man/man1
man1_MANS = \
	runscanner-illumina.1 \
	$(NULL)

# These are all the source .cpp files for runscanner-illumina. Note the change
# of `-` to `_`.
runscanner_illumina_SOURCES = \
	main.cpp \
	$(NULL)
# These are the compiler arguments for building runscanner-illumina.
# We want (in order):
# - C++11
# - debugging symbols to be included
# - optimisation level 2
# - to search the interop directory for #include headers
# - whatever junk jsoncpp wants
runscanner_illumina_CPPFLAGS = \
	-std=c++11 -g -O2 \
	-I interop \
	$(JSON_CFLAGS) \
	$(NULL)
# These are the linker flags for building runscanner-illumina
# We need:
# - the statically-linked Illumina Interop library
# - whatever junk jsoncpp needs
runscanner_illumina_LDADD = \
	interop-build/src/interop/libinterop_lib.a \
	$(JSON_LIBS) \
	$(NULL)
