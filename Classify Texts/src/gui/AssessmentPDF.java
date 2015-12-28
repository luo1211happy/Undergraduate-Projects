package gui;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import classification.ClassifyA;
import cluster.CompareA;

import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class AssessmentPDF {
	private Integer atimes;
	private PictureA picture;
	private String filepath = null;
	private String path;
	private Document document;
	private PdfWriter writer;
	private BaseFont bf;
	private Font fontChinese1;
	private Font fontChinese2;
	private Font fontChinese3;
	private Chapter chapter1;
	private Paragraph title1;
	private Paragraph title2;
	private Paragraph title3;
	private Paragraph title4;
	private Section section1;
	private Section section2;
	private Section section3;

	public AssessmentPDF(PictureA picture, String filepath, Integer atimes)
			throws Exception {
		this.picture = picture;
		this.filepath = filepath;
		this.atimes = atimes;
		// document.newPage();
		// 新建章节
		// 需要添加iTextAsian.jar包才能支持中文
		bf = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
		fontChinese1 = new Font(bf, 18, Font.BOLDITALIC, new Color(0, 0, 255));
		title1 = new Paragraph("评论分类", fontChinese1);
		chapter1 = new Chapter(title1, 1);
		chapter1.setNumberDepth(0);
		fontChinese2 = new Font(bf, 18, Font.BOLD, new Color(255, 0, 0));
		fontChinese3 = new Font(bf, 14, Font.BOLD, new Color(0, 0, 0));
		System.out.println("ap times:" + atimes);
	}

	public void writeSimilarity() throws Exception {

		if (filepath != null) {
			File dir = new File(filepath);
			if (dir.exists() && dir.isDirectory()) {
				path = new File("").getAbsolutePath() + "\\print\\" + atimes
						+ "\\" + "评论分类.pdf";
				File fileTem = new File(path);
				if (!fileTem.exists()) {
					fileTem.getParentFile().mkdirs();
				}
				// 新建document对象 第一个参数是页面大小。接下来的参数分别是左、右、上和下页边距。
				document = new Document(PageSize.A4, 20, 20, 20, 20);
				// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
				writer = PdfWriter.getInstance(document, new FileOutputStream(
						path));
				// 打开文件
				document.open();
				// 标题
				document.addTitle("评论分类");

				// 作者
				document.addAuthor("肖乐");

				// 主题
				document.addSubject("显示如何对评论进行分类");
				document.addKeywords("评论, 分类");
				document.addCreator("使用iText jar包");
				title2 = new Paragraph("文档相似度", fontChinese2);
				section1 = chapter1.addSection(title2);
				String imagepath = picture.printImage(picture, atimes);
				Image image = Image.getInstance(imagepath);
				image.scaleAbsolute(picture.getWidth() / 2,
						picture.getHeight() / 2);
				section1.add(image);
				document.add(chapter1);
				// 关闭文档
				document.close();
			} else {
				JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void writeCategory() throws Exception {
		if (filepath != null) {
			File dir = new File(filepath);
			if (dir.exists() && dir.isDirectory()) {
				path = new File("").getAbsolutePath() + "\\print\\" + atimes
						+ "\\" + "评论分类.pdf";
				File fileTem = new File(path);
				if (!fileTem.exists()) {
					fileTem.getParentFile().mkdirs();
				}
				// 新建document对象 第一个参数是页面大小。接下来的参数分别是左、右、上和下页边距。
				document = new Document(PageSize.A4, 20, 20, 20, 20);
				// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
				writer = PdfWriter.getInstance(document, new FileOutputStream(
						path));
				// 打开文件
				document.open();
				// 标题
				document.addTitle("评论分类");

				// 作者
				document.addAuthor("肖乐");

				// 主题
				document.addSubject("显示如何对评论进行分类");
				document.addKeywords("评论, 分类");
				document.addCreator("使用iText jar包");
				title3 = new Paragraph("分类结果", fontChinese2);
				section2 = chapter1.addSection(title3);
				title4 = new Paragraph("关键词", fontChinese2);
				section3 = chapter1.addSection(title4);
				Map<Integer, List<String>> nrconn = new HashMap<Integer, List<String>>();
				ClassifyA ca = new ClassifyA();
				nrconn = ca.getAssessment(filepath, nrconn, atimes);
				Object[] classes2 = nrconn.keySet().toArray();
				for (int i = 0; i < classes2.length; i++) {
					Paragraph someSectionText = new Paragraph("第" + (i + 1)
							+ "类", fontChinese3);
					section2.add(someSectionText);
					someSectionText = new Paragraph("size: "
							+ nrconn.get(classes2[i]).size(), fontChinese3);
					section2.add(someSectionText);
					String content = "";
					for (int j = 0; j < nrconn.get(classes2[i]).size(); j++) {
						content = content + nrconn.get(classes2[i]).get(j)
								+ "|";
					}
					someSectionText = new Paragraph(content, fontChinese3);
					section2.add(someSectionText);
					String parentfile = new File("").getAbsolutePath()
							+ "\\assessments\\" + atimes + "\\" + (i + 1);
					CompareA comparea = new CompareA();
					Set<String> keyset = comparea.extractAKeywords(i,
							parentfile);
					Iterator<String> kit = keyset.iterator();
					// 显示关键词
					someSectionText = new Paragraph("第" + (i + 1) + "类",
							fontChinese3);
					section3.add(someSectionText);
					String keywords = "";
					while (kit.hasNext()) {
						keywords = keywords + kit.next() + "#";
					}
					someSectionText = new Paragraph(keywords, fontChinese3);
					section3.add(someSectionText);
				}
				document.add(chapter1);
				document.close();
			} else {
				JOptionPane.showMessageDialog(null, "文件夹路径输入错误", "警告",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "没有输入文件夹路径", "警告",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void openFile() throws IOException {
		Runtime.getRuntime().exec("explorer " + path);
	}

}
