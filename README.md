# Dungeon the RPG
This is a project for "Object-oriented Programming" course.

## Guide to create rooms with Tiled

All rooms are created with [Tiled](http://www.mapeditor.org/ "Tiled Map Editor download") map editor.

### Let's get started

First let's create new map. `File -> New...`
![New Map - Tiled](http://i.imgur.com/gMGdWtx.png "New Map - Tiled")
* Orientation: Orthogonal
* Tile layer format: CSV
* Tile render order: Right Down
* Map size: choose it yourself
* Tile size
..* Width: 48px
..* Height: 48px
`Ok`
Then let's set Tile layer format.
`Map -> Map Properties`
Make sure that `Tile Layer Format is CSV`
![Tile Layer Format](http://i.imgur.com/NxuTfSq.png "Tile Layer Format is CSV")

### Layers
Change "Tile Layer 1" to "background". This will be background.
![Change "Tile Layer 1" to "background"](http://i.imgur.com/8cnz6uS.png "Change "Tile Layer 1" to "background"")
Add 2 more object layers. `Layer -> Add Object Layer`. Name them "monsters" and "map_data". Monser layer will hold monsters and map_data holds connections between rooms and player spawn point.
![3 layers](http://i.imgur.com/w0rMh8T.png "All 3 layers")

### Adding Tilesets
Let's now add tilesets. `Map -> New Tileset...`
![New Tileset](http://i.imgur.com/q4MadH9.png "Map -> New Tileset...")
For background tiles e.g. walls, ground, we want tileset name to be "background"
![Name: background](http://i.imgur.com/JFcRT4K.png "Name: background")
Do the same for "objects_sheet.png" and "markers.png"
![Tilesets](http://i.imgur.com/iHbTloO.png "All tilesets")

### Making the map

#### Creating the background
Let's get started. First make sure you have selected "background" layer, then select tile from "background" tilesheet. And then start painting the map.
![example map](http://i.imgur.com/1vAKsfT.png "Example map")

#### Adding Monsters
Make sure you have enabled objects snapping to grid. `View -> Snap to Grid`
Select "monsters" layer and choose tile from "objects_sheet" tilesheet.
Choose "Insert tile (T)" tool from toolbar.
![Insert tile](http://i.imgur.com/YbTkjjy.png "Insert tile (T)")
With "Select objects (S)" tool you can move and edit objects.
![Insert tile](http://i.imgur.com/r5ssod9.png "Example map with monsters")

#### Adding Map Data

When adding map data, make sure you have selected "map_data" layer.
This is player spawn.
![Player spawn](http://i.imgur.com/g5bC0P6.png "Put it only in room0")
NB! Put it only in room0.tmx

This is connection.
![Connection](http://i.imgur.com/An7y0AK.png "Connection")
Connections are in pairs. Each pair must be unique. See following images to see connecions in example rooms.
![Naming connection](http://i.imgur.com/VmOcef9.png "Naming connection")

#### Saving the room.
`File -> Save As...` Naming rooms starts from 0. Room0 will be the room where player starts the game. After that numbering rooms is irrelevant. Only requirement is that there aren't any gaps in numbering.
![Saving](http://i.imgur.com/0nGI6sL.png "Saving the room.")
Finished room0.tmx
![Finished room0.tmx](http://i.imgur.com/5ZCEh5G.png "Finished room0.tmx")

#### Creating more rooms.

To create more rooms follow previous steps. New rooms do not need "player spawn" in the "map_data".
Example room1.tmx, which is connected to room0.tmx
![Example room1.tmx](http://i.imgur.com/2wTYZTw.png "Example room1.tmx")



