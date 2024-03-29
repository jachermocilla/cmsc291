/*
 * @(#)ThreeD.java	1.18 06/02/22
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)ThreeD.java	1.18 06/02/22
 */

/* A set of classes to parse, represent and display 3D wireframe models
   represented in Wavefront .obj format. */

package com.jachsoft.threed;

import java.awt.Graphics;
import java.awt.Color;


/** The representation of a 3D model */
class Model3D {
    float vert[];
    int tvert[];
    int nvert, maxvert;
    int con[];
    int ncon, maxcon;
    boolean transformed;
    Matrix3D mat;

    float xmin, xmax, ymin, ymax, zmin, zmax;

    Model3D () {
    	mat = new Matrix3D ();
    	mat.xrot(20);
    	mat.yrot(30);
    }
    

    /** Add a vertex to this model */
    int addVert(float x, float y, float z) {
    	int i = nvert;
    	if (i >= maxvert)
    		if (vert == null) {
    			maxvert = 100;
    			vert = new float[maxvert * 3];
    		} else {
    			maxvert *= 2;
    			float nv[] = new float[maxvert * 3];
    			System.arraycopy(vert, 0, nv, 0, vert.length);
    			vert = nv;
    		}
    	i *= 3;
    	vert[i] = x;
    	vert[i + 1] = y;
    	vert[i + 2] = z;
    	return nvert++;
    }
    
    /** Add a line from vertex p1 to vertex p2 */
    void add(int p1, int p2) {
    	int i = ncon;
    	if (p1 >= nvert || p2 >= nvert)
    		return;
    	if (i >= maxcon)
    		if (con == null) {
    			maxcon = 100;
    			con = new int[maxcon];
    		} else {
    			maxcon *= 2;
    			int nv[] = new int[maxcon];
    			System.arraycopy(con, 0, nv, 0, con.length);
    			con = nv;
    		}
    	if (p1 > p2) {
    		int t = p1;
    		p1 = p2;
    		p2 = t;
    	}
    	con[i] = (p1 << 16) | p2;
    	ncon = i + 1;
    }
    /** Transform all the points in this model */
    void transform() {
    	if (transformed || nvert <= 0)
    		return;
    	if (tvert == null || tvert.length < nvert * 3)
    		tvert = new int[nvert*3];
    	mat.transform(vert, tvert, nvert);
    	transformed = true;
    }

   /* Quick Sort implementation
    */
   private void quickSort(int a[], int left, int right)
   {
      int leftIndex = left;
      int rightIndex = right;
      int partionElement;
      if ( right > left)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         partionElement = a[ ( left + right ) / 2 ];

         // loop through the array until indices cross
         while( leftIndex <= rightIndex )
         {
            /* find the first element that is greater than or equal to
             * the partionElement starting from the leftIndex.
             */
            while( ( leftIndex < right ) && ( a[leftIndex] < partionElement ) )
               ++leftIndex;

            /* find an element that is smaller than or equal to
             * the partionElement starting from the rightIndex.
             */
            while( ( rightIndex > left ) &&
                   ( a[rightIndex] > partionElement ) )
               --rightIndex;

            // if the indexes have not crossed, swap
            if( leftIndex <= rightIndex )
            {
               swap(a, leftIndex, rightIndex);
               ++leftIndex;
               --rightIndex;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( left < rightIndex )
            quickSort( a, left, rightIndex );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( leftIndex < right )
            quickSort( a, leftIndex, right );

      }
   }

   private void swap(int a[], int i, int j)
   {
      int T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;
   }


    /** eliminate duplicate lines */
    void compress() {
    	int limit = ncon;
    	int c[] = con;
    	quickSort(con, 0, ncon - 1);
    	int d = 0;
    	int pp1 = -1;
    	for (int i = 0; i < limit; i++) {
    		int p1 = c[i];
    		if (pp1 != p1) {
    			c[d] = p1;
    			d++;
    		}
    		pp1 = p1;
    	}
    	ncon = d;
    }


    /** Paint this model to a graphics context.  It uses the matrix associated
	with this model to map from model space to screen space.
	The next version of the browser should have double buffering,
	which will make this *much* nicer */
    void paint(Graphics g) {
	if (vert == null || nvert <= 0)
	    return;
	transform();
	
	int lim = ncon;
	int c[] = con;
	int v[] = tvert;
		
	if (lim <= 0 || nvert <= 0)
	    return;
	
	g.setColor(Color.RED);
    g.drawLine(v[0], v[1],
 	       v[3], v[4]);
    g.drawString("Red (X)",v[3],v[4]);
    
    g.setColor(Color.GREEN);
    g.drawLine(v[0], v[1],
 	       v[6], v[7]);
    g.drawString("Green(Y)",v[6],v[7]);
    
    g.setColor(Color.BLUE);
    g.drawLine(v[0], v[1],
 	      v[9], v[10]);
    g.drawString("Blue (Z)",v[9],v[10]);
    	
	for(int i=0;i<nvert;i+=3){
		g.setColor(new Color((int)vert[i],(int)vert[i+1],(int)vert[i+2]));
		g.drawLine(v[i], v[i + 1],
	 		       v[i], v[i + 1]);
	}
	
    }

    /** Find the bounding box of this model */
    void findBB() {
    	if (nvert <= 0)
    		return;
    	float v[] = vert;
    	float xmin = v[0], xmax = xmin;
    	float ymin = v[1], ymax = ymin;
    	float zmin = v[2], zmax = zmin;
    	for (int i = nvert * 3; (i -= 3) > 0;) {
    		float x = v[i];
    		if (x < xmin)
    			xmin = x;
    		if (x > xmax)
    			xmax = x;
    		float y = v[i + 1];
    		if (y < ymin)
    			ymin = y;
    		if (y > ymax)
    			ymax = y;
    		float z = v[i + 2];
    		if (z < zmin)
    			zmin = z;
    		if (z > zmax)
    			zmax = z;
    	}
    	this.xmax = xmax;
    	this.xmin = xmin;
    	this.ymax = ymax;
    	this.ymin = ymin;
    	this.zmax = zmax;
    	this.zmin = zmin;
    }
}