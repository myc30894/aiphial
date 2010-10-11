/*
 * File:   main.cpp
 * Author: nickl
 *
 * Created on 28 Декабрь 2009 г., 20:21
 */


#include "nbcudawa.h"

#include <cuda_runtime_api.h>
#include <stdio.h>
#include <stdlib.h>
#include <cuda_runtime.h>
#include <string.h>
#include <cutil.h>

#include "ru_nickl_meanshift_direct_cuda_NativeCudaMSFilter.h"

typedef struct LuvPoint
{
    float x;
    float y;
    float l;
    float u;
    float v;
};

#define GET_PITCHED(TYPE,ARR,PITCH,X,Y) (((TYPE*)((char* )ARR+Y*PITCH))+X)

__device__ LuvPoint GetElement(const LuvPoint* A,int pitch, int x, int y)
{
    return *(((LuvPoint*)((char* )A+y*pitch))+x);
   // return A[y*width+x];
    
}

__device__ void SetElement(LuvPoint* A,int pitch, int x, int y,LuvPoint value )
{
   *(((LuvPoint*)((char* )A+y*pitch))+x)=value;
    //A[y*width+x] = value;
}

__device__ void GetPointsWithin(LuvPoint* result, size_t* resultsize, const LuvPoint* src,int pitch,size_t width,size_t height, int x, int y, int squareRange)
{
    
    int size = 0;

    //printf("squareRange=%d\n",squareRange);

        for (int y0 = y - squareRange; y0 <= y + squareRange; y0++)
        {

            if (y0 >= 0 && y0 < height)
            {

                for (int x0 = x - squareRange; x0 <= x + squareRange; x0++)
                {
                    if (x0 >= 0 && x0 < width)
                    {
                        result[size++]=GetElement(src,pitch,x0, y0);
                    }
                }
            }
        }

    //printf("size=%d\n",size);

     *resultsize=size;
}

__device__ double colorDistance(LuvPoint a, LuvPoint b)
    {
        return (a.l - b.l)*(a.l - b.l) + (a.u - b.u)*(a.u - b.u) + (a.v - b.v)*(a.v - b.v);
    }

__device__ double fullDistance(LuvPoint a, LuvPoint b)
    {
        return (a.l - b.l)*(a.l - b.l) + (a.u - b.u)*(a.u - b.u) + (a.v - b.v)*(a.v - b.v)+ (a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y);
    }


