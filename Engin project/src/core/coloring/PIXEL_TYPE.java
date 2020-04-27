package core.coloring;

public enum PIXEL_TYPE {
	PIXEL_SOLID(0x2588),
	PIXEL_THREEQUARTERS(0x2593),
	PIXEL_HALF(0x2592),
	PIXEL_QUARTER(0x2591);

	private long type;

	private PIXEL_TYPE(long value) {
		type = value;
	}

	public char type() {
		return (char) type;
	}
}
