package cluster;

public class TestDistance {
	private String filename;//测试文档名称
	private int c;//所比较的训练文件所在的簇类
	private Double distance;//测试文件与该簇类的所有元素进行相似度计算然后存入平均值
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	

}
