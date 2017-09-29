# Define this package with name, version, and contact information
AC_INIT([runscanner-illumina], [1.0], [robert.davey@earlham.ac.uk])
# Configure automake in a standard way
AM_INIT_AUTOMAKE([-Wall -Werror foreign subdir-objects])
AC_CONFIG_MACRO_DIR([m4])
AC_CONFIG_HEADERS(config.h)
# Find a functioning C++ compiler
AC_PROG_CXX_C_O
# Find a functioning libtool, the C/C++ linking wrapper that handles complex
# dependencies. This is typical for AutoTools.
AC_PROG_LIBTOOL

# Detect jsoncpp using pkg-config. The detected library information will be
# bound to variables starting with `JSON_`
PKG_CHECK_MODULES(JSON, [ jsoncpp ])

# Using the detected information, transform `Makefile.in` to `Makefile`
AC_CONFIG_FILES([Makefile])
AC_OUTPUT
