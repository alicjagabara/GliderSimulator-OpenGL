package engine.text;

import java.io.File;

public class FontType {

	private int textureAtlas;
	private TextMeshCreator loader;

	public FontType(int textureAtlas, File fontFile) {
		this.textureAtlas = textureAtlas;
		this.loader = new TextMeshCreator(fontFile);
	}

	public int getTextureAtlas() {
		return textureAtlas;
	}

	public TextMeshData loadText(GUIText text) {
		return loader.createTextMesh(text);
	}

}
