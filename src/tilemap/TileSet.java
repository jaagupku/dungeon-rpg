package tilemap;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

public class TileSet {
	int tileWidth, tileHeight, tileCount, columns, firstGid;
	Image[] tileSheet;

	public TileSet(Node n) {
		super();
		Element e = (Element) n;
		tileWidth = Integer.parseInt(e.getAttribute("tilewidth"));
		tileHeight = Integer.parseInt(e.getAttribute("tileheight"));
		firstGid = Integer.parseInt(e.getAttribute("firstgid"));
		tileCount = Integer.parseInt(e.getAttribute("tilecount"));
		columns = Integer.parseInt(e.getAttribute("columns"));
		tileSheet = new Image[tileCount];
		NodeList childNodes = n.getChildNodes();
		Element imageData = (Element) childNodes.item(1);
		String sheetPath = imageData.getAttribute("source").replace("../", "");
		tileSheet = loadImagesFromTilesheet(sheetPath, tileCount, columns, Game.tileSize, Game.scale)
				.toArray(tileSheet);
		setUpAnimatedTiles(childNodes);
		
	}
	
	/**
	 * Reads in animation details and starts playing it.
	 * @param childNodes - tileset childnodes.
	 */
	private void setUpAnimatedTiles(NodeList childNodes){
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (!childNodes.item(i).getNodeName().equals("tile"))
				continue;
			int id = Integer.parseInt(((Element) childNodes.item(i)).getAttribute("id"));
			NodeList tileChilds = childNodes.item(i).getChildNodes();
			for (int j = 0; j < tileChilds.getLength(); j++) {
				if (!tileChilds.item(j).getNodeName().equals("animation"))
					continue;
				NodeList frames = tileChilds.item(j).getChildNodes();
				Animation animation = new Animation(id);
				for (int frame = 0; frame < frames.getLength(); frame++) {
					if (!frames.item(frame).getNodeName().equals("frame"))
						continue;
					Element eFrame = ((Element) frames.item(frame));
					int tileid = Integer.parseInt(eFrame.getAttribute("tileid"));
					int duration = Integer.parseInt(eFrame.getAttribute("duration"));
					animation.addFrame(tileid, duration);
				}
				animation.play();
			}
		}
	}

	/**
	 * Gets image
	 * @param gid - Global id
	 * @return image
	 */
	public Image get(int gid) {
		return tileSheet[gid - firstGid];
	}

	public int getFirstGid() {
		return firstGid;
	}

	/**
	 * Loads and rescales images from tilesheet at specified path and returns
	 * images as a list.
	 * 
	 * @param path
	 *            - Path as a string for the tilesheet
	 * @param tileCount
	 *            - number of tiles in the sheet
	 * @param columns
	 *            - number of columns in a tilesheet
	 * @param tileSize
	 *            - tile size, tile width = tile height
	 * @param scale
	 *            - scale factor
	 * @return list of rescaled images.
	 */
	public static List<Image> loadImagesFromTilesheet(String path, int tileCount, int columns, int tileSize,
			double scale) {
		List<Image> sheet = new ArrayList<Image>();

		Image sheetImgNotResized = new Image(path);
		Image sheetImg = new Image(path, Math.round(scale * sheetImgNotResized.getWidth()),
				Math.round(scale * sheetImgNotResized.getHeight()), true, false);

		for (int tCount = 0; tCount < tileCount; tCount++) {
			int x = (int) ((tCount % columns) * tileSize * scale);
			int y = (int) ((tCount / columns) * tileSize * scale);
			int width = (int) (tileSize * Game.scale);
			int height = (int) (tileSize * Game.scale);

			WritableImage wi = new WritableImage(sheetImg.getPixelReader(), x, y, width, height);
			sheet.add((Image) wi);
		}

		return sheet;
	}

	/**
	 * Animation class holds animations frame id's and durations.
	 *
	 */
	private class Animation {

		private Image thisImg;
		private int tileid;
		private List<Timeline> frames;

		public Animation(int tileid) {
			this.tileid = tileid;
			frames = new ArrayList<Timeline>();
			thisImg = tileSheet[tileid];
		}

		private int getNextFrame(int current) {
			return (current < frames.size()-1 ? current+1 : 0);
		}

		public void addFrame(int tileid, int duration) {
			int current = frames.size();
			Timeline tl = new Timeline(new KeyFrame(Duration.millis(duration)));
			tl.setAutoReverse(false);
			tl.setCycleCount(1);
			tl.setOnFinished(ea -> {
				tileSheet[this.tileid] = (current == 0 ? thisImg : tileSheet[tileid]);
				frames.get(getNextFrame(current)).play();
			});
			frames.add(tl);
		}
		
		public void play(){
			frames.get(0).play();
		}
		
		public void stop(){
			for(Timeline tl : frames){
				tl.stop();
			}
		}
	}
}