__global__ void MeanShiftsplit(int a, int b, LuvPoint* srcArray,size_t pitch_src, LuvPoint* resultArray,size_t pitch_res,LuvPoint* tempArray,size_t pitch_temp, size_t h, size_t w, int st, float cs)
{


    int i = a * blockDim.x + threadIdx.x;
    int j = b * blockDim.y + threadIdx.y;

    //__syncthreads();

    if(i < w && j< h)
    {

    //LuvPoint shiftresult={i,j,0,0,0};
    
    LuvPoint shiftingpoint = GetElement(srcArray,pitch_src,i,j);

    LuvPoint shiftresult=shiftingpoint;



    //LuvPoint inSquare[729];


    //LuvPoint* inSquare =GET_PITCHED(LuvPoint,tempArray,pitch_temp,(threadIdx.x*(2*st+1)*(2*st+1)),threadIdx.y);

    //LuvPoint* inSquare = (((LuvPoint*)((char* )tempArray+threadIdx.y*pitch_temp))+threadIdx.x*(2*st+1)*(2*st+1));

    //LuvPoint* inSquare = tempArray +  blockIdx.y * blockDim.y *(2*st+1)*(2*st+1)  +  blockIdx.x * (2*st+1)*(2*st+1);

    LuvPoint* inSquare = tempArray + threadIdx.x * blockDim.y *(2*st+1)*(2*st+1)  + threadIdx.y * (2*st+1)*(2*st+1);

    //printf("inSquare=%d\n",inSquare);

    size_t inSquareSize = 0;

  //  printf("i=%d, j=%d, threadIdx.x=%d, threadIdx.y=%d, inSquare=%d\n",i, j,threadIdx.x,threadIdx.y,long(inSquare));

//    printf("i=%d, j=%d,\n",i, j);


//    if(i==0 && j == 32)
//    {
//
    


        LuvPoint prevposition;

        for (int s = 0; s < 10; s++)
        {

            prevposition = shiftresult;

         //   printf("sr = l=%f, u=%f, v=%f, x=%f, y=%f,\n",shiftresult.l,shiftresult.u,shiftresult.v, shiftresult.x, shiftresult.y);
         //   printf("x=%d, y=%d,\n",int(shiftresult.x + 0.5), int(shiftresult.y + 0.5));
            
            GetPointsWithin(inSquare, &inSquareSize, srcArray, pitch_src, w, h, int(shiftresult.x + 0.5), int(shiftresult.y + 0.5), st);

          //  printf("inSquareSizee=%d\n",inSquareSize);

            int count = 0;

            shiftresult.l = 0.;
            shiftresult.u = 0.;
            shiftresult.v = 0.;
            shiftresult.x = 0.;
            shiftresult.y = 0.;

            for (int k = 0; k < inSquareSize; k++)
            {
                LuvPoint point = inSquare[k];

//                printf("colorDistance(point, prevposition)=%f, cs * cs = %f\n",colorDistance(point, prevposition), cs * cs);
//                printf("point = l=%f, u=%f, v=%f, x=%f, y=%f,\n",point.l,point.u,point.v, point.x, point.y);

                if (colorDistance(point, prevposition) < cs * cs)
                {
                    shiftresult.l += point.l;
                    shiftresult.u += point.u;
                    shiftresult.v += point.v;
                    shiftresult.x += point.x;
                    shiftresult.y += point.y;
                    count++;
                }
            }
          //  printf("count=%d\n",count);
            

            shiftresult.l /= count;
            shiftresult.u /= count;
            shiftresult.v /= count;
            shiftresult.x /= count;
            shiftresult.y /= count;

           //  printf("l=%f, u=%f, v=%f, x=%f, y=%f,\n",shiftresult.l,shiftresult.u,shiftresult.v, int(shiftresult.x + 0.5), int(shiftresult.y + 0.5));

            if(colorDistance(prevposition, shiftresult)<3)
                break;

        }//while(fullDistance(prevposition, shiftresult)>10);


        shiftresult.x = i;
        shiftresult.y = j;

//}
    SetElement(resultArray,pitch_res,i,j,shiftresult );

    }

    //__syncthreads();


}


extern "C" void processsplit(double* srcArray, double* resultArray, size_t h, size_t w, int st, float cs)
{

    //st = 0;
    //h=300;
   // w=200;

    //fprintf(stderr,"using processsplit\n");



    LuvPoint* srcCuda;
    size_t pitch_src = w * sizeof (LuvPoint);
    CUDA_SAFE_CALL(cudaMallocPitch((void**) & srcCuda, &pitch_src, w * sizeof (LuvPoint), h));
    //CUDA_SAFE_CALL(cudaMalloc((void**) & srcCuda, w * sizeof (LuvPoint) * h));


    LuvPoint* resCuda;
    size_t pitch_res = w * sizeof (LuvPoint);
    CUDA_SAFE_CALL(cudaMallocPitch((void**) & resCuda, &pitch_res, w * sizeof (LuvPoint), h));
    //CUDA_SAFE_CALL(cudaMalloc((void**) & resCuda, w * sizeof (LuvPoint) * h));


   LuvPoint* src = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));
    for (int i = 0; i < w * h; i++)
    {
        src[i].l = srcArray[i * 3 + 0];
        src[i].u = srcArray[i * 3 + 1];
        src[i].v = srcArray[i * 3 + 2];
        src[i].x = i%w;
        src[i].y = i/w;
    }

    LuvPoint* dest = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));

    //memcpy(dest,src,w*h*sizeof (Luv));



    CUDA_SAFE_CALL(cudaMemcpy2D(srcCuda, pitch_src, src, w * sizeof (LuvPoint), w * sizeof (LuvPoint), h, cudaMemcpyHostToDevice));

 //   CUDA_SAFE_CALL(cudaMemcpy2D(resCuda, pitch_res, srcCuda, pitch_src, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToDevice));
