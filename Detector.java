import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;

/**A class that is used to create a Detector
 * 
 * @author Carlos Haddock, CS310, Prof. Russel Section-002
 *
 * @param <T> A generic type T
 */
public class Detector extends JPanel {
	
	/** gets the difference between two colors
	 * 
	 * @param c1 the first color
	 * @param c2 the second color
	 * @return the difference between the two colors
	 */
	public static int getDifference(Color c1, Color c2) {

		
	    double answer = (Math.pow((c1.getRed()-c2.getRed()), 2) + Math.pow((c1.getGreen()-c2.getGreen()), 2) + Math.pow((c1.getBlue() - c2.getBlue()), 2));
	
		answer = ((answer + 0) / 195075)*100;		
		int diff = (int) answer;
		return diff;
	}
	

	/** thresh the given image to be black and white, depending on it's relation to color c, aslong as it's within a given okDist
	 * 
	 * @param image given image
	 * @param c given color to be threshed
	 * @param okDist an ok Distance from the color c
	 */
	public static void thresh(BufferedImage image, Color c, int okDist) {
		int width = image.getWidth();
		int height = image.getHeight();
		Color black = new Color(0,0,0); 
		Color white = new Color(255,255,255); 

		for (int y = 0 ; y < height; y++) {
			for (int x = 0 ; x < width;  x ++) { 
				Color pxlclr = new Color(image.getRGB(x, y));
				if(getDifference(pxlclr, c) > okDist) {
					image.setRGB(x, y, white.getRGB());
				}
				else {
					image.setRGB(x, y, black.getRGB());
				}
			}
		}
	}
	
