package application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * This program is to analyze a picture with birds
 * @author Ashraf Mustafa
 */
 
 /*
 * 
 * These are all the variables needed for the program
 */

public class Controller {

	@FXML WritableImage BWImage;
	@FXML int imageDim;
	@FXML ImageView imageView;
	int pixel [];
	int root [];


	private ArrayList<Text> birdList;
	private Rectangle rec;
	@FXML WritableImage grayImage;
	private Set<Integer> s;
	private Set<Integer> s2;
	Text t;


	private Image image;
	private File file ;

	 
	@FXML Label numberOfRealBirds;
	@FXML Label numberOfFakeBirds;
	@FXML Pane pane;
	


	FileChooser fileChooser;
	Stage stage;


	/**
	 * This function is to upload an image from a file
	 */
	
	public void upload() {
		fileChooser = new FileChooser();
		FileChooser.ExtensionFilter ALLIMG = new FileChooser.ExtensionFilter("All Image Files", "*.jpg","*.jpeg","*.png");
		fileChooser.getExtensionFilters().add(ALLIMG);
		fileChooser.setTitle("Open Image File");
		file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			image = new Image(file.toURI().toString(),imageView.getFitWidth(),imageView.getFitHeight(),true,false);
			imageDim = (int) (image.getWidth()*image.getHeight());
			pixel=  new int [imageDim];
			for(int id=0;id<pixel.length;id++) 
			{
				pixel[id]=id;
				System.out.println(pixel[id] + " ");

			}
			
			imageView.setImage(image);		}

	}


	/**
	 * This function is to convert the colored image in to black and white image.
	 */
	
	public void convertBlacnWhite() {
		PixelReader pixelReader = image.getPixelReader();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		BWImage = new WritableImage(width,height);
		PixelWriter pixelWriter = BWImage.getPixelWriter();


		for(int y = 0;y<height;y++) {
			for(int x = 0;x<width;x++) {
				int pixel = pixelReader.getArgb(x, y);
				int red = ((pixel >> 16) & 0xff);
				int green = ((pixel >> 8) & 0xff);
				int blue = (pixel & 0xff);
				int grayLevel = (red + green + blue) /3;
				grayLevel = grayLevel<108 ? 0 : 255;
				int gray = 0xFF000000 | (grayLevel << 16) | (grayLevel << 8) | grayLevel;
				pixelWriter.setArgb(x, y, gray);
			}
		}
		imageView.setImage(BWImage);
	}

	
	
	/**
	 * This function is give the white pixel a value of -1 in the console and keep the others on their real values.
	 */

	public void pixelStorage(Image BWImage) {
		PixelReader pixelReader = BWImage.getPixelReader();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int id =0;

		for(int y=0;y<height;y++) {
			for(int x = 0;x<width;x++) {
				if(((pixelReader.getArgb(x, y)) & 0xFFFFFF) == 0xFFFFFF)
					pixel[id]=-1;
				System.out.print(pixel[id] + " ");

				id++;
			}
			System.out.println();
		}
	}
	
	
	/**
	 * This function is to give the details of the image as a pop-up alert.
	 */

	public void details(){

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Image Information");
		alert.setHeaderText(null);
		alert.setContentText("Name:" + file.getName() + "\nSize: " + file.length() + "\nDimensions:" + image.getWidth() + "x" + image.getHeight() + "\nPath : " + file.getAbsolutePath());
		alert.showAndWait();
	}


	public void recognisePic() {
		pixelStorage(BWImage);
	}

	/**
	 * This function recognizes the birds on the image inaccurately without correcting the noise.
	 */

	public void inaccBirdsrec () {

		pixelStorage(BWImage);
		rootHolderStorage();
		unionPixel(BWImage);
		getNumberofRoot();
		BirdsOrNoBirds();
		birdsNumbering();
		rectangles();

	}
	

	/**
	 * 
	 * this function is to put the roots to the hashSet and removes any duplicates.
	 * 
	 */
	
	public void	birdsNumbering() {
		numbering();
		int bird = s.size();
		numberOfFakeBirds.setText(String.valueOf(bird));
		System.out.println( "number of birds : " + bird);
	}
	
	
	/**
	 * 
	 * this function counts all the roots or black pixels that have been set in union.
	 * 
	 */

	public void	rebirdsNumbering() {
		numbering();
		int bird = s2.size();
		numberOfRealBirds.setText(String.valueOf(bird));
		System.out.println( "The umber of birds is : " + bird);
	}


	/**
	 * 
	 * this function take a copy of pixels from pixel holder and add it to the root holder .
	 * 
	 */

	public void rootHolderStorage() {
		root = new int [imageDim];
		for(int i=0;i<root.length;i++) {
			root[i] = pixel[i];
			if(i>0 && i%image.getWidth()==0) System.out.println();
			System.out.print(find(i)+" ");

		}

	}

	/**
	 * 
	 * this function displays the root holders in the console .
	 * 
	 */
	
	public void rootHolderInConsol() {
		for(int i=0;i<pixel.length;i++) {
			if(i>0 && i%image.getWidth()==0) 
				System.out.println();

		}
	}

	/**
	 * 
	 * this function that checks if the pixels are close to each other.
	 * 
	 */

	public void unionPixel(Image grayimage) {
		
		int width = (int) image.getWidth();
		int height =  (int) image.getHeight();


		for (int y = 0;y<height;y++) {
			for(int x = 0;x<width;x++) {
				int id= (y*width+x);

				if(find(id)!=-1) {

					int id2=(y*width+x+1);
					int id3=((y+1)*width+x);

					if(x < width -1 && find(id2)!=-1) {
						unite(id,id2);
					}

					if(y < height -1 && find(id3)!=-1) {
						unite(id,id3);
					}

				}
			}

		}

		for(int i = 0;i<root.length;i++) {
			if(i%width==0) { 
			}
		}

	}
	

	public int root(int i) {
		while (i != root[i]) {
			if(root[i] != -1) {
				root[i] = root[root[i]]; 
				i = root[i];
			}
			else return -1;
		}

		return i;
	}



	public int find(int x) {
		return root(x);
	}

	/**
	 * 
	 * this is the function that unites two roots.
	 * 
	 */

	public void unite(int x, int z) {
		int i = root(x);
		int j = root(z);
		root[j] = root[i];

	}
	
	/**
	 * 
	 * This is the function that adds the available roots and takes one root if they are duplicate to add to the hashSet
	 * 
	 */


	public int getNumberofRoot() {
		s = new HashSet<Integer>();
		for(int i = 0;i<imageDim;i++){
			if(root[i] != -1) {
				s.add(root(i));
			}
		}

		System.out.println("");
		for(int i= 0;i<root.length;i++) {

		}
		System.out.println("The number of birds is : " + s.size());
		System.out.println("********" + s + "*********");
		return s.size();

	}


	/**
	 * 
	 * this is the function that draws the rectangles on the roots in the hashSets (Birds).
	 * 
	 */
	
	public void rectangles() {
		for(int k : s) {
			double top = image.getHeight();
			double left = image.getWidth();
			double bottom = 0;
			double right = 0;

			for(int i = 0;i<root.length;i++) {
				double y = i/(int)image.getWidth();
				double x =  i%(int)image.getWidth();

				if(find(i)==k) {
					if(x<left) left=x;
					if(x>right) right=x;
					if(y<top) top=y;
					if(y>bottom) bottom=y;

				}
			}

			rec = new Rectangle(left,top,right-left,bottom-top);
			k++;
			System.out.println("XX Bird Id: "+k+", Bounds: "+left+", "+top+", "+bottom+", "+right + ", area " + (right-left)*(bottom-top));
			rec.setFill(Color.TRANSPARENT);
			rec.setStroke(Color.RED);
			pane.getChildren().addAll(rec);
			imageView.setImage(BWImage);

		}
	}
	
	/**
	 * 
	 * this is the function is to identify the bird by comparing their area to 100, if so, then they are birds, otherwise it ignores them.
	 * 
	 */


	public void BirdsOrNoBirds() {
		double area;
		s2 = new HashSet<Integer>();

		for(int k : s) {
			double top = image.getHeight();
			double left = image.getWidth();
			double bottom = 0;
			double right = 0;

			for(int i = 0;i<root.length;i++) {
				double y = i/(int)image.getWidth();
				double x =  i%(int)image.getWidth();

				if(find(i)==k) {
					if(x<left) left=x;
					if(x>right) right=x;
					if(y<top) top=y;
					if(y>bottom) bottom=y;


				}
			}

			area = (right-left)*(bottom-top);
			if(area>=100) {
				rec = new Rectangle(left,top,right-left,bottom-top);
				k++;
				System.out.println("XX Bird Id: "+k+", Bounds: "+left+", "+top+", "+bottom+", "+right + ", area " + (right-left)*(bottom-top));
				rec.setFill(Color.TRANSPARENT);
				rec.setStroke(Color.RED);
				pane.getChildren().addAll(rec);
				imageView.setImage(BWImage);
				s2.add(k);
			}

		}
		System.out.println("number of birds "+ s2.size());
	}

	
	
	/**
	 * 
	 * this is the function that counts the birds and give the accurate number.
	 * 
	 */
	
	public void accurateBirds() {		
		double area;
		int counter=1;
		s2 = new HashSet<Integer>();

		for(int k : s) {
			double top = image.getHeight();
			double left = image.getWidth();
			double bottom = 0;
			double right = 0;

			for(int i = 0;i<root.length;i++) {
				double y = i/(int)image.getWidth();
				double x =  i%(int)image.getWidth();

				if(find(i)==k) {
					if(x<left) left=x;
					if(x>right) right=x;
					if(y<top) top=y;
					if(y>bottom) bottom=y;


				}
			}

			area = (right-left)*(bottom-top);
			if(area>=100) {
				t = new Text(right + 2,top,String.valueOf(counter));
				counter++;
				pane.getChildren().add(t);
				s2.add(k);

			}
		}

		rebirdsNumbering();


	}

	
	/**
	 * This function is to calculate the inaccurate number of birds.
	 */
	
	public void numbering() {		
		int counter = 1;

		for(int num : s) {
			double top = image.getHeight();
			double left = image.getWidth();
			double bottom = 0;
			double right = 0;

			for(int i = 0;i<root.length;i++) {
				double y = i/(int)image.getWidth();
				double x =  i%(int)image.getWidth();

				if(find(i)==num) {
					if(x<left) left=x;
					if(x>right) right=x;
					if(y<top) top=y;
					if(y>bottom) bottom=y;

				}
			}
			t = new Text(right + 2,top,String.valueOf(counter));
			counter++;
			birdList = new ArrayList<Text>();
			birdList.add(t);
			pane.getChildren().add(t);

		}
	}
	
	
	/**
	 * This function is to return the image to its original colors.
	 */
	
	public void original() {
		imageView.setImage(image);
	}
	
	
	/**
	 * This function is to clear the rectangles around the birds.
	 */
	
	public void clearRectangle() {
		pane.getChildren().remove(1, s.size()+1);
		pane.getChildren().remove(1,birdList.size());

	}

	


}






