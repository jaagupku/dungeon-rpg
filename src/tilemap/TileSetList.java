package tilemap;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import game.Game;
import javafx.scene.image.Image;

public class TileSetList {
	
	private List<TileSet> tileSets; 
	private List<Integer> firstGids;
	
	
	public TileSetList(List<Node> sheets){
		tileSets = new ArrayList<TileSet>();
		firstGids = new ArrayList<Integer>();
		for (Node n : sheets) {
			Element e = (Element) n;
			if (e.getAttribute("name").equals("markers"))
				continue;
			TileSet ts = new TileSet(n);
			tileSets.add(ts);
			firstGids.add(ts.getFirstGid());
		}
	}
	
	/**
	 * Gets image.
	 * @param gid - global image id
	 * @return image
	 */
	public Image get(int gid){
		for(int i=0; i<firstGids.size(); i++){
			if(firstGids.get(i) > gid)
				continue;
			return tileSets.get(i).get(gid);
		}
		return new Image("missing.png", Game.tileSize*Game.scale, Game.tileSize*Game.scale, true, false);
	}
}
