package cluster;

public class SDistance {
	private String index;//��ѡ�ĵ�
	private String point;//�ϸ�K����
	private Integer similarity;//���ƶȣ���ѡ�ĵ����ϸ�K���ڵĽ����ĸ�����
	private Integer density;//�ܶȣ��ϸ�K����������е����ƶ�֮�ͣ�
	private Integer link;//�Ƿ�������
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