	/**Returns the neighbor sets of the curr pixelID
	 * 
	 * @param image given image
	 * @param ds given disjoint set
	 * @param pixelId the current pixelId 
	 * @return the neighbor sets within a pair of integers
	 */
	public static Pair<Integer,Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId) {

		
		Pixel curr = getPixel(image, pixelId);
		int X = curr.a -1; //.a is x
		int Y = curr.b -1; //.b is y

		Integer pixelIdLeft = null;
		Integer pixelIdUp = null;
		Integer upRoot = null;
		Integer leftRoot = null;
		if(X >= 0) {
			pixelIdLeft = getId( image, X, curr.b);
			leftRoot = ds.find(pixelIdLeft);
		}
		
		if(Y >= 0) {

			pixelIdUp = getId( image, curr.a, Y);
			upRoot = ds.find(pixelIdUp);
		}


		Pair<Integer, Integer> ans = new Pair<Integer, Integer>(leftRoot,upRoot);
		return ans; 
	}

	/** Detect the spots that were left over from the thresh
	 */
	public void detect() {

		thresh(img,blobColor,okDist);
		int width = img.getWidth();
		int height = img.getHeight();
		int resolution = width * height;
		ArrayList<Pixel> arr = new ArrayList<Pixel>();
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				arr.add(new Pixel(x,y));
			}
		}
		

		ds = new DisjointSets<Pixel>(arr);
		
		
		for (int y = 0 ; y < height;  y ++) {
			for (int x = 0 ; x < width; x++) {
				int currID = getId(img, x, y);
				Pair<Integer, Integer> neighbors = getNeighborSets(img, ds, currID);
				int currRoot = ds.find(currID);
				Integer leftRoot = neighbors.a;
				Integer upRoot = neighbors.b;
				Pixel up =null;
				Pixel left =null;
				if(upRoot != null) {
				up = getPixel(img, upRoot);
				}
				if(leftRoot != null) {
				left = getPixel(img, leftRoot);
				}

				Pixel currPixel = getPixel(img, currID);
				Color currColor = getColor(img, currPixel);
				boolean mda = true;
				if(currColor.getRGB() == new Color(0,0,0).getRGB()) {
					if(upRoot != null) {
						Pixel upPixel = getPixel(img, upRoot);
						Color upColor = getColor(img, upPixel);
						if(currColor.getRGB() == upColor.getRGB()) {
							
							ds.union(ds.find(currID),upRoot);



						}
					}
				}
				if(leftRoot != null && ds.find(currID) != leftRoot) {
				if(currColor.getRGB() == new Color(0,0,0).getRGB()) {
					if(leftRoot !=null) {
						//System.out.println("Attempting to Union Left");
						Pixel leftPixel = getPixel(img, leftRoot);
						Color leftColor = getColor(img, leftPixel);
							if(currColor.getRGB() == leftColor.getRGB()) {
								ds.union(ds.find(currID),leftRoot);
							}	
					}
				}
				}
			}
		}
	}
	
	
	/** outPut the results to the user, paints k amount of blobs
	 * 
	 * @param outputFileName outputfile for the image
	 * @param outputECFileName output file for the extra credit image
	 * @param k paint k amount of blobs
	 */
	public void outputResults(String outputFileName, String outputECFileName, int k) {
		if(k<1) {
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k="+k));
		}
		
		ArrayList<Integer> PixSetSize = new ArrayList<Integer>();
		ArrayList<Integer> PixRootLoc = new ArrayList<Integer>();
		int wid = img.getWidth();
		int height = img.getHeight();
		int resolution = wid*height;

		for(int i = 0 ; i < resolution;i++) {

			if(ds.get(i).size() > 1) {
				
				Set<Pixel> PixSet = ds.get(i);
				PixSetSize.add(PixSet.size());

				PixRootLoc.add(i);

			}
			else if(ds.get(i).size() == 1) {

				Set<Pixel> PixSet = ds.get(i);
				Pixel P = (Pixel) PixSet.toArray()[0];
			
				if (img.getRGB(P.a, P.b) == new Color(0,0,0).getRGB()){
					PixSetSize.add(PixSet.size());
					PixRootLoc.add(i);
				}

			}
		}


		Collections.sort(PixSetSize);
		
		

		//System.out.println("Made it to the mirroring process.");
		ArrayList<Integer> mirror = new ArrayList<Integer>();
		ArrayList<Integer> mirrorPixRootLoc = new ArrayList<Integer>();
		for(int i =PixSetSize.size()-1 ; i >= 0; i--) {
			mirror.add(PixSetSize.get(i));
			

		}
		for (int i = 0 ; i < mirror.size() ; i ++) {
			for(int j = 0 ; j < PixRootLoc.size(); j++) {
				if(mirror.get(i) == ds.get(PixRootLoc.get(j)).size()) {
					mirrorPixRootLoc.add(PixRootLoc.get(j));
				}

			}
		}

		//System.out.println("Made it to the recoloring process.");
		boolean initT = true;
		for(int i = 0 ; i < k; i ++) { //loop for k blobs
			Set<Pixel> PixSet = ds.get(mirrorPixRootLoc.get(i));
			for(int j = 0 ; j < PixSet.size(); j++) {
				Pixel P = (Pixel) PixSet.toArray()[j];

				img.setRGB(P.a, P.b, getSeqColor(i, k).getRGB());
			}
			if(initT) {
			System.out.println(k +  " / " + mirror.size());
			initT = false;
			}
			System.out.println("Blob " + (i+1) + ": " + mirror.get(i) + " pixels" );
		}

		
		
		
		
		
		//System.out.println("Made it to the saving process.");
		try {
			File ouptut = new File(outputFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to "+outputFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputFileName);
		}
		
		
		/*		
		//if you're doing the EC and your output image is still this.img,
		//you can uncomment this to save this.img to the specified outputECFileName
		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to "+outputECFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputECFileName);
		}*/
		
	}

	/**Test Driver
	 * 
	 * @param args arguments for the driver
	 */
	public static void main(String[] args) {
/*		//Testing getDifference
		Color black = new Color(0,0,0);
		Color white = new Color(255,255,255);
		Color red = new Color(255, 0, 0);
		Color green = new Color (0, 255, 0);
		System.out.println("w-b: "+getDifference(white, black)); //100
		System.out.println("w-w: "+getDifference(white, white)); //0
		System.out.println("b-b: "+getDifference(black, black)); //0 
		System.out.println("r-r: "+getDifference(red, red)); //0 
		System.out.println("r-b: "+getDifference(red, black)); //33
		System.out.println("r-g: "+getDifference(red, green)); //66
		System.out.println("g-g: "+getDifference(green, green)); //0
		
		//Testing Thresh
		*//**
		int blck = black.getRGB();
		int r = red.getRGB();
		int g = green.getRGB();
		int w = white.getRGB();
		
		System.out.println(blck);
		System.out.println(r);
		System.out.println(g);
		System.out.println(w);
		*//*
		 
		//Some stuff to get you started...
		
		File imageFile = new File("../input/04_Circles.png");
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+imageFile+", error msg: "+e);
			return;
		}
		
		Pixel p = getPixel(img, 110); //100x100 pixel image, pixel id 110
		System.out.println(p.a); //x = 10
		System.out.println(p.b); //y = 1
		System.out.println(getId(img, p)); //gets the id back (110)
		System.out.println(getId(img, p.a, p.b)); //gets the id back (110)
		
		
		//TESTING MY THRESH
		File imageFileTest = new File("../input/01_TinyRGB.png");
		BufferedImage img2 = null;
		
		try {
			img2 = ImageIO.read(imageFileTest);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+imageFileTest+", error msg: "+e);
			return;
		}
		
		
		//ACTUAL PART WHERE I TEST IT
		thresh(img2, red, 15);
		ArrayList<Pixel> arr = new ArrayList<Pixel>();
		int counter = 0 ;
		boolean check = true;
		for (int x = 0; x < 10 ; x++) {
			for (int y = 0; y < 10; y++) {
				arr.add(new Pixel(x,y));
				if (counter % 50 == 0) {
					
					if(check) {
					System.out.print("Processing");
					check = false;
					}
					System.out.print(".");

					
				}
			}
		}
		System.out.println("Check me "+ arr.toArray().length);
		DisjointSets<Pixel> a = new DisjointSets<Pixel>(arr);
		System.out.println(img2.getWidth());
		System.out.println("ID " + getId(img2,9,9));
		Pair<Integer, Integer> ans = getNeighborSets(img2, a, 10);
		System.out.println("Left: "+ans.a);
		System.out.println("Up: "+ans.b);
		
		File outputfile = new File("../output/Success.png");
		try {
			ImageIO.write(img2, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
	}

	//-----------------------------------------------------------------------
	//
	// Todo: Read and provide comments, but do not change the following code
	//
	//-----------------------------------------------------------------------

	//Data
	public BufferedImage img;        //this is the 2D array of RGB pixels
	private Color blobColor;         //the color of the blob we are detecting
	private String imgFileName;      //input image file name
	private DisjointSets<Pixel> ds;  //the disjoint set
	private int okDist;              //the distance between blobColor and the pixel which "still counts" as the color


	/** a constructor that reads image from a given file.
	 * 
	 * @param imgfile given file for image
	 * @param blobColor the color of the blob we are detecting
	 * @param okDist the distance between blobColor and the pixel which "still counts" as the color
	 */
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName=imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;
		
		reloadImage();
	}


	/** reloads the image
	 * 
	 */
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);
		
		try {
			this.img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+this.imgFileName+", error msg: "+e);
			return;
		}
	}

	// JPanel function
	/** Paints the image
	 * 
	 */
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0,this);
	}

	//private classes below

	//Convenient helper class representing a pair of things
	/** Pair helper class
	 * 
	 * @author Whoever wrote this
	 *
	 * @param <A> type a
	 * @param <B> type b
	 */
	private static class Pair<A,B> {
		A a;
		B b;
		public Pair(A a, B b) {
			this.a=a;
			this.b=b;
		}
	}

	/**A pixel is a set of locations a (aka. x, distance from the left) and b (aka. y, distance from the top)
	 * 
	 * @author Whoever wrote this
	 * 
	 */
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x,y);
		}
	}

	
	/** Convert a pixel in an image to its ID
	 * 
	 * @param image given image
	 * @param p pixel p that the id will be found
	 * @return the id of the pixel
	 */
	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	/**Convex ID for an image back to a pixel
	 * 
	 * @param image given image
	 * @param id given id
	 * @return return the pixel 
	 */
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id/image.getWidth();
		int x = id-(image.getWidth()*y);

		if(y<0 || y>=image.getHeight() || x<0 || x>=image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x,y);
	}

	/**Converts a location in an image into an id
	 * 
	 * @param image given image
	 * @param x given x coordinate
	 * @param y given y coordinate
	 * @return the id of the (x,y) pair
	 */
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth()*y)+x;
	}

	/**Returns the color of a given pixel in a given image
	 * 
	 * @param image given image
	 * @param p pixel p
	 * @return the color
	 */
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}
	
	//Pass 0 -> k-1 as i to get the color for the blobs 0 -> k-1
	/** get sequential colors for i>k-1 blobs
	 * 
	 * @param i given i, usually 0
	 * @param max given maximum usually k-1
	 * @return the next color in the sequence.
	 */
	private Color getSeqColor(int i, int max) {
		if(i < 0) i = 0;
		if(i >= max) i = max-1;
		
		int r = (int)(((max-i+1)/(double)(max+1)) * blobColor.getRed());
		int g = (int)(((max-i+1)/(double)(max+1)) * blobColor.getGreen());
		int b = (int)(((max-i+1)/(double)(max+1)) * blobColor.getBlue());
		
		if(r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		}
		else if(r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}
		return new Color(r, g, b);
	}
}
