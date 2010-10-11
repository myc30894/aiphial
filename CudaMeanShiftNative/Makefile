# Makefile
# Generic Makefile for making cuda programs
#
BIN               := CudaMeanShiftNative
NAME              := cudameanshift
# flags
CUDA_INSTALL_PATH := /usr/local/cuda
CUDA_SDK_PATH     := /home/nickl/apps/NVIDIA_GPU_Computing_SDK
INCLUDES += -I. -I$(CUDA_INSTALL_PATH)/include -I$(CUDA_SDK_PATH)/C/common/inc -I/home/nickl/apps/jdk1.6/include -I/home/nickl/apps/jdk1.6/include/linux
LIBS              := -L$(CUDA_INSTALL_PATH)/lib64 -L$(CUDA_SDK_PATH)/C/common/lib/linux -L$(CUDA_SDK_PATH)/C/lib
CFLAGS            := -O3
LDFLAGS           := -lcudart
# compilers
NVCC              := nvcc
CC                := nvcc
CPP               := nvcc
LINKER            := nvcc
# files
C_SOURCES         := $(wildcard *.c)
CPP_SOURCES       := $(wildcard *.cpp)
CU_SOURCES        := $(wildcard *.cu)
HEADERS           := $(wildcard *.h)
C_OBJS            := $(patsubst %.c, %.o, $(C_SOURCES))
CPP_OBJS          := $(patsubst %.cpp, %.o, $(CPP_SOURCES))
CU_OBJS           := $(patsubst %.cu, %.o, $(CU_SOURCES))

$(BIN): clean

#	$(CC) -g -Xcompiler -fPIC -c $(C_SOURCES) $(CFLAGS) $(INCLUDES)
#	$(CPP) -c $(CPP_SOURCES) $(INCLUDES)
#	$(NVCC) -g  -deviceemu -Xcompiler -fPIC -c  $(CU_SOURCES) $(INCLUDES)
	$(NVCC) -g  -Xcompiler -fPIC -c  $(CU_SOURCES) $(INCLUDES)

#$(LINKER) -o $(BIN) $(CU_OBJS) $(C_OBJS) $(LDFLAGS) $(INCLUDES) $(LIBS)
	mkdir -p dist/Debug/CUDA-Linux-x86
	$(LINKER) -shared -o dist/Debug/CUDA-Linux-x86/lib$(NAME).so $(CU_OBJS) $(C_OBJS) $(CPP_OBJS) $(LDFLAGS) $(INCLUDES) $(LIBS)

#	$(C_OBJS): $(C_SOURCES) $(HEADERS)

#	$(CU_OBJS): $(CU_SOURCES) $(HEADERS)


run: $(BIN)
LD_LIBRARY_PATH=$(CUDA_INSTALL_PATH)/lib ./$(BIN)

clean:
	rm -f *.o