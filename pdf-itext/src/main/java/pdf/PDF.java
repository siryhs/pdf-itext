package pdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Test;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class PDF {

	/**
	 * 生成一个PDF
	 */
	@Test
	public void createPDF() {
		// Step 1—Create a Document.
		Document document = new Document();
		// Step 2—Get a PdfWriter instance.
		try {
			PdfWriter.getInstance(document, new FileOutputStream("createSamplePDF.pdf"));
			// Step 3—Open the Document.
			document.open();
			// Step 4—Add content.
			document.add(new Paragraph("Hello World送到附近岁的老将"));
			// Step 5—Close the Document.
			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 页面大小,页面背景色,页边空白,Title,Author,Subject,Keywords
	 */
	@Test
	public void task() {
		// 页面大小
		Rectangle rect = new Rectangle(PageSize.B5.rotate());
		// 页面背景色
		rect.setBackgroundColor(BaseColor.ORANGE);

		Document doc = new Document(rect);

		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(doc, new FileOutputStream("task.pdf"));
			// PDF版本(默认1.4)
			writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
			// 文档属性
			doc.addTitle("Title@sample");
			doc.addAuthor("Author@rensanning");
			doc.addSubject("Subject@iText sample");
			doc.addKeywords("Keywords@iText");
			doc.addCreator("Creator@iText");

			// 页边空白
			doc.setMargins(10, 20, 30, 40);

			doc.open();
			doc.add(new Paragraph("Hello World"));
			doc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void setPassword() {
		Document doc = new Document();
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(doc, new FileOutputStream("setPassword.pdf"));
			// 设置密码为："World"
			writer.setEncryption("Hello".getBytes(), "World".getBytes(), PdfWriter.ALLOW_SCREENREADERS,
					PdfWriter.STANDARD_ENCRYPTION_128);
			doc.open();
			doc.add(new Paragraph("Hello World 倒萨路口附近"));
			doc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void setPage(){
		Document document = new Document();
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream("setPage.pdf"));
			document.open();
			document.add(new Paragraph("First page"));  
			//document.add(new Paragraph(Document.getVersion()));  
			document.newPage();  
			writer.setPageEmpty(false);  
			document.newPage();  
			document.add(new Paragraph("New page"));  
			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
