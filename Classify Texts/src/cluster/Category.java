package cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Category {

	// �ҵ�K����ڼ�
	public Map<Integer, List<Distance>> getKarea(
			Map<Integer, List<Distance>> dismap, int k) {
		Map<Integer, List<Distance>> karea = new HashMap<Integer, List<Distance>>();
		Object[] classes = dismap.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			List<Distance> klist = new ArrayList<Distance>();
			if (k <= dismap.get(classes[i]).size()) {
				for (int j = 0; j < k; j++) {
					klist.add(dismap.get(classes[i]).get(j));
				}
			}
			karea.put(i, klist);
		}
		// Object[] classes2 = karea.keySet().toArray();
		// for (int i = 0; i < classes2.length; i++) {
		// for (int j = 0; j < karea.get(classes2[i]).size(); j++) {
		// System.out.println("karea.get(" + i + ").get(" + j + ")"
		// + "index: " + karea.get(classes2[i]).get(j).getIndex()
		// + "|point" + karea.get(classes2[i]).get(j).getPoint());
		// }
		// }
		System.out.println("karea.size()" + karea.size());
		return karea;
	}

	// ϡ�����ͼ�������ϸ����ͼ
	public Map<Integer, List<Distance>> spareKarea(
			Map<Integer, List<Distance>> karea) {
		Map<Integer, List<Distance>> skarea = new HashMap<Integer, List<Distance>>();
		Object[] classes = karea.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			Distance sknode = new Distance();
			List<Distance> nlist = new ArrayList<Distance>();
			for (int j = 0; j < classes.length; j++) {
				// Sknn(x)���ж�����
				// �ߵ��ظ����
				if (i != j) {
					sknode = calSKnn(karea.get(classes[i]), karea
							.get(classes[j]));
					// ͨ��link���ж�����֮���Ƿ�������
					if (sknode.getLink() != 0) {
						nlist.add(sknode);
					}

				}
			}
			// ͨ���ж�list�Ĵ�С���жϸõ��Ƿ����ϸ�K�����
			if (nlist.size() != 0) {
				skarea.put(i, nlist);
			}
		}
//		Object[] classes2 = skarea.keySet().toArray();
//		for (int i = 0; i < classes2.length; i++) {
//			for (int j = 0; j < skarea.get(classes2[i]).size(); j++) {
//				System.out.println("skarea.get(" + i + ").get(" + j + ")"
//						+ "index: " + skarea.get(classes2[i]).get(j).getIndex()
//						+ "|point" + skarea.get(classes2[i]).get(j).getPoint());
//			}
//		}
		System.out.println("skarea.size()" + skarea.size());
		return skarea;
	}

	// ������ѡ����һ��SKnn��x��
	public Distance calSKnn(List<Distance> x, List<Distance> y) {
		Distance sknode = new Distance();
		sknode.setLink(0);
		for (int i = 0; i < x.size(); i++) {
			for (int j = 0; j < y.size(); j++) {
				if (x.get(i).getIndex().equals(y.get(j).getPoint())
						&& x.get(i).getPoint().equals(y.get(j).getIndex())) {
					sknode.setIndex(x.get(i).getIndex());
					sknode.setPoint(x.get(i).getPoint());
					sknode.setLink(1);
				}
			}
		}
		return sknode;
	}

	// ���������ϸ�K����ͼ
	public Map<Integer, List<SDistance>> buildSMarea(
			Map<Integer, List<Distance>> skarea) {
		Map<Integer, List<SDistance>> smarea = new HashMap<Integer, List<SDistance>>();
		Object[] classes = skarea.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			List<SDistance> slist = new ArrayList<SDistance>();
			for (int j = 0; j < classes.length; j++) {
				if (i != j) {
					for (int m = 0; m < skarea.get(classes[i]).size(); m++) {
						// ����Sknn�����е��index������ͬ�ģ���������ʱ���������һ����
						if (skarea.get(classes[i]).get(m).getPoint().equals(
								skarea.get(classes[j]).get(0).getIndex())) {
							int similarity = calSimilarity(skarea
									.get(classes[i]), skarea.get(classes[j]));
							SDistance snode = new SDistance();
							snode.setIndex(skarea.get(classes[i]).get(m)
									.getIndex());
							snode.setPoint(skarea.get(classes[i]).get(m)
									.getPoint());
							snode.setSimilarity(similarity);
							slist.add(snode);
						}
					}
				}

			}
			smarea.put(i, slist);
		}
