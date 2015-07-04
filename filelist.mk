##########################################
# filelist.mk - unique for each directory
#
#include $(PRJ)/subdir1/filelist.mk
#include $(PRJ)/subdir2/filelist.mk

include $(PRJ)/src/filelist.mk

#filelist += \
#$(PRJ)/example/src/File1.java \
#$(PRJ)/example/src/File2.java 

