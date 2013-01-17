package alex.taran.opengl.model;

public class VertexAttribute {
	public enum AttributeType {
		UNKNOWN,
		POSITION,
		TEXCOORD,
		NORMAL,
	}
	
	public VertexAttribute() {
		attributeType = AttributeType.UNKNOWN;
		attributeSize = 0;
		data = null;
	}
	
	public VertexAttribute(AttributeType attributeType, int attributeSize) {
		this.attributeType = attributeType;
		this.attributeSize = attributeSize;
		data = null;
	}
	
	public AttributeType getAttributeType() {
		return attributeType;
	}

	public int getAttributeSize() {
		return attributeSize;
	}

	private final AttributeType attributeType;
	private final int attributeSize; // 1, 2, 3 or 4 components
	private final float[] data;
}