//

    dim3 dimBlock(16,16);
    int amax = (w + dimBlock.x -1)/dimBlock.x;
    int bmax = (h+dimBlock.y -1)/dimBlock.y;
    dim3 dimGrid(1,1);

    LuvPoint* tempSqDataArray;
    size_t pitch_tempSqData;

    //CUDA_SAFE_CALL(cudaMallocPitch((void**) & tempSqDataArray, &pitch_tempSqData, dimBlock.x *(2*st+1)*(2*st+1) * sizeof (LuvPoint), dimBlock.y));

    size_t blockLength = (2*st+1)*(2*st+1) * sizeof (LuvPoint);

    CUDA_SAFE_CALL(cudaMalloc((void**) & tempSqDataArray, dimBlock.x * dimBlock.y *blockLength ));


       for (int b = 0; b < bmax; b++)
        {

    for (int a = 0; a < amax; a++)
    {
     

        // MeanShift<<<dimGrid, dimBlock>>>(a,b,(((LuvPoint*)((char* )srcCuda+b*pitch_src))+a),pitch_src, (((LuvPoint*)((char* )resCuda+b*pitch_res))+a),pitch_res,tempSqDataArray,pitch_tempSqData,h, w, st, cs);
        MeanShiftsplit<<<dimGrid, dimBlock>>>(a,b,srcCuda,pitch_src, resCuda,pitch_res,tempSqDataArray,pitch_tempSqData,h, w, st, cs);

           //cudaThreadSynchronize();


         CUDA_SAFE_CALL(cudaGetLastError());

        }
    }



    CUDA_SAFE_CALL(cudaFree(tempSqDataArray));

    CUDA_SAFE_CALL(cudaMemcpy2D(dest,w * sizeof (LuvPoint), resCuda, pitch_res, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToHost));


    for (int i = 0; i < w * h; i++)
    {
        resultArray[i * 3 + 0] = dest[i].l;
        resultArray[i * 3 + 1] = dest[i].u;
        resultArray[i * 3 + 2] = dest[i].v;
    }



    free(dest);
    free(src);
    CUDA_SAFE_CALL(cudaFree(srcCuda));
    CUDA_SAFE_CALL(cudaFree(resCuda));

}


__global__ void MeanShiftstack( LuvPoint* srcArray,size_t pitch_src, LuvPoint* resultArray,size_t pitch_res, size_t h, size_t w, int st, float cs)
{

    int i = blockIdx.x * blockDim.x + threadIdx.x;
    int j = blockIdx.y  * blockDim.y + threadIdx.y;

    //__syncthreads();

    if(i < w && j< h)
    {

    //LuvPoint shiftresult={i,j,0,0,0};

    LuvPoint shiftingpoint = GetElement(srcArray,pitch_src,i,j);

    LuvPoint shiftresult=shiftingpoint;



    LuvPoint inSquare[729];


    size_t inSquareSize = 0;

        LuvPoint prevposition;

        for (int s = 0; s < 10; s++)
        {

            prevposition = shiftresult;

            GetPointsWithin(inSquare, &inSquareSize, srcArray, pitch_src, w, h, int(shiftresult.x + 0.5), int(shiftresult.y + 0.5), st);

            int count = 0;

            shiftresult.l = 0.;
            shiftresult.u = 0.;
            shiftresult.v = 0.;
            shiftresult.x = 0.;
            shiftresult.y = 0.;

            for (int k = 0; k < inSquareSize; k++)
            {
                LuvPoint point = inSquare[k];

                if (colorDistance(point, prevposition) < cs * cs)
                {
                    shiftresult.l += point.l;
                    shiftresult.u += point.u;
                    shiftresult.v += point.v;
                    shiftresult.x += point.x;
                    shiftresult.y += point.y;
                    count++;
                }
            }


            shiftresult.l /= count;
            shiftresult.u /= count;
            shiftresult.v /= count;
            shiftresult.x /= count;
            shiftresult.y /= count;

            if(colorDistance(prevposition, shiftresult)<3)
                break;

        }

        shiftresult.x = i;
        shiftresult.y = j;


    SetElement(resultArray,pitch_res,i,j,shiftresult );

    }

    //__syncthreads();


}


