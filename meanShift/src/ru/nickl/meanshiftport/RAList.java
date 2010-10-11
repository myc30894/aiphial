/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanshiftport;

/**
 *
 * @author nickl
 */
public class RAList {
    
   

	//============================
	// *** Public Data Members ***
	//============================

	////////////RAM Label//////////
	 public int		label;

	////////////RAM Weight/////////
	 public float	edgeStrength;
	 public int		edgePixelCount;

	////////////RAM Link///////////
	 public RAList	next;

	//=======================
	// *** Public Methods ***
	//=======================

	/*/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\*/
	/* Class Constructor and Destructor */
	/*\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/*/


/*******************************************************/
/*Class Constructor                                    */
/*******************************************************/
/*Constructs a RAList object.                          */
/*******************************************************/
/*Post:                                                */
/*      - a RAlist object has been properly constru-   */
/*        cted.                                        */
/*******************************************************/

    public RAList( )
{
	//initialize label and link
	label			= -1;
	next			= null;

	//initialize edge strenght weight and count
	edgeStrength	= 0;
	edgePixelCount	= 0;
}

/*******************************************************/
/*Class Destructor                                     */
/*******************************************************/
/*Destructrs a RAList object.                          */
/*******************************************************/
/*Post:                                                */
/*      - the RAList object has been properly dest-    */
/*        ructed.                                      */
/*******************************************************/

    public void destructor()
{
	//do nothing
}

/*******************************************************/
/*Insert                                               */
/*******************************************************/
/*Insert a region node into the region adjacency list. */
/*******************************************************/
/*Pre:                                                 */
/*      - entry is a node representing a connected re- */
/*        gion                                         */
/*Post:                                                */
/*      - entry has been inserted into the region adj- */
/*        acency list if it does not already exist     */
/*        there.                                       */
/*      - if the entry already exists in the region    */
/*        adjacency list 1 is returned otherwise 0 is  */
/*        returned.                                    */
/*******************************************************/

int Insert(RAList entry)
{

	//if the list contains only one element
	//then insert this element into next
	if(null==next)
	{
		//insert entry
		next		= entry;
		entry.next = null;

		//done
		return 0;
	}

	//traverse the list until either:

	//(a) entry's label already exists - do nothing
	//(b) the list ends or the current label is
	//    greater than entry's label, thus insert the entry
	//    at this location

	//check first entry
	if(next.label > entry.label)
	{
		//insert entry into the list at this location
		entry.next	= next;
		next		= entry;

		//done
		return 0;
	}

	//check the rest of the list...
	exists	= 0;
	cur		= next;
	while(cur!=null)
	{
		if(entry.label == cur.label)
		{
			//node already exists
			exists = 1;
			break;
		}
		else if(((cur.next==null))||(cur.next.label > entry.label))
		{
			//insert entry into the list at this location
			entry.next	= cur.next;
			cur.next	= entry;
			break;
		}

		//traverse the region adjacency list
		cur = cur.next;
	}

	//done. Return exists indicating whether or not a new node was
	//      actually inserted into the region adjacency list.
	return (int)(exists);

}





	//=============================
	// *** Private Data Members ***
	//=============================

	///////current and previous pointer/////
	private RAList	cur, prev;

	////////flag///////////
	private byte exists;





}
