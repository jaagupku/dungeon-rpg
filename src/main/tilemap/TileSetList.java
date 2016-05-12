package main.tilemap;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.image.Image;

public class TileSetList {

	private List<TileSet> tileSets;
	private Image missingTile;

	public TileSetList(List<Node> sheets, double scale, int tileSize) {
		missingTile = TileSet.loadImagesFromTilesheet("images\\missing.png", 1, 1, 48, scale).get(0);
		tileSets = new ArrayList<TileSet>();
		for (Node n : sheets) {
			Element e = (Element) n;
			if (e.getAttribute("name").equals("markers"))
				continue;
			TileSet ts = new TileSet(n, scale, tileSize);
			tileSets.add(ts);
		}
	}

	void playAnimations() {
		tileSets.forEach(ts -> ts.getAnimations().forEach(ani -> ani.play()));
	}

	void stopAnimations() {
		tileSets.forEach(ts -> ts.getAnimations().forEach(ani -> ani.stop()));
	}

	/**
	 * Gets image.
	 * 
	 * @param gid
	 *            - global image id
	 * @return image
	 */
	public final Image get(int gid) {
		for (int i = 0; i < tileSets.size(); i++) {
			if (tileSets.get(i).getFirstGid() + tileSets.get(i).getTileCount() - 1 > gid)
				return tileSets.get(i).get(gid);
		}
		return missingTile;
	}
}
