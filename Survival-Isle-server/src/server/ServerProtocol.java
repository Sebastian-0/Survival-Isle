package server;

public enum ServerProtocol {
	SendWorld,
	SetPlayer,
	CreateObjects,
	SendObjects, 
	DestroyObject,
	CreateEffect,
	SetInventory, 
	TimeEvent,
	SendWorldWallTiles,
	FailedToConnect,
	PlaySound,
	SendClose,
	AckClose,
	SendChatMessage, 
	SendDebug;
}
