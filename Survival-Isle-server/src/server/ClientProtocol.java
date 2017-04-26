package server;

public enum ClientProtocol {

	ToPlayer,
	MoveUp,
	MoveLeft,
	MoveDown,
	MoveRight, 
	SelectTool,
	SendClose,
	AckClose,
	ActivateTool,
	DeactivateTool,
	SendChatMessage;
}
