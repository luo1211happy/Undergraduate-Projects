package cluster;

public class Distance {
	private String index;//所选择的的文档点
	private String point;//所选择的测试文档点
	private Double value;//文档与文档间的距离（相似度）
	private int link;//所选的点与测试点是否构成严格K近邻，是设为1，不是设为0
	public Distance(String index,String pointer,Double value){
		this.index = index;
		this.point=pointer;
		this.value= value;
	}
	public Distance(){
		
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public int getLink() {
		return link;
	}
	public void setLink(int link) {
		this.link = link;
	}
	

}
