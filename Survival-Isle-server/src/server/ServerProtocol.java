package server;

public enum ServerProtocol {
	SEND_WORLD,
	SET_PLAYER,
	CREATE_OBJECTS,
	SEND_OBJECTS, 
	DESTROY_OBJECTS,
	FailedToConnect;
}