extern "C" void processstack(double* srcArray, double* resultArray, size_t h, size_t w, int st, float cs)
{

    //st = 0;
    //h=300;
   // w=200;

    //fprintf(stderr,"using processstack\n");

     if(st>13)
         fprintf(stderr,"spatial side cannot be more than 13, setted value is %d\n", st);
        return;
    
    LuvPoint* srcCuda;
    size_t pitch_src = w * sizeof (LuvPoint);
    CUDA_SAFE_CALL(cudaMallocPitch((void**) & srcCuda, &pitch_src, w * sizeof (LuvPoint), h));
    //CUDA_SAFE_CALL(cudaMalloc((void**) & srcCuda, w * sizeof (LuvPoint) * h));


    LuvPoint* resCuda;
    size_t pitch_res = w * sizeof (LuvPoint);
    CUDA_SAFE_CALL(cudaMallocPitch((void**) & resCuda, &pitch_res, w * sizeof (LuvPoint), h));
    //CUDA_SAFE_CALL(cudaMalloc((void**) & resCuda, w * sizeof (LuvPoint) * h));


   LuvPoint* src = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));
    for (int i = 0; i < w * h; i++)
    {
        src[i].l = srcArray[i * 3 + 0];
        src[i].u = srcArray[i * 3 + 1];
        src[i].v = srcArray[i * 3 + 2];
        src[i].x = i%w;
        src[i].y = i/w;
    }

    LuvPoint* dest = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));

    //memcpy(dest,src,w*h*sizeof (Luv));



    CUDA_SAFE_CALL(cudaMemcpy2D(srcCuda, pitch_src, src, w * sizeof (LuvPoint), w * sizeof (LuvPoint), h, cudaMemcpyHostToDevice));

 //   CUDA_SAFE_CALL(cudaMemcpy2D(resCuda, pitch_res, srcCuda, pitch_src, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToDevice));
//

    dim3 dimBlock(16,16);
    dim3 dimGrid((w + dimBlock.x -1)/dimBlock.x,(h+dimBlock.y -1)/dimBlock.y);

    LuvPoint* tempSqDataArray;
    size_t pitch_tempSqData;

    //CUDA_SAFE_CALL(cudaMallocPitch((void**) & tempSqDataArray, &pitch_tempSqData, dimBlock.x *(2*st+1)*(2*st+1) * sizeof (LuvPoint), dimBlock.y));

    size_t blockLength = (2*st+1)*(2*st+1) * sizeof (LuvPoint);


    MeanShiftstack<<<dimGrid, dimBlock>>>(srcCuda,pitch_src, resCuda,pitch_res,h, w, st, cs);
    //cudaThreadSynchronize();


    CUDA_SAFE_CALL(cudaGetLastError());


    CUDA_SAFE_CALL(cudaMemcpy2D(dest,w * sizeof (LuvPoint), resCuda, pitch_res, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToHost));


    for (int i = 0; i < w * h; i++)
    {
        resultArray[i * 3 + 0] = dest[i].l;
        resultArray[i * 3 + 1] = dest[i].u;
        resultArray[i * 3 + 2] = dest[i].v;
    }



    free(dest);
    free(src);
    CUDA_SAFE_CALL(cudaFree(srcCuda));
    CUDA_SAFE_CALL(cudaFree(resCuda));

}



