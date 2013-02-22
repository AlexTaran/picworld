package alex.taran.opengl.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import alex.taran.opengl.utils.ResourceUtils;
import android.content.Context;
import android.util.Log;

public class Model implements Serializable {
	private static final long serialVersionUID = 836158788393139455L;
	
	private Model(Map<String, List<Float>> groups) {
		this.groups = new HashMap<String, float[]>();
		int sum = 0;
		for (Entry<String,List<Float> > e : groups.entrySet()) {
			sum += e.getValue().size();
			float[] arr = new float[e.getValue().size()];
			for (int i = 0; i < e.getValue().size(); ++i) {
				arr[i] = e.getValue().get(i);
			}
			this.groups.put(e.getKey(), arr);
		}
		totalSize = sum;
	}
	
	public static Model loadFromAndroidObj(Context context, String fileName) {
		return loadObjFromStream(ResourceUtils.getInputStreamForRawResource(context, fileName));
	}
	
	public static Model loadFromAndroidObj(Context context, String fileName, float scale) {
		return loadObjFromStream(ResourceUtils.getInputStreamForRawResource(context, fileName), scale);
	}
	
	public static Model loadObjFromStream(InputStream is) {
		return loadObjFromStream(is, 1.0f);
	}

	public static Model loadObjFromStream(InputStream is, float scale) {
		List<String> file = ResourceUtils.loadInputStreamAsLines(is);
		Log.i("MYOBJ", "Start loading OBJ model with " + file.size() + " lines");
		List<Float> vertices = new ArrayList<Float>();
		List<Float> texcoords = new ArrayList<Float>();
		List<Float> normals = new ArrayList<Float>();
		final String defaultGroupName = "DefaultGroup";
		String currentGroupName = defaultGroupName;
		Map<String, List<Float>> groups = new HashMap<String, List<Float>>();
		groups.put(currentGroupName, new ArrayList<Float>());
		List<Float> currentGroup = groups.get(currentGroupName);
		Random r = new Random(234);
		// TODO: here load the model from a file
		for (int line_idx = 0; line_idx < file.size(); ++line_idx) {
			String line  = file.get(line_idx);
			try {
				String[] splitted = line.trim().split("\\s+");
				if (line.equals("") || splitted[0].equals("")) {
					continue;
				}
				if (splitted[0].equals("v")) {
					vertices.add(Float.parseFloat(splitted[1]) * scale);
					vertices.add(Float.parseFloat(splitted[2]) * scale);
					vertices.add(Float.parseFloat(splitted[3]) * scale);
				} else if (splitted[0].equals("vt")) {
					texcoords.add(Float.parseFloat(splitted[1]));
					texcoords.add(Float.parseFloat(splitted[2]));
					texcoords.add(Float.parseFloat(splitted[3]));
				} else if (splitted[0].equals("vn")) {
					normals.add(Float.parseFloat(splitted[1]));
					normals.add(Float.parseFloat(splitted[2]));
					normals.add(Float.parseFloat(splitted[3]));
				} else if (splitted[0].equals("f")) {
					int[] faceIndexes = null;
					// triangle
					List<Float> faces = new ArrayList<Float>();
					for (int i = 1; i < splitted.length; ++i) {
						String[] attributeIndexes = splitted[i].trim().split("/");
						int v = Integer.parseInt(attributeIndexes[0]) - 1;
						int t = Integer.parseInt(attributeIndexes[1]) - 1;
						int n = Integer.parseInt(attributeIndexes[2]) - 1;
						for (int j = 0; j < 3; ++j) {
							faces.add(vertices.get(v * 3 + j));
						}
						for (int j = 0; j < 3; ++j) {
							faces.add(texcoords.get(t * 3 + j));
						}
						for (int j = 0; j < 3; ++j) {
							faces.add(normals.get(n * 3 + j));
						}
					}
					if (splitted.length == 4) { // triangle
						//currentGroup.addAll(faces);
						for (int i = 0; i < 3 * 9; ++i) {
							currentGroup.add(faces.get(i));
						}
					} else if (splitted.length == 5) { // quad
						for (int i = 0; i < 3 * 9; ++i) {
							currentGroup.add(faces.get(i));
						}
						for (int i = 0; i < 1 * 9; ++i) {
							currentGroup.add(faces.get(i));
						}
						for (int i = 2 * 9; i < 4 * 9; ++i) {
							currentGroup.add(faces.get(i));
						}
					} else {
						Log.i("MYOBJ", "Skipped face with " + splitted.length
								+ " vertices");
					}
				} else if (splitted[0].equals("g")) {
					Log.i("MYOBJ", "A new group is created: " + line);
					currentGroupName = defaultGroupName + r.nextFloat() + " " + line_idx;
					/*if (splitted.length >= 2) {
						currentGroupName = splitted[1];
					} else {
						currentGroupName = defaultGroupName + line_idx;
					}*/
					if (!groups.containsKey(currentGroupName)) {
						currentGroup = new ArrayList<Float>();
						groups.put(currentGroupName, currentGroup);
					} else {
						currentGroup = groups.get(currentGroupName);
					}
				} else if (splitted[0].equals("#")) {
					// Do nothing (comment)
				} else if (splitted[0].equals("s")) {
					// smoothing groups go fuck
				} else if (splitted[0].equals("usemtl")) {
					// materials go fuck
				} else if (splitted[0].equals("mtllib")) {
					// materials go fuck
				} else {
					Log.i("MYOBJ", "Unknown OBJ command received: '" + splitted[0] + "' on line " + line_idx);
					// UNKNOWN COMMAND RECEIVED
				}
			} catch (Exception e) {
				Log.i("MYOBJ", "Exception while parsing string " + line + " : " + e.getMessage());
			}
		}
		Log.d("MYOBJ", "Vertices: " + vertices.size() + " Texcoords: " + texcoords.size() + " Normals: " + normals.size());
		for (Entry<String, List<Float>> e: groups.entrySet()) {
			Log.d("MYOBJ", "Group: "+ e.getKey() + " Size: " + e.getValue().size() /3 / 9 + " triangles");
		}
		return new Model(groups);
	}
	
