package beans;

import java.io.File;

public class RecordImage extends Record {
	private static final long serialVersionUID = 1L;
	private String imageName;
	private File image;

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
