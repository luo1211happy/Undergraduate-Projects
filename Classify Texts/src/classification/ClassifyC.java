package classification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliasi.util.Files;

import cluster.Category;
import cluster.CompareC;
import cluster.Distance;
import cluster.SDistance;

public class ClassifyC {

	/**
	 * @param args
	 * @throws Exception
	 */
	public Map<Integer,List<String>> getContent (String parentfile,Map<Integer, List<String>> nrconn, Integer times) throws Exception {
		// TODO Auto-generated method stub
		CompareC comparec = new CompareC();
		Map<Integer, List<Distance>> dismap = comparec.getDisMap(parentfile);
		Boolean language = comparec.getLanguage(parentfile);
		System.out.println("dismap.size()" + dismap.size());
		Category category = new Category();
		// k��2��ʼȡֵ,��Ϊ��1��ʼȡֵ�Ữ�ֹ�ϸ
		Set<Set<String>> completeset = new HashSet<Set<String>>();
		//�����жϷ��������
		int flag =0;
		for (int k = 2; k < dismap.size(); k++) {
			System.out.println("k :" + k);
			Map<Integer, List<Distance>> karea = category.getKarea(dismap, k);
			// �����ϸ�K����ͼ
			Map<Integer, List<Distance>> skarea = category.spareKarea(karea);
			// ���칲���ϸ�K����ͼ,�������ƶ�
			Map<Integer, List<SDistance>> smarea = category.buildSMarea(skarea);
			// �����ϸ�����ܶ�ͼ
			// Map<Integer,SDistance> slldarea = category.calDenstiy(smarea);
			Map<Integer, SDistance> sdarea = category.calDenstiy(smarea);
			// ��¼�µ��ܶȵĵ㼯
			Set<SDistance> sldset = category.keepLDensity(sdarea);
			// ��¼�·ǵ��ܶȵĵ㼯
			Set<SDistance> shdset = category.keepNLDensity(sdarea);
			// ���ݵ��ܶȵĴ���㣬�Ƴ��ϸ�K����ͼ�е��ܶȵĵ�
			// Map<Integer,List<SNode>> shmarea =
			// matrix.removeLowPoints(smarea,sldarea);
			// ���ݸ��ܶȵĴ���㣬��ѡ�ϸ�K����ͼ�еĵ㣬���γ���ͨͼ����ʼ����
			Map<Integer, List<SDistance>> shmarea = new HashMap<Integer, List<SDistance>>();
			Map<Integer, List<SDistance>> slmarea = new HashMap<Integer, List<SDistance>>();
			category.classification(smarea, shdset, sldset, shmarea, slmarea);
			// ���ǵ��ܶȵĵ㼯������ͨ�Ի��ִ�
			Map<Integer, List<String>> conn = category
					.buildConn(shmarea);
			// Map<Integer,List<String>> conn2 =
			// category.buildConn(slmarea,karea);
			// System.out.println("conn.size() :"+conn.size());
			// �Ծ����Ľ�����к��ڴ���
			if (conn.size() != 1) {
				flag = 0;
				Object[] classes = conn.keySet().toArray();
				for (int i = 0; i < classes.length; i++) {
					// ÿһ�صĸ������ܳ���K*K
					if (conn.get(classes[i]).size() < k * k) {
						List<Double> temp = category.evaluater(conn
								.get(classes[i]), karea);
						Double amount = 0.0;
						for (int j = 0; j < temp.size(); j++) {
							amount = amount + temp.get(j);
						}
						//�����׼��
						double mean = amount/temp.size();
						System.out.println("mean: "+mean);
						double summit =0.0;
						for(int m=0;m<temp.size();m++){
							summit = summit + (temp.get(m)-mean)*(temp.get(m)-mean);
						}
						double sderivation = Math.sqrt(summit/temp.size());
						System.out.println("��׼�� ��"+sderivation);
						// ���û����������ֵΪ��СԪ��������������ƶȵĲ�ֵ������0.06������������ˣ�
						System.out.println("0.06: "
								+ Math
										.sqrt((temp.get(0) - (amount / temp
												.size()))
												* (temp.get(0) - (amount / temp
														.size()))));
						//Ӣ����ֵ��Ϊ0.06
						//������ֵ��Ϊ0.1
						if(language.equals(true)){
							if(sderivation < 0.1)
											{
										Set<String> bset = new HashSet<String>();
										for (int b = 0; b < conn.get(classes[i]).size(); b++) {
											bset.add(conn.get(classes[i]).get(b));
										}
										completeset.add(bset);
									}
									// �Ի�������(������������)��ʼ��չ,���ݲ�ͬ��kֵ������ͬ�Ĵ���,ֻҪ�����������������ͽ��кϲ�
									else {
										Iterator<Set<String>> it1 = completeset.iterator();
										// ʹ��һ������������һ��
										Set<Set<String>> tcompleteset = new HashSet<Set<String>>();
										while (it1.hasNext()) {
											Set<String> itemp = it1.next();
											Iterator<String> it2 = itemp.iterator();
											while (it2.hasNext()) {
												// ��conn�и���list���Ƚϣ��ϲ�����
												String stemp = it2.next();
												for (int m = 0; m < conn.get(classes[i])
														.size(); m++) {
													if (stemp.equals(conn.get(classes[i])
															.get(m))) {
														System.out
																.println("conn.get(classses[].get()"
																		+ conn.get(
																				classes[i])
																				.get(m));
														Set<String> ccset = new HashSet<String>();
														for (int n = 0; n < conn.get(
																classes[i]).size(); n++) {
															ccset.addAll(itemp);
															ccset.add(conn.get(classes[i])
																	.get(n));
														}
														tcompleteset.add(ccset);
													} else {
														// ��û�в���������setҲ����װ��
														// ��Ȼ�����ظ�װ���������set�����ظ��Ķѣ�����û�й�ϵ������Ӱ����������
														// ����ѭ����conn����������һ��ѭ��������ƥ���set������һ��ѭ���оͲ�ƥ����
														// ����tcomplete�л�����н�����set,���֮���ٺϲ�ʱ�޳�
														tcompleteset.add(itemp);
													}
												}
											}
										}
										// ��completeset����װ��
										completeset.removeAll(completeset);
										completeset.addAll(tcompleteset);
									}
						}
						else{
							 if (Math.sqrt((temp.get(0) - (amount / temp.size()))
									 * (temp.get(0) - (amount / temp.size()))) < 0.06)
											{
										Set<String> bset = new HashSet<String>();
										for (int b = 0; b < conn.get(classes[i]).size(); b++) {
											bset.add(conn.get(classes[i]).get(b));
										}
										completeset.add(bset);
									}
									// �Ի�������(������������)��ʼ��չ,���ݲ�ͬ��kֵ������ͬ�Ĵ���,ֻҪ�����������������ͽ��кϲ�
									else {
										Iterator<Set<String>> it1 = completeset.iterator();
										// ʹ��һ������������һ��
										Set<Set<String>> tcompleteset = new HashSet<Set<String>>();
										while (it1.hasNext()) {
											Set<String> itemp = it1.next();
											Iterator<String> it2 = itemp.iterator();
											while (it2.hasNext()) {
												// ��conn�и���list���Ƚϣ��ϲ�����
												String stemp = it2.next();
												for (int m = 0; m < conn.get(classes[i])
														.size(); m++) {
													if (stemp.equals(conn.get(classes[i])
															.get(m))) {
														System.out
																.println("conn.get(classses[].get()"
																		+ conn.get(
																				classes[i])
																				.get(m));
														Set<String> ccset = new HashSet<String>();
														for (int n = 0; n < conn.get(
																classes[i]).size(); n++) {
															ccset.addAll(itemp);
															ccset.add(conn.get(classes[i])
																	.get(n));
														}
														tcompleteset.add(ccset);
													} else {
														// ��û�в���������setҲ����װ��
														// ��Ȼ�����ظ�װ���������set�����ظ��Ķѣ�����û�й�ϵ������Ӱ����������
														// ����ѭ����conn����������һ��ѭ��������ƥ���set������һ��ѭ���оͲ�ƥ����
														// ����tcomplete�л�����н�����set,���֮���ٺϲ�ʱ�޳�
														tcompleteset.add(itemp);
													}
												}
											}
										}
										// ��completeset����װ��
										completeset.removeAll(completeset);
										completeset.addAll(tcompleteset);
									}
							
						}
						
					}
					System.out.println("completeset.size(): "
							+ completeset.size());
					Iterator<Set<String>> it3 = completeset.iterator();
					int t = 0;
					while (it3.hasNext()) {
						Set<String> temp = it3.next();
						Iterator<String> it4 = temp.iterator();
						System.out.println("");
						System.out.println("" + t + ":");
						while (it4.hasNext()) {
							System.out.print(it4.next() + "|");
						}
						t++;
					}
				}

			} else {
				//����������γ���ֻ��һ���������Ļ���˵��k��ȡֵ�Ѿ��޷����ֳ���
				flag=flag+1;
				if(flag>=2){
					break;
				}
				else{
					continue;
				}
			}
		}
		Map<Integer, List<String>> rconn = new HashMap<Integer, List<String>>();
		Iterator<Set<String>> it1 = completeset.iterator();
		int k = 0;
		while (it1.hasNext()) {
			List<String> rlist = new ArrayList<String>();
			Set<String> ctemp = it1.next();
			Iterator<String> it2 = ctemp.iterator();
			while (it2.hasNext()) {
				rlist.add(it2.next());
			}
			rconn.put(k, rlist);
			k++;
		}
		System.out.println("rconn.size() :" + rconn.size());
		// �Ƴ��ظ��Լ��н�������
		nrconn = category.calList(rconn);

		System.out.println("nrconn.size()" + nrconn.size());
		Object[] classes = nrconn.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			System.out.println("");
			System.out.println(i + ":");
			for (int j = 0; j < nrconn.get(classes[i]).size(); j++) {
				System.out.print(nrconn.get(classes[i]).get(j) + "|");
			}
		}
//		comparec.classify(nrconn,parentfile);
//		System.out.println("nrconn.size()" + nrconn.size());
//		Object[] classes2 = nrconn.keySet().toArray();
//		for (int i = 0; i < classes2.length; i++) {
//			System.out.println("");
//			System.out.println(i + ":");
//			System.out.println("size: "+nrconn.get(classes2[i]).size());
//			for (int j = 0; j < nrconn.get(classes2[i]).size(); j++) {
//				System.out.print(nrconn.get(classes2[i]).get(j) + "|");
//			}
//		}
		System.out.println();
		comparec.printClass(nrconn,parentfile,times);
		return nrconn;

	}
}