//
//extern "C" void processfull(double* srcArray, double* resultArray, size_t h, size_t w, int st, float cs)
//{
//
//    //st = 0;
//    //h=300;
//   // w=200;
//
//    LuvPoint* srcCuda;
//    size_t pitch_src = w * sizeof (LuvPoint);
//    CUDA_SAFE_CALL(cudaMallocPitch((void**) & srcCuda, &pitch_src, w * sizeof (LuvPoint), h));
//    //CUDA_SAFE_CALL(cudaMalloc((void**) & srcCuda, w * sizeof (LuvPoint) * h));
//
//
//    LuvPoint* resCuda;
//    size_t pitch_res = w * sizeof (LuvPoint);
//    CUDA_SAFE_CALL(cudaMallocPitch((void**) & resCuda, &pitch_res, w * sizeof (LuvPoint), h));
//    //CUDA_SAFE_CALL(cudaMalloc((void**) & resCuda, w * sizeof (LuvPoint) * h));
//
//
//   LuvPoint* src = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));
//    for (int i = 0; i < w * h; i++)
//    {
//        src[i].l = srcArray[i * 3 + 0];
//        src[i].u = srcArray[i * 3 + 1];
//        src[i].v = srcArray[i * 3 + 2];
//        src[i].x = i%w;
//        src[i].y = i/w;
//    }
//
//    LuvPoint* dest = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));
//
//    //memcpy(dest,src,w*h*sizeof (Luv));
//
//
//
//    CUDA_SAFE_CALL(cudaMemcpy2D(srcCuda, pitch_src, src, w * sizeof (LuvPoint), w * sizeof (LuvPoint), h, cudaMemcpyHostToDevice));
//
// //   CUDA_SAFE_CALL(cudaMemcpy2D(resCuda, pitch_res, srcCuda, pitch_src, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToDevice));
////
//
//    dim3 dimBlock(16,16);
//    dim3 dimGrid((w + dimBlock.x -1)/dimBlock.x,(h+dimBlock.y -1)/dimBlock.y);
//
//    LuvPoint* tempSqDataArray;
//    size_t pitch_tempSqData;
//
//    //CUDA_SAFE_CALL(cudaMallocPitch((void**) & tempSqDataArray, &pitch_tempSqData, dimBlock.x *(2*st+1)*(2*st+1) * sizeof (LuvPoint), dimBlock.y));
//
//    size_t blockLength = (2*st+1)*(2*st+1) * sizeof (LuvPoint);
//
//    CUDA_SAFE_CALL(cudaMalloc((void**) & tempSqDataArray, dimBlock.x * dimBlock.y *blockLength ));
//
//    LuvPoint* tempfiller = (LuvPoint*) malloc(dimBlock.x * dimBlock.y *blockLength);
//
//    for (int i = 0; i < dimBlock.x * dimBlock.y * (2*st+1)*(2*st+1); i++)
//    {
//
//        tempfiller[i].l = 0.;
//        tempfiller[i].u = 0.;
//        tempfiller[i].v = 0.;
//        tempfiller[i].x = 0.;
//        tempfiller[i].y = 0.;
//    }
//
//    CUDA_SAFE_CALL(cudaMemcpy(tempSqDataArray,tempfiller, dimBlock.x * dimBlock.y *blockLength, cudaMemcpyHostToDevice));
//
//
//
//    printf("tempSqDataArraystart=%d, blockLength=%d\n",long(tempSqDataArray), blockLength);
//
//    printf("a=%d, b=%d\n",dimGrid.x,dimGrid.y);
//
//    printf("h=%d, w=%d, st=%d, cs=%f,\n",h, w, st, cs);
//
//    MeanShift<<<dimGrid, dimBlock>>>(srcCuda,pitch_src, resCuda,pitch_res,tempSqDataArray,pitch_tempSqData,h, w, st, cs);
//    //cudaThreadSynchronize();
//
//
//    CUDA_SAFE_CALL(cudaGetLastError());
//
//
//    CUDA_SAFE_CALL(cudaMemcpy(tempfiller, tempSqDataArray, dimBlock.x * dimBlock.y *blockLength, cudaMemcpyDeviceToHost));
//
//    for (int i = 0; i < dimBlock.x * dimBlock.y * (2*st+1)*(2*st+1); i++)
//    {
//
//        LuvPoint p = tempfiller[i];
//
//        //printf("%f %f\n",p.x,p.y);
//    }
//
//    free(tempfiller);
//    CUDA_SAFE_CALL(cudaFree(tempSqDataArray));
//
//    CUDA_SAFE_CALL(cudaMemcpy2D(dest,w * sizeof (LuvPoint), resCuda, pitch_res, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToHost));
//
//
//    for (int i = 0; i < w * h; i++)
//    {
//        resultArray[i * 3 + 0] = dest[i].l;
//        resultArray[i * 3 + 1] = dest[i].u;
//        resultArray[i * 3 + 2] = dest[i].v;
//    }
//
//
//
//    free(dest);
//    free(src);
//    CUDA_SAFE_CALL(cudaFree(srcCuda));
//    CUDA_SAFE_CALL(cudaFree(resCuda));
//
//}
//

/*
 */

// <editor-fold defaultstate="collapsed" desc="deformed process1">