//		Object[] classes2 = smarea.keySet().toArray();
//		for (int i = 0; i < classes2.length; i++) {
//			for (int j = 0; j < smarea.get(classes2[i]).size(); j++) {
//				System.out.println("smarea.get(" + i + ").get(" + j + ")"
//						+ "index: " + smarea.get(classes2[i]).get(j).getIndex()
//						+ "|similarity"
//						+ smarea.get(classes2[i]).get(j).getSimilarity());
//			}
//		}
		System.out.println("smarea.size()" + smarea.size());
		return smarea;
	}

	// �����ϸ�K����ͼ���ƶ�
	// SKNN��x����SKNN��Y��
	public Integer calSimilarity(List<Distance> x, List<Distance> y) {
		int similarity = 0;
		for (int i = 0; i < x.size(); i++) {
			for (int j = 0; j < y.size(); j++) {
				if (x.get(i).getPoint().equals(y.get(j).getPoint())) {
					similarity = similarity + 1;
				}
			}
		}
		return similarity;
	}

	// �����ϸ�����ܶ�ͼ
	public Map<Integer, SDistance> calDenstiy(
			Map<Integer, List<SDistance>> smarea) {
		Map<Integer, SDistance> sdarea = new HashMap<Integer, SDistance>();
		Object[] classes = smarea.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			int density = 0;
			for (int j = 0; j < smarea.get(classes[i]).size(); j++) {
				density = density
						+ smarea.get(classes[i]).get(j).getSimilarity();
			}
			SDistance sDistance = new SDistance();
			sDistance.setIndex(smarea.get(classes[i]).get(0).getIndex());
			sDistance.setDensity(density);
			sdarea.put(i, sDistance);
		}
		// Object[] classes2 = sdarea.keySet().toArray();
		// for (int i = 0; i < classes2.length; i++) {
		// System.out.println("sdarea.get(" + i + ")" + "index: "
		// + sdarea.get(classes2[i]).getIndex() + "|Density"
		// + sdarea.get(classes2[i]).getDensity());
		// }
		System.out.println("sdarea.size()" + sdarea.size());
		return sdarea;
	}

	// ͨ�������ܶ�������ȷ���ܶ���ֵ
	public Double calThreshold(Map<Integer, SDistance> sdarea) {
		Object[] classes = sdarea.keySet().toArray();
		double thres = 0;
		int amount = 0;
		for (int i = 0; i < classes.length; i++) {
			amount = amount + sdarea.get(classes[i]).getDensity();
		}
		double expect = amount / classes.length;
		System.out.println("expect: " + expect);
		List<SDistance> sdlist = new ArrayList<SDistance>();
		// ��sdarea�����е�Ԫ�ر�����sdlist����������
		for (int i = 0; i < classes.length; i++) {
			sdlist.add(sdarea.get(classes[i]));
		}
		// ð������,���н�������
		for (int i = 0; i < sdlist.size() - 1; i++) {
			for (int j = 0; j < sdlist.size() - i - 1; j++) {
				if (sdlist.get(j).getDensity() < sdlist.get(j + 1).getDensity()) {
					SDistance temp = sdlist.get(j);
					sdlist.set(j, sdlist.get(j + 1));
					sdlist.set(j + 1, temp);
				}
			}
		}
		for (int m = 0; m < sdlist.size(); m++) {
			// System.out.println("sdlist.get("+m+"): "+sdlist.get(m).getIndex()+"|"+sdlist.get(m).getDensity());
			sdarea.put(m, sdlist.get(m));
		}
//		 Object[] classes2 = sdarea.keySet().toArray();
//		 for (int i = 0; i < classes2.length; i++) {
//		 System.out.println("sdarea2.get(" + i + ")" + "index: "
//		 + sdarea.get(classes2[i]).getIndex() + "|Density"
//		 + sdarea.get(classes2[i]).getDensity());
//		 }
		for (int i = 1; i < classes.length - 1; i++) {
			if (sdarea.get(classes[i]).getDensity() >= expect) {
				if (sdarea.get(classes[i + 1]).getDensity() < sdarea.get(
						classes[i]).getDensity()
						&& sdarea.get(classes[i + 1]).getDensity() < sdarea
								.get(classes[i - 1]).getDensity()) {
					thres = sdarea.get(classes[i + 1]).getDensity()
							- sdarea.get(classes[i]).getDensity();
					// System.out.println(sdarea.get(classes[i +
					// 1]).getDensity());
					// System.out.println(sdarea.get(classes[i]).getDensity());
					break;
				}
			}
		}
		thres = Math.sqrt(thres * thres);
		System.out.println("�ܶ���ֵ��" + thres);
		return thres;
	}

	// ��¼�ǵ��ܶȵĵ㼯
	public Set<SDistance> keepNLDensity(Map<Integer, SDistance> sdarea) {
		Double thres = calThreshold(sdarea);
		Set<SDistance> hs = new HashSet<SDistance>();
		System.out.println("thres: " + thres);
		Iterator<Integer> iterator1 = sdarea.keySet().iterator();
		while (iterator1.hasNext()) {
			Integer key1 = (Integer) iterator1.next();
			if (sdarea.get(key1).getDensity() >= thres) {
				hs.add(sdarea.get(key1));

			}

		}
		// System.out.println("hs.size()" + hs.size());
		// Iterator<SDistance> it1 = hs.iterator();
		// while (it1.hasNext()) {
		// SDistance temp = it1.next();
		// System.out.println("hs.getIndex:" + temp.getIndex() + "getDensity:"
		// + temp.getDensity());
		// }
		return hs;
	}

	// ��¼���ܶȵĵ㼯
	public Set<SDistance> keepLDensity(Map<Integer, SDistance> sdarea) {
		Double thres = calThreshold(sdarea);
		Set<SDistance> ls = new HashSet<SDistance>();
		Iterator<Integer> iterator1 = sdarea.keySet().iterator();
		while (iterator1.hasNext()) {
			Integer key1 = (Integer) iterator1.next();
			if (sdarea.get(key1).getDensity() < thres) {
				ls.add(sdarea.get(key1));
			}

		}
		// System.out.println("ls.size()" + ls.size());
		// Iterator<SDistance> it1 = ls.iterator();
		// while (it1.hasNext()) {
		// SDistance temp = it1.next();
		// System.out.println("ls.getIndex:" + temp.getIndex() + "getDensity:"
		// + temp.getDensity());
		// }
		return ls;
	}

	// ���ݸ��ܶȵĴ���㣬��ѡ�ϸ�K����ͼ�еĵ㣬���γ���ͨͼ����ʼ����
	public void classification(Map<Integer, List<SDistance>> smarea,
			Set<SDistance> hs, Set<SDistance> ls,
			Map<Integer, List<SDistance>> shmarea,
			Map<Integer, List<SDistance>> slmarea) {
		Iterator<SDistance> iterator1 = hs.iterator();
		Iterator<SDistance> iterator2 = ls.iterator();

		Object[] classes2 = smarea.keySet().toArray();
		while (iterator1.hasNext()) {
			SDistance temp = iterator1.next();
			for (int j = 0; j < classes2.length; j++) {
				if (temp.getIndex().equals(
						smarea.get(classes2[j]).get(0).getIndex())) {
					shmarea.put(j, smarea.get(classes2[j]));
				}
			}
		}
		while (iterator2.hasNext()) {
			SDistance temp2 = iterator2.next();
			for (int j = 0; j < classes2.length; j++) {
				if (temp2.getIndex().equals(
						smarea.get(classes2[j]).get(0).getIndex())) {
					slmarea.put(j, smarea.get(classes2[j]));
				}
			}
		}
//		Object[] classes3 = shmarea.keySet().toArray();
//		Object[] classes4 = slmarea.keySet().toArray();
//		System.out.println("");
//		System.out.println("���ܶȣ�");
//		for (int g = 0; g < classes3.length; g++) {
//			System.out.println("");
//			System.out.println("���Ե�"
//					+ shmarea.get(classes3[g]).get(0).getIndex() + ": ");
//			int h = 0;
//			for (int k = 0; k < shmarea.get(classes3[g]).size(); k++) {
//				if (h >= 5) {
//					System.out.println("");
//					h = h % 5;
//				}
//				System.out.print("("
//						+ shmarea.get(classes3[g]).get(k).getIndex() + ","
//						+ shmarea.get(classes3[g]).get(k).getPoint() + ")  ");
//				h = h + 1;
//			}
//			System.out.println("");
//		}
//		System.out.println("");
//		System.out.println("�Ǹ��ܶȣ�");
//		for (int g = 0; g < classes4.length; g++) {
//			System.out.println("");
//			System.out.println("���Ե�"
//					+ slmarea.get(classes4[g]).get(0).getIndex() + ": ");
//			int nh = 0;
//			for (int k = 0; k < slmarea.get(classes4[g]).size(); k++) {
//				if (nh >= 5) {
//					System.out.println("");
//					nh = nh % 5;
//				}
//				System.out.print("("
//						+ slmarea.get(classes4[g]).get(k).getIndex() + ","
//						+ slmarea.get(classes4[g]).get(k).getPoint() + ")  ");
//				nh = nh + 1;
//			}
//			System.out.println("");
//		}

	}

	// �Ƴ��ظ�������
	public Map<Integer, List<String>> calList(Map<Integer, List<String>> conn) {
		Iterator<Integer> iterator1 = conn.keySet().iterator();
		while (iterator1.hasNext()) {
			Integer key1 = (Integer) iterator1.next();
			Iterator<Integer> iterator2 = conn.keySet().iterator();
			while (iterator2.hasNext()) {
				Integer key2 = (Integer) iterator2.next();
				if (key1 != key2 && conn.get(key1) != null
						&& conn.get(key2) != null) {
					// ���н�����listֱ��ƴ�ӵ����е�list�У�֮����ɾ���ظ���Ԫ��
					for (int m = 0; m < conn.get(key1).size(); m++) {
						for (int n = 0; n < conn.get(key2).size(); n++) {
							if (conn.get(key1).get(0) != null
									&& conn.get(key2).get(0) != null)
								if (conn.get(key1).get(m).equals(
										conn.get(key2).get(n))) {
									for (int k = 0; k < conn.get(key2).size(); k++) {
										conn.get(key1).add(
												conn.get(key2).get(k));
										conn.get(key2).set(k, null);
									}
								}
						}
					}
				}
			}
		}
		// ȥ��Ԫ��Ϊnull��list
		Iterator<Integer> iterator3 = conn.keySet().iterator();
		while (iterator3.hasNext()) {
			Integer key = (Integer) iterator3.next();
			if (conn.get(key).get(0) == null) {
				iterator3.remove();
				conn.remove(key);
			}
		}
		// ���н�����list�ٴ�ƴ����������ɾ���ظ���Ԫ��
		Object[] classes = conn.keySet().toArray();
		for (int b = 0; b < classes.length; b++) {
			for (int c = 0; c < classes.length; c++) {
				for (int d = 0; d < conn.get(classes[b]).size(); d++) {
					for (int e = 0; e < conn.get(classes[c]).size(); e++) {
						if (d != e)
							if (conn.get(classes[b]).get(d) != null
									&& conn.get(classes[c]).get(e) != null)
								if (conn.get(classes[b]).get(d).equals(
										conn.get(classes[c]).get(e))) {
									conn.get(classes[c]).set(e, null);
								}
					}
				}
			}
		}
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < conn.get(classes[i]).size(); j++) {
				// ������.equals(),��Ϊnull�ǿն���
				if (conn.get(classes[i]).get(j) == null) {
					conn.get(classes[i]).remove(j);
					j = j - 1;
				}
			}
		}
		//ɾ��ֻ������Ԫ�ص���𣬾������κ�����
		Iterator<Integer> iterator4 = conn.keySet().iterator();
		while(iterator4.hasNext()){
			Integer key = (Integer) iterator4.next();
			if(conn.get(key).size() == 2){
				iterator4.remove();
				conn.remove(key);
			}
		}
		return conn;

	}

	// ���Ƴ��˵��ܶȵĵ㼯������ͨͼ���ֳ��µĴ�
	public Map<Integer, List<String>> buildConn(Map<Integer, List<SDistance>> shmarea) {
		Map<Integer, List<String>> conn = new HashMap<Integer, List<String>>();
		Set<Set<String>> connset = new HashSet<Set<String>>();
		Object[] classes = shmarea.keySet().toArray();
		System.out.println("shmarea size()" + shmarea.size());
		for (int i = 0; i < classes.length; i++) {
			boolean ck = false;
			Set<String> ckset = new HashSet<String>();
			for (int j = 0; j < classes.length; j++) {
				// �ߵ��ظ����
				if (i != j) {
					ck = calConn(shmarea.get(classes[i]), shmarea
							.get(classes[j]));
					// ͨ��link���ж�����֮���Ƿ�������
					if (ck == true) {
						ckset.add(shmarea.get(classes[j]).get(0).getIndex());
						// �Ѹõ��µ�pointȫ�������б�
						for (int m = 0; m < shmarea.get(classes[j]).size(); m++) {
							ckset
									.add(shmarea.get(classes[j]).get(m)
											.getPoint());
						}
					}
				}
			}
			// ͨ���ж�list�Ĵ�С���жϸõ��Ƿ����ϸ�K�����
			if (ckset.size() != 0) {
				// ����ָ����
				ckset.add(shmarea.get(classes[i]).get(0).getIndex());
				// �Ѹõ��µ�pointȫ�������б�
				for (int n = 0; n < shmarea.get(classes[i]).size(); n++) {
					ckset.add(shmarea.get(classes[i]).get(n).getPoint());
				}
				connset.add(ckset);
			}
		}
		Iterator<Set<String>> it1 = connset.iterator();
		int j = 0;
		while (it1.hasNext()) {
			Set<String> temp = it1.next();
			List<String> cklist = new ArrayList<String>();
			Iterator<String> it2 = temp.iterator();
			while (it2.hasNext()) {
				String temp2 = it2.next();
				cklist.add(temp2);
			}
			conn.put(j, cklist);
			j++;
		}
		// ȥ���ظ��ĺͲ����ظ���list
		Map<Integer, List<String>> conn2 = new HashMap<Integer, List<String>>();
		conn2.putAll(calList(conn));
		// ��ӡ��
		Object[] classes2 = conn2.keySet().toArray();
		int k = 1;
		for (int m = 0; m < classes2.length; m++) {
			System.out.println("��" + k + "��");
			List<String> klist = new ArrayList<String>();
			if (conn2.get(classes2[m]) != null)
				for (int n = 0; n < conn2.get(classes2[m]).size(); n++) {
					System.out.print(conn2.get(classes2[m]).get(n) + " ");
					klist.add(conn2.get(classes2[m]).get(n));
				}
			System.out.println("");
//			System.out.println(""+k+":"+evaluater(klist,karea)+"");
			k = k + 1;
		}
		return conn2;
	}

	public List<Double> evaluater(List<String> klist, Map<Integer, List<Distance>> karea) {
		List<Double> elist = new ArrayList<Double>();
		for (int i = 0; i < klist.size(); i++) {
			for (int j = 0; j < klist.size(); j++) {
				if (i != j) {
					Object[] classes = karea.keySet().toArray();
					for (int m = 0; m < classes.length; m++) {
						for (int n = 0; n < karea.get(classes[m]).size(); n++) {
							if (klist.get(i).equals(
									karea.get(classes[m]).get(n).getIndex())
									&& klist.get(j).equals(
											karea.get(classes[m]).get(n)
													.getPoint())) {
								elist.add(karea.get(classes[m]).get(n).getValue());

							}
						}
					}
				}
			}
		}
//		Double amount =0.0;
		Collections.sort(elist);
//		for(int e=0;e<elist.size();e++){
//			System.out.println("elist.get("+e+")"+elist.get(e));
//			amount+=elist.get(e);
//		}
		return elist;
	}

	// ��������֮���Ƿ��������
	public boolean calConn(List<SDistance> x, List<SDistance> y) {
		boolean ck = false;
		List<SDistance> cklist = new ArrayList<SDistance>();
		for (int i = 0; i < x.size(); i++) {
			for (int j = 0; j < y.size(); j++) {
				if (x.get(i).getPoint().equals(y.get(j).getIndex())
						&& x.get(i).getIndex().equals(y.get(j).getPoint())) {
					cklist.add(y.get(j));
				}
			}
		}
		if (cklist.size() > 0) {
			ck = true;
		}
		return ck;
	}
}
