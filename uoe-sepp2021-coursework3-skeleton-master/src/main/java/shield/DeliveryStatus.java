package shield;

public enum DeliveryStatus {
	NOTFOUND(-1),
	PLACED(0),
	PACKED(1),
	DISPATCHED(2),
	DELIVERED(3),
	CANCELLED(4);

	private int status;
	DeliveryStatus(int i) {
		this.status = i;
	}

	int getStatus(){
		return status;
	}

	static String getStatus(int index){
		switch(index){
			case -1:
				return "UNPLACED";
			case 0:
				return "PLACED";
			case 1:
				return "PACKED";
			case 2:
				return "DISPATCHED";
			case 3:
				return "DELIVERED";
			case 4:
				return "CANCELLED";
			default:
				return "ERROR";
		}
	}
}
