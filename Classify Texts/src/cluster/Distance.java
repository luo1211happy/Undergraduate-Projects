package cluster;

public class Distance {
	private String index;//��ѡ��ĵ��ĵ���
	private String point;//��ѡ��Ĳ����ĵ���
	private Double value;//�ĵ����ĵ���ľ��루���ƶȣ�
	private int link;//��ѡ�ĵ�����Ե��Ƿ񹹳��ϸ�K���ڣ�����Ϊ1��������Ϊ0
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