__global__ void deformed(LuvPoint* A, LuvPoint* B,
                         LuvPoint* C, LuvPoint* srcArray, LuvPoint* resultArray, LuvPoint* tempArray, size_t h, size_t w, int st, double cs)
{



    int i = blockIdx.x * blockDim.x + threadIdx.x;
    int j = blockIdx.y * blockDim.y + threadIdx.y;
    if (i >= w || j >= h)
        return;


    LuvPoint shiftresult = {i, j, 100, 100, 100};

    LuvPoint shiftingpoint = GetElement(srcArray, w, i, j);

    SetElement(resultArray, w, i, j, shiftresult);


    (*(C + j * w + i)).l = (*(A + j * w + i)).l + (*(B + j * w + i)).l;
    (*(C + j * w + i)).u = (*(A + j * w + i)).u + (*(B + j * w + i)).u;
    (*(C + j * w + i)).v = (*(A + j * w + i)).v + (*(B + j * w + i)).v;




}

extern "C" void process1(double* srcArray, double* resultArray, size_t h, size_t w, int st, double cs)
{

    // h = 300;
    // w = 300;

    LuvPoint* srcCuda;
    //size_t pitch_src;// = w * sizeof (LuvPoint);
    //CUDA_SAFE_CALL(cudaMallocPitch((void**) & srcCuda, &pitch_src, w * sizeof (LuvPoint), h));
    CUDA_SAFE_CALL(cudaMalloc((void**) & srcCuda, w * sizeof (LuvPoint) * h));


    LuvPoint* resCuda;
    //size_t pitch_res;// = w * sizeof (LuvPoint);
    //CUDA_SAFE_CALL(cudaMallocPitch((void**) & resCuda, &pitch_res, w * sizeof (LuvPoint), h));
    CUDA_SAFE_CALL(cudaMalloc((void**) & resCuda, w * sizeof (LuvPoint) * h));


    LuvPoint* src = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));
    for (int i = 0; i < w * h; i++)
    {
        src[i].l = srcArray[i * 3 + 0];
        src[i].u = srcArray[i * 3 + 1];
        src[i].v = srcArray[i * 3 + 2];
        src[i].x = i / w;
        src[i].y = i % w;
    }

    LuvPoint* dest = (LuvPoint*) malloc(w * h * sizeof (LuvPoint));

    //memcpy(dest,src,w*h*sizeof (Luv));



    CUDA_SAFE_CALL(cudaMemcpy(srcCuda, src, w * sizeof (LuvPoint) * h, cudaMemcpyHostToDevice));

    //   CUDA_SAFE_CALL(cudaMemcpy2D(resCuda, pitch_res, srcCuda, pitch_src, w * sizeof (LuvPoint), h, cudaMemcpyDeviceToDevice));
    //
    LuvPoint* tempSqDataArray;
    //size_t pitch_tempSqData;
    CUDA_SAFE_CALL(cudaMalloc((void**) & tempSqDataArray, w * sizeof (LuvPoint) * st * st * h));

    printf("h=%d, w=%d, st=%d, cs=%f,\n", h, w, st, cs);


    //   dim3 dimBlock(16, 16);
    //   dim3 dimGrid((w + dimBlock.x – 1) / dimBlock.x,(h + dimBlock.y – 1) / dimBlock.y);

    //    dim3 dimBlock(8,8);
    //    dim3 dimGrid((w + dimBlock.x -1)/dimBlock.x,(h+dimBlock.y -1)/dimBlock.y);
    //
    //    printf("a=%d, b=%d\n",(w + dimBlock.x -1)/dimBlock.x,(h+dimBlock.y -1)/dimBlock.y);

    //MeanShift<<<dimGrid, dimBlock>>>(srcCuda,pitch_src, resCuda,pitch_res,tempSqDataArray,pitch_tempSqData,h, w, st, cs);



    // -------------------------------------------------

    printf("h=%d, w=%d, st=%d, cs=%f,\n", h, w, st, cs);

    int size = h * w * sizeof (LuvPoint);
    // Allocate input vectors h_A and h_B in host memory
    LuvPoint* h_A = (LuvPoint*) malloc(size);
    LuvPoint* h_B = (LuvPoint*) malloc(size);
    LuvPoint* h_C = (LuvPoint*) malloc(size);
    // Allocate vectors in device memory
    LuvPoint* d_A;
    CUDA_SAFE_CALL(cudaMalloc((void**) & d_A, size));
    LuvPoint* d_B;
    CUDA_SAFE_CALL(cudaMalloc((void**) & d_B, size));
    LuvPoint* d_C;
    CUDA_SAFE_CALL(cudaMalloc((void**) & d_C, size));

    for (int i = 0; i < h * w; i++)
    {
        h_A[i].l = i;
        h_A[i].u = i;
        h_A[i].v = i;
        h_B[i].l = i;
        h_B[i].u = i;
        h_B[i].v = i;

    }

    // Copy vectors from host memory to device memory
    CUDA_SAFE_CALL(cudaMemcpy(d_A, h_A, size, cudaMemcpyHostToDevice));
    CUDA_SAFE_CALL(cudaMemcpy(d_B, h_B, size, cudaMemcpyHostToDevice));
    // Invoke kernel

    dim3 dimBlock1(16, 16);
    dim3 dimGrid1((w + dimBlock1.x - 1) / dimBlock1.x, (h + dimBlock1.y - 1) / dimBlock1.y);

    printf("a=%d, b=%d\n", (w + dimBlock1.x - 1) / dimBlock1.x, (h + dimBlock1.y - 1) / dimBlock1.y);

    deformed <<<dimGrid1, dimBlock1>>>(d_A, d_B, d_C, srcCuda, resCuda, tempSqDataArray, h, w, st, cs);

    //deformed<<<dimGrid1, dimBlock1>>>(d_A, d_B, d_C,0,0, 0,0,0,0,h, w, st, cs);

    // Copy result from device memory to host memory
    // h_C contains the result in host memory
    CUDA_SAFE_CALL(cudaMemcpy(h_C, d_C, size, cudaMemcpyDeviceToHost));

    CUDA_SAFE_CALL(cudaGetLastError());


    for (int i = 0; i < w * h && i < 10; i++)
    {
        printf("l=%0.2f u=%0.2f v=%0.2f, ", h_A[i].l, h_A[i].u, h_A[i].v);
    }
    printf("\n");
    for (int i = 0; i < w * h && i < 10; i++)
    {
        printf("l=%0.2f u=%0.2f v=%0.2f, ", h_B[i].l, h_B[i].u, h_B[i].v);
    }
    printf("\n");
    for (int i = 0; i < w * h && i < 10; i++)
    {
        printf("l=%0.2f u=%0.2f v=%0.2f, ", h_C[i].l, h_C[i].u, h_C[i].v);
    }
    printf("\n");
    // Free device memory
    CUDA_SAFE_CALL(cudaFree(d_A));

    CUDA_SAFE_CALL(cudaFree(d_B));
    CUDA_SAFE_CALL(cudaFree(d_C));

    free(h_A);
    free(h_B);
    free(h_C);

    // ----------------------------------------------------------


    CUDA_SAFE_CALL(cudaGetLastError());

    CUDA_SAFE_CALL(cudaFree(tempSqDataArray));

    CUDA_SAFE_CALL(cudaMemcpy(dest, resCuda, w * sizeof (LuvPoint) * h, cudaMemcpyDeviceToHost));


    for (int i = 0; i < w * h; i++)
    {
        resultArray[i * 3 + 0] = dest[i].l;
        resultArray[i * 3 + 1] = dest[i].u;
        resultArray[i * 3 + 2] = dest[i].v;
    }

    free(dest);
    free(src);
    CUDA_SAFE_CALL(cudaFree(srcCuda));
    CUDA_SAFE_CALL(cudaFree(resCuda));



}
// </editor-fold>



JNIEXPORT void JNICALL Java_ru_nickl_meanshift_direct_cuda_NativeCudaMSFilter_doNativefilter
(JNIEnv *env, jobject obj, jdoubleArray resultJarray, jdoubleArray srcJarray, jint h, jint w, jshort st, jdouble cs, jint mode)
{
    jdouble* srcarray = (env)->GetDoubleArrayElements(srcJarray, NULL);

    //(*env)->SetDoubleArrayRegion(env,resultJarray,0,h*w*3,srcarray);

   jdouble* resultarray = (env)->GetDoubleArrayElements(resultJarray, NULL);


    //processstack(srcarray, resultarray, h, w, st, cs);

   switch(mode){
   case 0: processsplit(srcarray, resultarray, h, w, st, cs); break;
   case 1: processstack(srcarray, resultarray, h, w, st, cs); break;
   }


    (env)->ReleaseDoubleArrayElements(resultJarray, resultarray, 0);
    (env)->ReleaseDoubleArrayElements(srcJarray, srcarray, 0);

}



// Host code
