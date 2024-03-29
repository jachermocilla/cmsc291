package com.jachsoft.imagelib.algorithms;

import com.jachsoft.imagelib.RGBColor;
import com.jachsoft.imagelib.RGBImage;

public class DynamicCompression extends ImageOperator{

	public DynamicCompression() {
		super();
		// TODO Auto-generated constructor stub
	}

	int c;
	
	public DynamicCompression(RGBImage source){
		super(source);
	}
	
	public void setParameter(int c){
		this.c=c;
	}
	
	public RGBImage apply(){
		int ulx=region.getUlx();
		int uly=region.getUly();
		int w=region.getWidth();
		int h=region.getHeight();
		
		w = ulx+w;
		h = uly+h;
		
		RGBImage retval;
		retval=source;
				
		for (int y=uly; y < h;y++){
			for (int x=ulx; x < w; x++){
				RGBColor col=source.getRGBColor(x, y);
				float r=(float)(c*Math.log(1+col.getRedN()));			
				float g=(float)(c*Math.log(1+col.getGreenN()));
				float b=(float)(c*Math.log(1+col.getBlueN()));
				retval.setRGB(x, y, r, g, b);
			}
		}
		return retval;
	}	
}
