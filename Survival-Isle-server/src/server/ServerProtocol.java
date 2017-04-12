package server;

public enum ServerProtocol {
	SendWorld,
	SetPlayer,
	CreateObjects,
	SendObjects, 
	DestroyObject,
	SetInventory,
	SendWorldTiles,
	FailedToConnect;
}
