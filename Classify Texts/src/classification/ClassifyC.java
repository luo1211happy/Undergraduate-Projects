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
		// k从2开始取值,因为从1开始取值会划分过细
		Set<Set<String>> completeset = new HashSet<Set<String>>();
		//用来判断分类的能力
		int flag =0;
		for (int k = 2; k < dismap.size(); k++) {
			System.out.println("k :" + k);
			Map<Integer, List<Distance>> karea = category.getKarea(dismap, k);
			// 构造严格K近邻图
			Map<Integer, List<Distance>> skarea = category.spareKarea(karea);
			// 构造共享严格K近邻图,计算相似度
			Map<Integer, List<SDistance>> smarea = category.buildSMarea(skarea);
			// 构建严格近邻密度图
			// Map<Integer,SDistance> slldarea = category.calDenstiy(smarea);
			Map<Integer, SDistance> sdarea = category.calDenstiy(smarea);
			// 记录下低密度的点集
			Set<SDistance> sldset = category.keepLDensity(sdarea);
			// 记录下非低密度的点集
			Set<SDistance> shdset = category.keepNLDensity(sdarea);
			// 根据低密度的代表点，移除严格K近邻图中低密度的点
			// Map<Integer,List<SNode>> shmarea =
			// matrix.removeLowPoints(smarea,sldarea);
			// 根据高密度的代表点，挑选严格K近邻图中的点，并形成连通图，开始聚类
			Map<Integer, List<SDistance>> shmarea = new HashMap<Integer, List<SDistance>>();
			Map<Integer, List<SDistance>> slmarea = new HashMap<Integer, List<SDistance>>();
			category.classification(smarea, shdset, sldset, shmarea, slmarea);
			// 将非低密度的点集根据连通性划分簇
			Map<Integer, List<String>> conn = category
					.buildConn(shmarea);
			// Map<Integer,List<String>> conn2 =
			// category.buildConn(slmarea,karea);
			// System.out.println("conn.size() :"+conn.size());
			// 对聚类后的结果进行后期处理
			if (conn.size() != 1) {
				flag = 0;
				Object[] classes = conn.keySet().toArray();
				for (int i = 0; i < classes.length; i++) {
					// 每一簇的个数不能超过K*K
					if (conn.get(classes[i]).size() < k * k) {
						List<Double> temp = category.evaluater(conn
								.get(classes[i]), karea);
						Double amount = 0.0;
						for (int j = 0; j < temp.size(); j++) {
							amount = amount + temp.get(j);
						}
						//计算标准差
						double mean = amount/temp.size();
						System.out.println("mean: "+mean);
						double summit =0.0;
						for(int m=0;m<temp.size();m++){
							summit = summit + (temp.get(m)-mean)*(temp.get(m)-mean);
						}
						double sderivation = Math.sqrt(summit/temp.size());
						System.out.println("标准差 ："+sderivation);
						// 设置基本簇类的阈值为最小元素与簇类整体相似度的差值不超过0.06（经过计算如此）
						System.out.println("0.06: "
								+ Math
										.sqrt((temp.get(0) - (amount / temp
												.size()))
												* (temp.get(0) - (amount / temp
														.size()))));
						//英文阈值设为0.06
						//中文阈值设为0.1
						if(language.equals(true)){
							if(sderivation < 0.1)
											{
										Set<String> bset = new HashSet<String>();
										for (int b = 0; b < conn.get(classes[i]).size(); b++) {
											bset.add(conn.get(classes[i]).get(b));
										}
										completeset.add(bset);
									}
									// 对基本簇类(类别个数是最多的)开始扩展,根据不同的k值产生不同的簇类,只要与基本簇类产生交集就进行合并
									else {
										Iterator<Set<String>> it1 = completeset.iterator();
										// 使用一个集合来过渡一下
										Set<Set<String>> tcompleteset = new HashSet<Set<String>>();
										while (it1.hasNext()) {
											Set<String> itemp = it1.next();
											Iterator<String> it2 = itemp.iterator();
											while (it2.hasNext()) {
												// 与conn中各个list做比较，合并簇类
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
														// 对没有产生交集的set也进行装填
														// 虽然会多次重复装填，但是由于set是无重复的堆，所以没有关系，不会影响最后的数量
														// 由于循环的conn，所以在这一次循环过程中匹配的set，在下一次循环中就不匹配了
														// 所以tcomplete中会存在有交集的set,这个之后再合并时剔除
														tcompleteset.add(itemp);
													}
												}
											}
										}
										// 对completeset重新装填
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
									// 对基本簇类(类别个数是最多的)开始扩展,根据不同的k值产生不同的簇类,只要与基本簇类产生交集就进行合并
									else {
										Iterator<Set<String>> it1 = completeset.iterator();
										// 使用一个集合来过渡一下
										Set<Set<String>> tcompleteset = new HashSet<Set<String>>();
										while (it1.hasNext()) {
											Set<String> itemp = it1.next();
											Iterator<String> it2 = itemp.iterator();
											while (it2.hasNext()) {
												// 与conn中各个list做比较，合并簇类
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
														// 对没有产生交集的set也进行装填
														// 虽然会多次重复装填，但是由于set是无重复的堆，所以没有关系，不会影响最后的数量
														// 由于循环的conn，所以在这一次循环过程中匹配的set，在下一次循环中就不匹配了
														// 所以tcomplete中会存在有交集的set,这个之后再合并时剔除
														tcompleteset.add(itemp);
													}
												}
											}
										}
										// 对completeset重新装填
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
				//如果连续两次出现只有一个类的情况的话，说明k的取值已经无法划分出类
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
		// 移除重复以及有交集的列
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