	public static Model loadSerializedFromStream(InputStream is) {
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			Model mdl = (Model)ois.readObject();
			Log.d("FUCK", "Model loaded. Number of groups: " + mdl.groups.size());
			return mdl;
		} catch (Exception e) {
			Log.d("FUCK", "Error in deserialization of Model from stream" + e.getMessage());
			return null;
		}
	}

	public Map<String, Integer> genNamedOffsetForVertexBuffer() {
		int currentOffset = 0;
		Map<String, Integer> namedOffsets = new HashMap<String, Integer>();
		for (String name : groups.keySet()) {
			namedOffsets.put(name, currentOffset * 4);
			currentOffset += groups.get(name).length;
		}
		return namedOffsets;
	}

	public float[] genVertexBuffer() {
		int vboSize = 0;
		for (String name : groups.keySet()) {
			vboSize += groups.get(name).length;
		}
		float[] buf = new float[vboSize];
		int offset = 0;
		for (String name : groups.keySet()) {
			float[] arr = groups.get(name);
			for (int i = 0; i < arr.length; ++i) {
				buf[offset] = arr[i];
				++offset;
			}
		}
		return buf;
		/*
		List<Float> vbo = new ArrayList<Float>();
		
		for (String name : groups.keySet()) {
			vbo.addAll(groups.get(name));
			//vbo.addAll(0, groups.get(name));
		}
		
		float[] buf = new float[vbo.size()];
		for (int i = 0; i < vbo.size(); ++i) {
			buf[i] = vbo.get(i);
		}
		return buf;*/
	}
	
	public int getGroupSize(String groupName) {
		return groups.get(groupName).length;
	}
	
	public Set<String> getGroupNames() {
		return groups.keySet();
	}
	
	public int getTotalSize() {
		return totalSize;
	}
	
	public void scale(float k) {
		scale(k, k, k);
	}
	
	// WARNING! if arguments are not equal, IT BREAKS NORMALS!!! 
	// if you implement fixing normals, you can make it public 
	private void scale(float kx, float ky, float kz) {
		for (Entry<String, float[]> e: groups.entrySet()) {
			float[] arr = e.getValue();
			for (int i = 0; i < arr.length; ++i) {
				if (i % 9 == 0) {
					arr[i] *= kx;
				} else if (i % 9 == 1) {
					arr[i] *= ky;
				} else if (i % 9 == 2) {
					arr[i] *= kz;
				} else {
					// pass
				}
			}
		}
	}
	
	// returns 6 floats: minx, maxx, miny, maxy, minz, maxz 
	public float[] getModelDimensions() {
		float[] dimensions = new float[] {Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE,
				Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE};
		for (Entry<String, float[]> e: groups.entrySet()) {
			float minx = Float.MIN_VALUE, maxx, miny, maxy, minz, maxz;
			int idx = 0;
			for (Float f: e.getValue()) {
				if (idx % 9 == 0) {
					dimensions[0] = Math.min(dimensions[0], f);
					dimensions[1] = Math.max(dimensions[1], f);
				} else if (idx % 9 == 1) {
					dimensions[2] = Math.min(dimensions[2], f);
					dimensions[3] = Math.max(dimensions[3], f);
				} else if (idx % 9 == 2) {
					dimensions[4] = Math.min(dimensions[4], f);
					dimensions[5] = Math.max(dimensions[5], f);
				}
				++idx;
			}
		}
		return dimensions;
	}

	private final Map<String, float[]> groups;
	private final Integer totalSize;
}
