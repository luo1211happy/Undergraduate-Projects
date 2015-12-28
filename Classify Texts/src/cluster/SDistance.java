package cluster;

public class SDistance {
	private String index;//所选的点
	private String point;//严格K近邻
	private Integer similarity;//相似度（所选的点与严格K近邻的交集的个数）
	private Integer density;//密度（严格K最近邻中所有的相似度之和）
	private Integer link;//是否有连接
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
	public Integer getSimilarity() {
		return similarity;
	}
	public void setSimilarity(Integer similarity) {
		this.similarity = similarity;
	}
	public Integer getDensity() {
		return density;
	}
	public void setDensity(Integer density) {
		this.density = density;
	}
	public Integer getLink() {
		return link;
	}
	public void setLink(Integer link) {
		this.link = link;
	}

}
