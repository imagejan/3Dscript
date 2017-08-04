package animation2;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Timelines {

	private final List<CtrlPoints> timelines = new ArrayList<CtrlPoints>();
	private final List<String> names = new ArrayList<String>();

	private final int nChannels;

	public static String getName(int i) {
		switch(i) {
		case 0: return "X Translation";
		case 1: return "Y Translation";
		case 2: return "Z Translation";

		case 3: return "X Rotation";
		case 4: return "Y Rotation";
		case 5: return "Z Rotation";

		case 6: return "Scale";

		case 7: return "Bounding Box X Min";
		case 8: return "Bounding Box Y Min";
		case 9: return "Bounding Box Z Min";
		case 10: return "Bounding Box X Max";
		case 11: return "Bounding Box Y Max";
		case 12: return "Bounding Box Z Max";

		case 13: return "Near";
		case 14: return "Far";
		default:
			int r = (i - 15) % 6;
			int c = (i - 15) / 6;
			switch(r) {
			case 0: return "Channel " + (c + 1) + " color min";
			case 1: return "Channel " + (c + 1) + " color max";
			case 2: return "Channel " + (c + 1) + " color gamma";
			case 3: return "Channel " + (c + 1) + " alpha min";
			case 4: return "Channel " + (c + 1) + " alpha max";
			case 5: return "Channel " + (c + 1) + " alpha gamma";
			}
		}
		return null;
	}

	public Timelines(int nChannels) {
		this.nChannels = nChannels;
		int i = 0;
		for(i = 0; i < 15; i++)
			names.add(getName(i));

		for(int c = 0; c < nChannels; c++) {
			for(int j = 0; j < 6; j++)
				names.add(getName(i++));
		}

		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());

		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());

		timelines.add(new CtrlPoints());

		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());

		timelines.add(new CtrlPoints());
		timelines.add(new CtrlPoints());

		for(int c = 0; c < nChannels; c++) {
			timelines.add(new CtrlPoints());
			timelines.add(new CtrlPoints());
			timelines.add(new CtrlPoints());
			timelines.add(new CtrlPoints());
			timelines.add(new CtrlPoints());
			timelines.add(new CtrlPoints());
		}
	}

	public void getBoundingBox(Point ll, Point ur) {
		ll.set(Integer.MAX_VALUE, Double.POSITIVE_INFINITY);
		ur.set(Integer.MIN_VALUE, Double.NEGATIVE_INFINITY);
		for(CtrlPoints c : timelines)
			c.getBoundingBox(ll, ur);
		if(ll.x == Integer.MAX_VALUE)
			ll.x = 0;
		if(ur.x == Integer.MIN_VALUE)
			ur.x = ll.x + 1;
		if(ll.y == Double.POSITIVE_INFINITY)
			ll.y = 0;
		if(ur.y == Double.NEGATIVE_INFINITY)
			ur.y = ll.y + 1;
		if(ur.x == ll.x)
			ur.x += 1;
		if(ur.y == ll.y)
			ur.y += 1;
	}

	public int getNChannels() {
		return nChannels;
	}

	public static void main(String[] args) {
		toJSON();
	}

	public static void toJSON() {
		Keyframe kf = new Keyframe(1,
				new RenderingSettings[] {
						new RenderingSettings(0, 255, 1, 0, 255, 2),
						new RenderingSettings(0, 255, 1, 0, 255, 2)
				},
				0, // near
				100, // far
				1, // scale,
				1, // dx,
				1, // dy,
				1, // dz,
				0, // angleX,
				0, // double angleY,
				0, // double angleZ,
				0, // int bbx,
				0, // int bby,
				0, // int bbz,
				256, // int bbw,
				256, // int bbh,
				57); // int bbd) {

		Gson gson = new GsonBuilder()
	             .disableHtmlEscaping()
	             .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
	             .setPrettyPrinting()
	             .serializeNulls()
	             .create();
		System.out.println(gson.toJson(kf));
	}

	public void clear() {
		for(CtrlPoints ctrls : timelines)
			ctrls.clear();
	}

	public boolean isEmpty() {
		for(CtrlPoints ctrls : timelines)
			if(ctrls.size() > 0)
				return false;
		return true;
	}

	private void record(int i, int t, double v) {
		if(v != Keyframe.UNSET)
			timelines.get(i).add(t, v);
		else {
			LinePoint lp = timelines.get(i).getPointAt(t);
			timelines.get(i).remove(lp);
		}
	}

	public void recordFrame(Keyframe kf) {
		int i = 0;
		int t = kf.getFrame();

		record(i++, t, kf.dx);
		record(i++, t, kf.dy);
		record(i++, t, kf.dz);

		record(i++, t, kf.angleX);
		record(i++, t, kf.angleY);
		record(i++, t, kf.angleZ);

		record(i++, t, kf.scale);

		record(i++, t, kf.bbx0);
		record(i++, t, kf.bby0);
		record(i++, t, kf.bbz0);
		record(i++, t, kf.bbx1);
		record(i++, t, kf.bby1);
		record(i++, t, kf.bbz1);

		record(i++, t, kf.near);
		record(i++, t, kf.far);

		for(int c = 0; c < nChannels; c++) {
			RenderingSettings rs = kf.renderingSettings[c];
			record(i++, t, rs.colorMin);
			record(i++, t, rs.colorMax);
			record(i++, t, rs.colorGamma);
			record(i++, t, rs.alphaMin);
			record(i++, t, rs.alphaMax);
			record(i++, t, rs.alphaGamma);
		}
	}

	double getInterpolatedValue(int i, int t, double def) {
		double v = timelines.get(i).getInterpolatedValue(t);
		if(v == Keyframe.UNSET)
			return def;
		return v;
	}

	public Keyframe getInterpolatedFrame(int t, Keyframe def) {
		Keyframe kf = new Keyframe(t);
		int i = 0;

		kf.dx = (float)getInterpolatedValue(i++, t, def.dx);
		kf.dy = (float)getInterpolatedValue(i++, t, def.dy);
		kf.dz = (float)getInterpolatedValue(i++, t, def.dz);

		kf.angleX = (float)getInterpolatedValue(i++, t, def.angleX);
		kf.angleY = (float)getInterpolatedValue(i++, t, def.angleY);
		kf.angleZ = (float)getInterpolatedValue(i++, t, def.angleZ);

		kf.scale = (float)getInterpolatedValue(i++, t, def.scale);

		kf.bbx0 = (int)Math.round(getInterpolatedValue(i++, t, def.bbx0));
		kf.bby0 = (int)Math.round(getInterpolatedValue(i++, t, def.bby0));
		kf.bbz0 = (int)Math.round(getInterpolatedValue(i++, t, def.bbz0));
		kf.bbx1 = (int)Math.round(getInterpolatedValue(i++, t, def.bbx1));
		kf.bby1 = (int)Math.round(getInterpolatedValue(i++, t, def.bby1));
		kf.bbz1 = (int)Math.round(getInterpolatedValue(i++, t, def.bbz1));

		kf.near = (float)getInterpolatedValue(i++, t, def.near);
		kf.far  = (float)getInterpolatedValue(i++, t, def.far);

		kf.renderingSettings = new RenderingSettings[nChannels];
		for(int c = 0; c < nChannels; c++) {
			kf.renderingSettings[c] = new RenderingSettings(
					(float)getInterpolatedValue(i + 0, t, def.renderingSettings[c].colorMin),
					(float)getInterpolatedValue(i + 1, t, def.renderingSettings[c].colorMax),
					(float)getInterpolatedValue(i + 2, t, def.renderingSettings[c].colorGamma),
					(float)getInterpolatedValue(i + 3, t, def.renderingSettings[c].alphaMin),
					(float)getInterpolatedValue(i + 4, t, def.renderingSettings[c].alphaMax),
					(float)getInterpolatedValue(i + 5, t, def.renderingSettings[c].alphaGamma));
			i += 6;
		}
		return kf;
	}

	public Keyframe getInterpolatedFrame(int t) {
		Keyframe kf = new Keyframe(t);
		int i = 0;

		kf.dx = (float)timelines.get(i++).getInterpolatedValue(t);
		kf.dy = (float)timelines.get(i++).getInterpolatedValue(t);
		kf.dz = (float)timelines.get(i++).getInterpolatedValue(t);

		kf.angleX = timelines.get(i++).getInterpolatedValue(t);
		kf.angleY = timelines.get(i++).getInterpolatedValue(t);
		kf.angleZ = timelines.get(i++).getInterpolatedValue(t);

		kf.scale = (float)timelines.get(i++).getInterpolatedValue(t);

		kf.bbx0 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));
		kf.bby0 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));
		kf.bbz0 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));
		kf.bbx1 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));
		kf.bby1 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));
		kf.bbz1 = (int)Math.round(timelines.get(i++).getInterpolatedValue(t));

		kf.near = (float)timelines.get(i++).getInterpolatedValue(t);
		kf.far  = (float)timelines.get(i++).getInterpolatedValue(t);

		kf.renderingSettings = new RenderingSettings[nChannels];
		for(int c = 0; c < nChannels; c++) {
			kf.renderingSettings[c] = new RenderingSettings(
					(float)timelines.get(i + 0).getInterpolatedValue(t), // colormin
					(float)timelines.get(i + 1).getInterpolatedValue(t), // colormax
					(float)timelines.get(i + 2).getInterpolatedValue(t), // colorgamma
					(float)timelines.get(i + 3).getInterpolatedValue(t), // alphamin
					(float)timelines.get(i + 4).getInterpolatedValue(t), // alphamax
					(float)timelines.get(i + 5).getInterpolatedValue(t)  // alphagamma
			);
			i += 6;
		}
		return kf;
	}

	private double get(int i, int t) {
		LinePoint lp = timelines.get(i).getPointAt(t);
		if(lp == null)
			return Keyframe.UNSET;
		return lp.y;
	}

	public Keyframe getKeyframeNoInterpol(int t) {
		Keyframe kf = new Keyframe(t);
		int i = 0;

		kf.dx = (float)get(i++, t);
		kf.dy = (float)get(i++, t);
		kf.dz = (float)get(i++, t);

		kf.angleX = get(i++, t);
		kf.angleY = get(i++, t);
		kf.angleZ = get(i++, t);

		kf.scale = (float)get(i++, t);

		kf.bbx0 = (int)Math.round(get(i++, t));
		kf.bby0 = (int)Math.round(get(i++, t));
		kf.bbz0 = (int)Math.round(get(i++, t));
		kf.bbx1 = (int)Math.round(get(i++, t));
		kf.bby1 = (int)Math.round(get(i++, t));
		kf.bbz1 = (int)Math.round(get(i++, t));

		kf.near = (float)get(i++, t);
		kf.far  = (float)get(i++, t);

		kf.renderingSettings = new RenderingSettings[nChannels];
		for(int c = 0; c < nChannels; c++) {
			kf.renderingSettings[c] = new RenderingSettings(
					(float)get(i + 0, t), // colormin
					(float)get(i + 1, t), // colormax
					(float)get(i + 2, t), // colorgamma
					(float)get(i + 3, t), // alphamin
					(float)get(i + 4, t), // alphamax
					(float)get(i + 5, t)  // alphagamma
			);
			i += 6;
		}
		return kf;
	}

	public CtrlPoints get(int i) {
		return timelines.get(i);
	}

	public int size() {
		return names.size();
	}

//	public String[] getNames() {
//		String[] names = new String[size()];
//		this.names.toArray(names);
//		return names;
//	}
}
