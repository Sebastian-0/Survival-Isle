package server;

public enum ServerProtocol {
	SendWorld,
	SetPlayer,
	CreateObjects,
	SendObjects, 
	DestroyObject,
	CreateEffect,
	SetInventory,
	SendWorldWallTiles,
	FailedToConnect,
	PlaySound;
}
