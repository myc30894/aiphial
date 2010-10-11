/*
 * there are fake definitions to make nb recognize cuda keywords
 */

#ifndef _NBCUDAWA_H
#define	_NBCUDAWA_H

#if !defined(__CUDACC__)

    // define the keywords, so that the IDE does not complain about them
#define __global__
#define __device__
#define __shared__
#define blockDim
#define blockIdx
#define threadIdx
#define dim3


#endif

#endif	/* _NBCUDAWA_H */

