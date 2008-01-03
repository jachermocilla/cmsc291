package com.jachsoft.imagelib;

import java.awt.image.BufferedImage;

public class RGBImage{
	
    protected BufferedImage bimg;
	
    
    public RGBImage(){
    	
    }
    
    public RGBImage (BufferedImage bimg){
    	this.bimg=bimg;
    }
    
    
	public RGBImage(int w,int h){
		bimg=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	}
	
	public void setRGB(int x, int y, int r, int g, int b){
		int rgb=RGBColor.pack(r, g, b);
		bimg.setRGB(x, y, rgb);
	}

	public void setRGB(int x, int y, float r, float g, float b){
		int rgb=RGBColor.pack((int)(r*255), (int)(g*255),(int) (b*255));
		bimg.setRGB(x, y, rgb);
	}

	
	public void setRGB(int x, int y, int rgb){
		bimg.setRGB(x, y, rgb);
	}
	
	
	public RGBColor getRGBColor(int x, int y){
		RGBColor rgb = new RGBColor(bimg.getRGB(x, y));
		return rgb;
	}
	
	public BufferedImage getBufferedImage(){
		return bimg;
	}
	
	
	public GrayScaleImage getGrayScaleImage(float wr,float wg,float wb){
		int w = bimg.getWidth();
		int h = bimg.getHeight();
		
		GrayScaleImage gray = new GrayScaleImage(w,h);
				
		for (int y=0; y < h;y++){
			for (int x=0; x < w; x++){
				RGBColor col=this.getRGBColor(x, y);
				float color=((col.getBlue()*wb + col.getGreen()*wg + col.getRed()*wr)/255f);				
				gray.setColor(x, y, color);
			}
		}
		return gray;
	}
	
	public RGBImage getNegative(){
		int w = bimg.getWidth();
		int h = bimg.getHeight();
		
		RGBImage retval = this;//new RGBImage(w,h);
				
		for (int y=0; y < h;y++){
			for (int x=0; x < w; x++){
				RGBColor col=this.getRGBColor(x, y);
				int r=255-col.getRed();			
				int g=255-col.getGreen();
				int b=255-col.getBlue();
					
				retval.setRGB(x, y, r, g, b);
			}
		}
		return retval;
	}
	
	public GrayScaleImage getGrayScaleImage(){
		return getGrayScaleImage(0.333f,0.333f,0.333f);		
	}
	
	
	public void setBufferedImage(BufferedImage bimg){
		this.bimg = bimg;		
	}
		
	public int getWidth(){
		return bimg.getWidth();
	}
	
	public int getHeight(){
		return bimg.getHeight();
	}
	
	public void clear(){
		int width=bimg.getWidth();
		int height=bimg.getHeight();
		for (int y=0;y < height;y++){
			for (int x=0;x < width;x++){
				bimg.setRGB(x, y, 0xFF000000);
			}
		}
	}
	
	public Neighbor getNeighbor(int x, int y, int channel, int type) 
		throws ImageOperationException{
		Neighbor nbor=new Neighbor(type);
		int startX=0;
		int startY=0;
		int endX=bimg.getWidth();
		int endY=bimg.getHeight();
		int offset=nbor.getOffset();
		
		startX=startX+offset;
		startY=startY+offset;
		endX=endX-offset;
		endY=endY-offset;
		
		if ((x < startX) || (x > endY) || (y < startY) || (y > endY)){
			throw new ImageOperationException("Cannot obtain neighbor!");
		}
		
		int ulx = x-offset;
		int uly = y-offset;
		
		for (int i=(y-offset); i <= y+offset; i++){
			for (int j=(x-offset); j <= x+offset;j++){
				System.out.println(j+","+i+","+offset);
				RGBColor rgb = this.getRGBColor(j, i);
				nbor.setValue(j-ulx, i-uly, rgb.getChannel(channel));
			}			
		}
		return nbor;
	}
	
}
