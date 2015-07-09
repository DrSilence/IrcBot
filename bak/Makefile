
include "Makefile.rules"

.PHONY: compile
compile:
	for i in $(DIRS); do make -C $$i; done

.PHONY: clean
clean:
	$(RM)$(BIN) $(DEPENDFILE) *.class
	for i in $(DIRS); do make -C $$i clean; done

.PHONY: depends
depends:
	for i in $(DIRS); do make -C $$i depends; done


