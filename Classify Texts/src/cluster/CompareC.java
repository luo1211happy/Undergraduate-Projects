package cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.LanguageCallback;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


import com.aliasi.util.Counter;
import com.aliasi.util.Files;
import com.aliasi.util.ObjectToCounterMap;
import com.aliasi.util.Strings;

import com.aliasi.chunk.Chunker;
import com.aliasi.tokenizer.*;
import com.sun.jna.Library;
import com.sun.jna.Native;
public class CompareC {
	public List<Distance> getComparelist(File testfile, String filepath)
			throws IOException {
		File dir = new File(filepath);
		// 打开存放文件的地址，并将地址中的文件存入List
		ArrayList<Document> docs = new ArrayList<Document>();
		for (File file : dir.listFiles()) {
			Document doc = new Document(file);
			docs.add(doc);
		}
		int total = docs.size();
		Document testdoc = new Document(testfile);
		ArrayList<Distance> comparelist = new ArrayList<Distance>();
		for (int i = 0; i < total; i++) {
			comparelist.add(new Distance(testdoc.mFile.getName(),
					docs.get(i).mFile.getName(), cos_similarity(testdoc, docs
							.get(i))));
		}
		Collections.sort(comparelist, new Comparator<Distance>() {

			@Override
			public int compare(Distance o1, Distance o2) {
				// TODO Auto-generated method stub
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return comparelist;

	}
	public Boolean getLanguage(String filepath) throws IOException{
		File dir = new File(filepath);
		int language = 0;//英文是0，中文是1
		// 打开存放文件的地址，并将地址中的文件存入List
		for (File file : dir.listFiles()) {
			Document doc = new Document(file);
			language = doc.language;
		}
		if(language == 0){
			return false;
		}
		else{
			return true;
		}
		
	}

	public Map<Integer, List<Distance>> getDisMap(String filepath)
			throws Exception {
		File dir = new File(filepath);
		// 打开存放文件的地址，并将地址中的文件存入List
		ArrayList<Document> docs = new ArrayList<Document>();
		for (File file : dir.listFiles()) {
			Document doc = new Document(file);
			docs.add(doc);
		}
		int total = docs.size();

		Document first, second;
		Map<Integer, List<Distance>> imap = new HashMap<Integer, List<Distance>>();
		for (int i = 0; i < total; i++) {
			ArrayList<Distance> compareList = new ArrayList<Distance>();
			for (int j = 0; j < total; j++) {
				if (i != j) {
					first = docs.get(i);
					second = docs.get(j);
					// 将两篇文档的相似度计算出来放入相似度列表
					compareList.add(new Distance(first.mFile.getName(),
							second.mFile.getName(), cos_similarity(first,
									second)));

				}
			}
			boolean noSwap;
			Distance temp;
			// 将comparelist中的相似度进行排序（从大到小），越大意义为距离最小。
			int compareListSize = compareList.size();
			for (int m = 1; m < compareListSize; m++) {
				noSwap = true;
				for (int n = compareListSize - 1; n > m - 1; n--) {
					if (compareList.get(n).getValue() > compareList.get(n - 1)
							.getValue()) {
						temp = compareList.get(n);
						compareList.set(n, compareList.get(n - 1));
						compareList.set(n - 1, temp);
						noSwap = false;
					}
				}
				if (noSwap)
					break;
			}
			imap.put(i, compareList);
		}

		return imap;
	}

	// 挑选文件对应相应的簇
	public void printClass(Map<Integer, List<String>> conn, String filepath, Integer times)
			throws IOException {
		File dir = new File(filepath);
		// 打开存放文件的地址，并将地址中的文件存入List
		ArrayList<Document> docs = new ArrayList<Document>();
		for (File file : dir.listFiles()) {
			Document doc = new Document(file);
			docs.add(doc);
		}
		Object[] classes = conn.keySet().toArray();
		int k = 1;
		for (int m = 0; m < classes.length; m++) {
			System.out.println("第" + k + "簇");
			if (conn.get(classes[m]) != null)
				for (int n = 0; n < conn.get(classes[m]).size(); n++) {
					for (int i = 0; i < docs.size(); i++) {
						if (conn.get(classes[m]).get(n).equals(
								docs.get(i).mFile.getName())) {
							String path = new File("").getAbsolutePath()
									+ File.separator + "\\docs\\" + times + "\\"+k+"\\"
									+ docs.get(i).mFile.getName();
							File fileTem = new File(path);
							if (!fileTem.exists())
								fileTem.getParentFile().mkdirs();
							fileTem.createNewFile();
							// FileReader fr = new
							// FileReader(docs.get(i).mFile);
							// InputStreamReader isr = new InputStreamReader(
							// new FileInputStream(docs.get(i).mFile),
							// "UTF-8");
							// BufferedReader reader = new BufferedReader(fr);
							String content = Files.readFromFile(
									docs.get(i).mFile, Strings.UTF8);
							appendMethodB(path, content);
						}
					}
				}
			k = k + 1;
		}
	}

	public Set<String> extractAKeywords(int k, String filepath)
			throws IOException {
		File dir = new File(filepath);
		// 打开存放文件的地址，并将地址中的文件存入List
		Set<String> keyset = new HashSet<String>();
		// int total = docs.size();
		for (File file : dir.listFiles()) {
			FileReader fr = new FileReader(file);
			InputStreamReader isr = new InputStreamReader(
					new FileInputStream(file),
					"UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			String temp;
			String text = null;
			while((temp = reader.readLine())!= null){
				text = text + temp;
			}
				
				String nativeByte = CLibrary.Instance.NLPIR_GetKeyWords(text, 10,
						false);
				String[] ntemp = nativeByte.split("#");
				for (int i = 0; i < ntemp.length; i++) {
					if ((ntemp[i].equals("null")) == false) {
						// System.out.print(ntemp[i]+"#");
						keyset.add(ntemp[i]);
					}
				}

			
		}
		Iterator<String> kit = keyset.iterator();
		while (kit.hasNext()) {
			System.out.print(kit.next() + "#");
		}
		System.out.println();
		return keyset;

	}

	// 根据已有类别对原始文档进行再度分类
	public void classify(Map<Integer, List<String>> conn, String filepath)
			throws IOException {
		File dir = new File(filepath);
		// 打开存放文件的地址，并将地址中的文件存入List
		ArrayList<Document> docs = new ArrayList<Document>();
		for (File file : dir.listFiles()) {
			Document doc = new Document(file);
			docs.add(doc);
		}
		System.out.println("docs1.size()" + docs.size());
		// 把分好的类的文件提取出来
		List<List<Document>> connlist = new ArrayList<List<Document>>();
		Object[] classes = conn.keySet().toArray();
		for (int m = 0; m < classes.length; m++) {
			List<Document> ilist = new ArrayList<Document>();
			if (conn.get(classes[m]) != null)
				for (int n = 0; n < conn.get(classes[m]).size(); n++) {
					Iterator<Document> it = docs.iterator();
					while (it.hasNext()) {
						Document temp = it.next();
						if (conn.get(classes[m]).get(n).equals(
								temp.mFile.getName())) {
							ilist.add(temp);
							it.remove();
							docs.remove(temp);
						}
					}
				}
			connlist.add(ilist);
		}
		System.out.println("docs2.size()" + docs.size());
		// 开始计算每个文档与训练文档的相似度
		for (int n = 0; n < docs.size(); n++) {
			List<TestDistance> tlist = new ArrayList<TestDistance>();
			for (int k = 0; k < connlist.size(); k++) {
				double amount = 0.0;
				for (int l = 0; l < connlist.get(k).size(); l++) {
					amount += cos_similarity(docs.get(n), connlist.get(k)
							.get(l));
				}
				TestDistance td = new TestDistance();
				td.setFilename(docs.get(n).mFile.getName());
				td.setC(k);
				td.setDistance(amount / connlist.get(k).size());
				tlist.add(td);
			}
			Collections.sort(tlist, new Comparator<TestDistance>() {

				@Override
				public int compare(TestDistance o1, TestDistance o2) {
					// TODO Auto-generated method stub
					return o1.getDistance().compareTo(o2.getDistance());
				}
			});
			// for(int temp=0;temp<tlist.size();temp++){
			// System.out.println(tlist.get(temp).getC()+"|"+tlist.get(temp).getDistance());
			// }
			if (tlist.size() > 0)
				connlist.get(tlist.get(tlist.size() - 1).getC()).add(
						docs.get(n));
			// conn.get(tlist.get(tlist.size()-1).getC()).add(tlist.get(tlist.size()-1).getFilename());
		}
		// 清空conn，重新装填
		conn.clear();
		for (int number = 0; number < connlist.size(); number++) {
			List<String> stemp = new ArrayList<String>();
			for (int u = 0; u < connlist.get(number).size(); u++) {
				stemp.add(connlist.get(number).get(u).mFile.getName());
			}
			conn.put(number, stemp);
		}
	}

	static double cos_similarity(Document doc1, Document doc2) {
		double oneMinusCosine = doc1.cosine(doc2);
		if (oneMinusCosine > 1.0)
			return 1.0;
		else if (oneMinusCosine < 0.0)
			return 0.0;
		else
			return oneMinusCosine;
	}

	public static void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter fw = new FileWriter(fileName, true);
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF-8");
			BufferedWriter writer = new BufferedWriter(out);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class Document {
		final File mFile;
		final char[] mText; // don't really need to store
		final ObjectToCounterMap<String> mTokenCounter = new ObjectToCounterMap<String>();
		double mLength = 0.0;
		int language = 0;//英文设为0，中文设为1
		Document(File file) throws IOException {
			mFile = file; // includes name
			mText = Files.readCharsFromFile(file, Strings.UTF8);
			String text = Files.readFromFile(file, Strings.UTF8);
			String argu = "C:\\Users\\BOB.BOB-PC\\Desktop\\JnaTest_NLPIR";
			// String system_charset = "GBK";//GBK----0
			String system_charset = "UTF-8";
			int charset_type = 1;

			int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type,
					"0");
			String nativeBytes = null;

			if (0 == init_flag) {
				nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
				System.err.println("初始化失败！fail reason is " + nativeBytes);
				return;
			}
			// 中文分词用
			if (isChinese(text) == true) {
				language = 1;
				StringReader sr = new StringReader(text);
				IKSegmenter ik = new IKSegmenter(sr, true);
				Lexeme lex = null;
				while ((lex = ik.next()) != null) {
					System.out.print(lex.getLexemeText() + "|");
					mTokenCounter.increment(lex.getLexemeText());
				}
				mLength = length(mTokenCounter);
			}
			// 英文分词用
			else {
				language = 0;
				Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(mText, 0,
						mText.length);
				String token;
				String content = null;
				while ((token = tokenizer.nextToken()) != null) {
					content += "[" + token + "]";
					mTokenCounter.increment(token.toLowerCase());
				}
				mLength = length(mTokenCounter);
			}
		}

		double cosine(Document thatDoc) {
			return product(thatDoc) / (mLength * thatDoc.mLength);
		}

		double product(Document thatDoc) {
			double sum = 0.0;
			for (String token : mTokenCounter.keySet()) {
				int count = thatDoc.mTokenCounter.getCount(token);
				if (count == 0)
					continue;
				// tf = sqrt(count); sum += tf1 * tf2
				sum += Math.sqrt(count * mTokenCounter.getCount(token));
			}
			return sum;
		}

		public String toString() {
			return mFile.getParentFile().getName() + "/" + mFile.getName();
		}

		static double length(ObjectToCounterMap<String> otc) {
			double sum = 0.0;
			for (Counter counter : otc.values()) {
				double count = counter.doubleValue();
				sum += count; // tf =sqrt(count); sum += tf * tf
			}
			return Math.sqrt(sum);
		}
	}

	static final TokenizerFactory TOKENIZER_FACTORY = tokenizerFactory();

	static TokenizerFactory tokenizerFactory() {
		TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;
		factory = new LowerCaseTokenizerFactory(factory);
		factory = new EnglishStopTokenizerFactory(factory);
		factory = new PorterStemmerTokenizerFactory(factory);
		return factory;
	}
	public interface CLibrary extends Library {
		File file = new File("lib\\win64\\NLPTR");
		String path = new File("lib\\win64").getAbsolutePath() + File.separator
				+ "NLPTR.dll";
		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native
				.loadLibrary(
						"C:\\Users\\BOB.BOB-PC\\Desktop\\JnaTest_NLPIR\\lib\\win64\\NLPIR.dll",
						CLibrary.class);

		public int NLPIR_Init(String sDataPath, int encoding,
				String sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
				boolean bWeightOut);

		public int NLPIR_AddUserWord(String sWord);// add by qp 2008.11.10

		public int NLPIR_DelUsrWord(String sWord);// add by qp 2008.11.10

		public String NLPIR_GetLastErrorMsg();

		public void NLPIR_Exit();
	}
	// 根据Unicode编码完美的判断中文汉字和符号
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	// 完整的判断中文汉字和符号
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

}