NULL = 
ACLOCAL_AMFLAGS = -I m4
bin_PROGRAMS = \
	runscanner-illumina \
	$(NULL)
man1_MANS = \
	runscanner-illumina.1 \
	$(NULL)

runscanner_illumina_SOURCES = \
	main.cpp \
	$(NULL)
runscanner_illumina_CPPFLAGS = \
	-std=c++11 -g -O2 \
	-I interop \
	$(JSON_CFLAGS) \
	$(NULL)
runscanner_illumina_LDADD = \
	interop-build/src/interop/libinterop_lib.a \
	$(JSON_LIBS) \
	$(NULL)
