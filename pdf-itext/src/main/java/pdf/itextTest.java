package pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class itextTest {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	private static DecimalFormat df = new DecimalFormat("#.#");
	private static String imgStr = null;
	private static String path = System.getProperty("user.dir");
	private static String imgFile = path + File.separator + "test.png";// 待处理的图片

	public static void main(String[] args) throws Exception {
		List<Map<String, String>> list = getExcle(path + File.separator + "TestData.xlsx");
		for (Map<String, String> map : list) {
			fromPDFTempletToPdfWithValue(map, path + File.separator + "testForm.pdf");
		}
	}

	// 图片转化成base64字符串
	public static String GetImageStr() {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		if (imgStr == null) {
			FileInputStream in = null;
			byte[] data = null;
			// 读取图片字节数组
			try {
				in = new FileInputStream(imgFile);
				data = new byte[in.available()];
				in.read(data);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			imgStr = encoder.encode(data);// 返回Base64编码过的字节数组字符串
		}
		return imgStr;
	}

	// base64字符串转化成图片
	public static boolean GenerateImage(String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = "d://222.jpg";// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void fromPDFTempletToPdfWithValue(Map<String, String> map, String path) {
		File file = new File(path.trim());
		String fileName = file.getName();
		String name = fileName.substring(0, fileName.indexOf("."));
		FileOutputStream fos = null;
		try {
			PdfReader reader = new PdfReader(path);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfStamper ps = new PdfStamper(reader, bos);
			AcroFields s = ps.getAcroFields();
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
			s.addSubstitutionFont(bfChinese);
			if (name.equals("testForm")) {
				s.setField("name", map.get("name"));
				s.setField("code", map.get("code"));
				s.setField("time", map.get("time"));
				s.setField("address", map.get("address"));
				s.setField("mobile", map.get("mobile"));
				s.setField("tupian", GetImageStr());
			} else {
				return;
			}
			ps.setFormFlattening(true);
			ps.close();
			File out = new File(file.getParent(), name);
			if (!out.exists()) {
				out.mkdirs();
			}
			fos = new FileOutputStream(
					out.getPath() + File.separator + map.get("number") + "_" + map.get("name") + ".pdf");
			fos.write(bos.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取excel表格的数据
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 */
	@SuppressWarnings("resource")
	public static List<Map<String, String>> getExcle(String path)
			throws FileNotFoundException, IOException, InvalidFormatException {
		InputStream in = new FileInputStream(new File(path));
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Workbook workbook = null;
		if (!in.markSupported()) {
			in = new PushbackInputStream(in, 8);
		}

		if (POIFSFileSystem.hasPOIFSHeader(in)) {
			workbook = new HSSFWorkbook(in);
		} else if (POIXMLDocument.hasOOXMLHeader(in)) {
			workbook = new XSSFWorkbook(OPCPackage.open(in));
		} else {
			System.out.println("你的excel版本目前解析不了！");
			return null;
		}
		// 加载上传的文件
		// 获取文件的第一个sheet页面
		Sheet sheet = workbook.getSheetAt(0);
		// 遍历所有的行
		for (Row row : sheet) {
			Map<String, String> map = new HashMap<String, String>();
			int rowNum = row.getRowNum();
			// 将第一行空去
			if (rowNum == 0) {
				continue;
			}
			String number = getCellStringValue(row.getCell(0));
			String name = getCellStringValue(row.getCell(1));
			String code = getCellStringValue(row.getCell(2));
			String time = getCellStringValue(row.getCell(3));
			String address = getCellStringValue(row.getCell(4));
			String mobile = getCellStringValue(row.getCell(5));
			map.put("number", number);
			map.put("name", name);
			map.put("code", code);
			map.put("time", time);
			map.put("address", address);
			map.put("mobile", mobile);
			list.add(map);
		}
		return list;
	}

	/**
	 * 单元格数据转换
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellStringValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		String cellValue = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:// 字符串类型
			cellValue = cell.getStringCellValue().trim();
			if (cellValue.equals("") || cellValue.trim().length() <= 0) {
				cellValue = "";
			}
			break;
		case Cell.CELL_TYPE_NUMERIC: // 数值类型
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
			} else {
				cellValue = df.format(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK:
			cellValue = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			cellValue = String.valueOf(cell.getErrorCellValue());
			break;
		default:
			break;
		}
		return cellValue;
	}
}
